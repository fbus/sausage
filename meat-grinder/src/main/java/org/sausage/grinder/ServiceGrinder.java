package org.sausage.grinder;

import java.net.URL;

import org.sausage.model.document.CompositeType;
import org.sausage.model.service.ACLs;
import org.sausage.model.service.AdapterService;
import org.sausage.model.service.Audit;
import org.sausage.model.service.Audit.IncludePipeline;
import org.sausage.model.service.CacheBehavior;
import org.sausage.model.service.FlowService;
import org.sausage.model.service.ISService;

import com.softwareag.util.IDataMap;
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
import com.wm.lang.ns.NSService;
import com.wm.pkg.art.ns.AdapterServiceNode;
import com.wm.util.Base64;
import com.wm.util.Values;

public class ServiceGrinder {

    public static ISService convert(BaseService baseSvc) {
        ISService service;
        if (baseSvc instanceof FlowSvcImpl) {
            FlowSvcImpl se = (FlowSvcImpl) baseSvc;

            FlowService flowService = new FlowService();
            service = flowService;

            FlowGrinder.addChrildren(flowService.rootStep, se.getFlowRoot().getNodes());
        } else if (baseSvc instanceof JavaService) {
            JavaService javaSvc = (JavaService) baseSvc;
            org.sausage.model.service.JavaService javaService = new org.sausage.model.service.JavaService();
            service = javaService;
            PackageFS store = (PackageFS) ((Package) javaSvc.getPackage()).getStore();
            Values fragIData = (Values) store.getFromNode(baseSvc.getNSName(), PackageStore.JAV_FILE);

            Object value = fragIData.get("body");
            if (value != null) {
                String source = Base64.decodeUTF8(value.toString());
                javaService.methodSource = source;
            }
        } else if (baseSvc instanceof AdapterServiceNode) {
            AdapterServiceNode adapterService = (AdapterServiceNode) baseSvc;
            AdapterService adp = new AdapterService();
            IData metadata = adapterService.getMetadataPropertySettings();
            adp.metadata = new IDataMap(metadata);

            service = adp;
        } else {

            Class<?> c = baseSvc.getClass();
            URL resource = c.getResource(c.getSimpleName() + ".class");
            throw new IllegalArgumentException("unhandled type : " + baseSvc + " - " + baseSvc.getClass() + " " + resource);
        }

        service.fullName = baseSvc.getNSName().getFullName();
        service.packageName = baseSvc.getPackage().getName();
        service.audit = new Audit();

        NSRecord output = baseSvc.getSignature().getOutput();
        NSRecord input = baseSvc.getSignature().getInput();
        CompositeType inputSignature = TypeGrinder.convert(input);
        CompositeType outputSignature = TypeGrinder.convert(output);
        service.cacheBehavior = getCacheBehavior(baseSvc);
        service.acl = getACLs(baseSvc);

        service.audit.includePipeline = getIncludePipeline(baseSvc);

        return service;
    }

    private static ACLs getACLs(BaseService se) {
        final ACLs acls;
        String readAcl = ACLManager.defaultSystemACL.equals(se.getReadAclGroup()) ? null : se.getReadAclGroup();
        String writeAcl = ACLManager.defaultSystemACL.equals(se.getWriteAclGroup()) ? null : se.getWriteAclGroup();
        String execAcl = ACLManager.defaultSystemACL.equals(se.getAclGroup()) ? null : se.getAclGroup();

        if (readAcl == null && writeAcl == null && execAcl == null) {
            acls = null;
        } else {
            acls = new ACLs();
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
