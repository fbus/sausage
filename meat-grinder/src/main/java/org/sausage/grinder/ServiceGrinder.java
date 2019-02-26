package org.sausage.grinder;

import org.sausage.grinder.util.SimpleIDataMap;
import org.sausage.model.adapter.AdapterService;
import org.sausage.model.document.CompositeType;
import org.sausage.model.service.AccessControlList;
import org.sausage.model.service.Audit;
import org.sausage.model.service.Audit.IncludePipeline;
import org.sausage.model.service.CacheBehavior;
import org.sausage.model.service.FlowService;
import org.sausage.model.service.ISService;
import org.sausage.model.service.Signature;
import org.sausage.model.service.UnhandledService;
import org.sausage.model.service.WsdConnectorService;

import com.wm.app.b2b.server.ACLManager;
import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.FlowSvcImpl;
import com.wm.app.b2b.server.JavaService;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageFS;
import com.wm.app.b2b.server.PackageStore;
import com.wm.data.IData;
import com.wm.lang.ns.AuditSettings;
import com.wm.lang.ns.NSRecord;
import com.wm.lang.ns.NSRecordRef;
import com.wm.lang.ns.NSService;
import com.wm.pkg.art.ns.AdapterServiceNode;
import com.wm.util.Base64;
import com.wm.util.Values;

public class ServiceGrinder {

    public static ISService convert(BaseService baseSvc) {
        final ISService service;
        if (baseSvc instanceof FlowSvcImpl) {
            FlowSvcImpl se = (FlowSvcImpl) baseSvc;
            if (se.getParentWSD() != null && !se.isModifiable()) {
                WsdConnectorService connector = new WsdConnectorService();
                connector.parentWsd = se.getParentWSD();
                connector.externalUrl = se.getOriginURI();
                service = connector;
                // step details are useless for connectors...
            } else {
                FlowService flowService = new FlowService();
                service = flowService;
                if (se.getFlowRoot() != null) {
                    FlowGrinder.addChrildren(flowService.rootStep, se.getFlowRoot().getNodes());
                }

            }

        } else if (baseSvc instanceof JavaService) {
            JavaService javaSvc = (JavaService) baseSvc;
            org.sausage.model.service.JavaService javaService = new org.sausage.model.service.JavaService();
            service = javaService;
            if (javaSvc.hasFrag()) {
                javaService.methodSource = getJavaFragSource(baseSvc, javaSvc);
            } else {
                javaService.methodName = javaSvc.getClassName() + "." + javaSvc.getMethodName();
            }
        } else if (baseSvc instanceof AdapterServiceNode) {
            AdapterServiceNode adapterService = (AdapterServiceNode) baseSvc;
            AdapterService adp = new AdapterService();
            adp.adapterTypeName = adapterService.getAdapterTypeName();
            IData metadata = adapterService.getMetadataPropertySettings();
            adp.metadata = new SimpleIDataMap(metadata);

            service = adp;
        } else {
            UnhandledService svc = new UnhandledService();
            svc.unhandledType = baseSvc.getServiceType().toString();
            svc.rawData = new SimpleIDataMap(baseSvc.getAsData());
            service = svc;
        }

        service.setName(baseSvc.getNSName().getFullName());
        service.setPackageName(baseSvc.getPackage().getName());

        service.audit = new Audit();

        service.signature = getSignature(baseSvc);

        service.cacheBehavior = getCacheBehavior(baseSvc);
        service.acl = getACLs(baseSvc);

        service.audit.includePipeline = getIncludePipeline(baseSvc);

        return service;
    }

    private static String getJavaFragSource(BaseService baseSvc, JavaService javaSvc) {
        PackageFS store = (PackageFS) ((Package) javaSvc.getPackage()).getStore();
        Values fragIData = (Values) store.getFromNode(baseSvc.getNSName(), PackageStore.JAV_FILE);
        String source = null;
        Object value = fragIData == null ? null : fragIData.get("body");
        if (value != null) {
            source = Base64.decodeUTF8(value.toString());
        } else {
            System.out.println("" + baseSvc.getNSName());
        }
        return source;
    }

    private static Signature getSignature(BaseService baseSvc) {

        Signature signature = new Signature();

        NSRecord output = null;
        NSRecord input = null;
        if (baseSvc.getSignature() != null) {
            output = baseSvc.getSignature().getOutput();
            input = baseSvc.getSignature().getInput();
        }
        if (input == null) {
            signature.input = new CompositeType();
        } else if (input instanceof NSRecordRef) {
            signature.input = TypeGrinder.convertReference((NSRecordRef) input);
        } else if (input instanceof NSRecord) {
            signature.input = TypeGrinder.convertRecord(input);
        } else {
            throw new IllegalArgumentException("unhandled input case : " + input);
        }

        if (output == null) {
            signature.output = new CompositeType();
        } else if (output instanceof NSRecordRef) {
            signature.output = TypeGrinder.convertReference((NSRecordRef) output);
        } else if (output instanceof NSRecord) {
            signature.output = TypeGrinder.convertRecord(output);
        } else {
            throw new IllegalArgumentException("unhandled output case : " + output);
        }

        return signature;
    }

    private static AccessControlList getACLs(BaseService se) {
        final AccessControlList acls;
        String readAcl = ACLManager.defaultSystemACL.equals(se.getReadAclGroup()) ? null : se.getReadAclGroup();
        String writeAcl = ACLManager.defaultSystemACL.equals(se.getWriteAclGroup()) ? null : se.getWriteAclGroup();
        String execAcl = ACLManager.defaultSystemACL.equals(se.getAclGroup()) ? null : se.getAclGroup();

        if (readAcl == null && writeAcl == null && execAcl == null) {
            acls = null;
        } else {
            acls = new AccessControlList();
            acls.readAcl = readAcl;
            acls.writeAcl = writeAcl;
            acls.execAcl = execAcl;
        }
        return acls;
    }

    private static CacheBehavior getCacheBehavior(BaseService se) {
        CacheBehavior cacheBehavior = null;
        if (se.isCacheEnabled()) {
            cacheBehavior = new CacheBehavior();
            cacheBehavior.expiryInMinutes = se.getCacheTTL();
            cacheBehavior.prefetch = se.isPrefetchEnabled();
            if (se.isPrefetchEnabled()) {
                cacheBehavior.prefetchActivation = se.getPrefetchLevel();
            }
        }
        return cacheBehavior;
    }

    private static IncludePipeline getIncludePipeline(NSService se) {
        final IncludePipeline includePipeline;
        switch (se.getAuditSettings().isDocumentAuditEnabled()) {
            case AuditSettings.PIPELINE_LOGALWAYS:
                includePipeline = IncludePipeline.ALWAYS;
                break;
            case AuditSettings.PIPELINE_ONERROR:
                includePipeline = IncludePipeline.ON_ERROR_ONLY;
                break;
            default:
                includePipeline = IncludePipeline.NEVER;
                break;
        }
        return includePipeline;
    }

}
