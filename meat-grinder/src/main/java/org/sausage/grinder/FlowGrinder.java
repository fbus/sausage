package org.sausage.grinder;

import org.apache.commons.lang.StringUtils;
import org.sausage.grinder.util.RecordDiffer;
import org.sausage.grinder.util.ValueGrinder;
import org.sausage.model.document.CompositeType;
import org.sausage.model.step.Branch;
import org.sausage.model.step.CompositeStep;
import org.sausage.model.step.Exit;
import org.sausage.model.step.Exit.FromEnum;
import org.sausage.model.step.Invoke;
import org.sausage.model.step.Loop;
import org.sausage.model.step.MapStep;
import org.sausage.model.step.Repeat;
import org.sausage.model.step.Sequence;
import org.sausage.model.step.Step;
import org.sausage.model.step.invoke.InputMap;
import org.sausage.model.step.invoke.OutputMap;
import org.sausage.model.step.map.Copy;
import org.sausage.model.step.map.Drop;
import org.sausage.model.step.map.PipelineChanges;
import org.sausage.model.step.map.SetValue;
import org.sausage.model.step.map.Transformer;

import com.wm.app.b2b.server.ns.Namespace;
import com.wm.lang.flow.FlowBranch;
import com.wm.lang.flow.FlowElement;
import com.wm.lang.flow.FlowExit;
import com.wm.lang.flow.FlowInvoke;
import com.wm.lang.flow.FlowLoop;
import com.wm.lang.flow.FlowMap;
import com.wm.lang.flow.FlowMapCopy;
import com.wm.lang.flow.FlowMapDelete;
import com.wm.lang.flow.FlowMapInvoke;
import com.wm.lang.flow.FlowMapSet;
import com.wm.lang.flow.FlowRetry;
import com.wm.lang.flow.FlowSequence;
import com.wm.lang.ns.NSRecord;

public class FlowGrinder {

    public static void addChrildren(CompositeStep parent, FlowElement[] children) {
        if(children == null) {
            return;
        }
        for (FlowElement flowElement : children) {
            @SuppressWarnings("rawtypes")
            FlowElementConverter converter = ConverterEnum.valueOf(flowElement.getFlowType()).getConverter();
            @SuppressWarnings("unchecked")
            Step s = converter.convert(flowElement);

            parent.nodes.add(s);
            s.parent = parent; 
        }
        
    }

    
    public static interface FlowElementConverter<I extends FlowElement, O extends Step> {
        O convert(I in);
    }
    
    public static abstract class BaseFlowElementConverter<I extends FlowElement, O extends Step> implements FlowElementConverter<I, O> {
        
        protected void copyCommonAttributes(I flowElement, O result) {
            result.comment = StringUtils.defaultIfEmpty(flowElement.getComment(), null);
            result.label = StringUtils.defaultIfEmpty(flowElement.getDebugLabel(), null);
            result.disable = !flowElement.isEnabled();
            // TODO scope ?
        }
        
        @SuppressWarnings("unchecked")
		protected <T> T getStringProperty(FlowElement elt, String name) {
            return (T) elt.getValues().get(name);
        }
    }
    
    
    public static class MapConverter extends BaseFlowElementConverter<FlowMap, MapStep> {

        @Override
        public MapStep convert(FlowMap in) {
            MapStep result = new MapStep();
            copyCommonAttributes(in, result);
            
            inferPipelineChanges(in, result);
            
            MapConverter.convert(in, result);
            return result;
        }

		public static void inferPipelineChanges(FlowMap in, MapStep out) {
			NSRecord source = in.getSource(Namespace.current());
            NSRecord target = in.getTarget(Namespace.current());
            CompositeType before = source == null ? null : TypeGrinder.convertRecord(source);
            CompositeType after = target == null ? null : TypeGrinder.convertRecord(target);

            if(before != null && after != null) {
           		PipelineChanges changes = RecordDiffer.getChanges(before, after);
           		if(changes != null && changes.structureModified != null && changes.added != null) {
           			out.pipelineChanges = changes;
           		}
            }
		}
        
        public static void convert(FlowMap in, MapStep out) {
            addChrildren(out, in.getNodes());
        }

    }
    
    public static class MapCopyConverter extends BaseFlowElementConverter<FlowMapCopy, Copy> {
        
        @Override
        public Copy convert(FlowMapCopy in) {
            Copy result = new Copy();
            copyCommonAttributes(in, result);
            result.label = null; // can't put a label on this, and WM uselessly put "Link". 
            result.from = in.getParsedFrom().getPathDisplayString();
            result.to = in.getParsedTo().getPathDisplayString();
            return result;
        }
        
    }
    
    public static class MapSetConverter extends BaseFlowElementConverter<FlowMapSet, SetValue> {
        
		@Override
		public SetValue convert(FlowMapSet in) {
			SetValue result = new SetValue();
			copyCommonAttributes(in, result);
			result.label = null; // can't put a label on this, and WM uselessly put "Setter".
			
			result.to = in.getParsedPath().getPathDisplayString();
			result.value = ValueGrinder.convert(in.getInput());
			result.doNotOverwritePipelineValue = !in.isOverwrite();
			result.performVariableSubstitution = in.isVariables();

			return result;
		}
        
    }
    
    public static class MapDeleteConverter extends BaseFlowElementConverter<FlowMapDelete, Drop> {
        
        @Override
        public Drop convert(FlowMapDelete in) {
            Drop result = new Drop();
            copyCommonAttributes(in, result);
            result.label = null; // can't put a label on this, and WM uselessly put "Dropper". 
            result.path = in.getParsedPath().getPathDisplayString();
            return result;
        }
        
    }

    public static class SequenceConverter extends BaseFlowElementConverter<FlowSequence, Sequence> {

        @Override
        public Sequence convert(FlowSequence in) {
            Sequence result = new Sequence();
            copyCommonAttributes(in, result);
            
            String exitOn = getStringProperty(in, FlowSequence.KEY_SEQUENCE_EXITON);
            result.exitOn = Sequence.ExitOn.valueOf(exitOn);
            
            addChrildren(result, in.getNodes());
            return result;
        }

    }
    
    public static class LoopConverter extends BaseFlowElementConverter<FlowLoop, Loop> {
        
        @Override
        public Loop convert(FlowLoop in) {
            Loop result = new Loop();
            copyCommonAttributes(in, result);
            
            result.inputArray = StringUtils.defaultIfEmpty(in.getInArray(), null);
            result.outputArray = StringUtils.defaultIfEmpty(in.getOutArray(), null);
            
            addChrildren(result, in.getNodes());
            return result;
        }
        
    }
    
    public static class RetryConverter extends BaseFlowElementConverter<FlowRetry, Repeat> {
        
        @Override
        public Repeat convert(FlowRetry in) {
            Repeat result = new Repeat();
            copyCommonAttributes(in, result);
            
            
            String retryInterval = getStringProperty(in, FlowRetry.KEY_RETRY_BACKOFF);
            String count = getStringProperty(in, FlowRetry.KEY_RETRY_COUNT);
            String repeatOn = getStringProperty(in, FlowRetry.KEY_RETRY_LOOPON);
            
            result.retryInterval = StringUtils.defaultIfEmpty(retryInterval, null);
            result.count = StringUtils.defaultIfEmpty(count, null);
            result.repeatOnFailure = FlowExit.EXIT_FAILURE.equals(repeatOn);
            
            addChrildren(result, in.getNodes());
            return result;
        }
        
    }
    
    public static class BranchConverter extends BaseFlowElementConverter<FlowBranch, Branch> {
        
        @Override
        public Branch convert(FlowBranch in) {
            Branch result = new Branch();
            copyCommonAttributes(in, result);
            String switchProperty = getStringProperty(in, FlowBranch.KEY_BRANCH_SWITCH);
            result.switchProperty = StringUtils.defaultIfEmpty(switchProperty, null);
            result.evaluateLabels = in.isCondition();
            
            addChrildren(result, in.getNodes());
            return result;
        }
        
    }
    
    public static class InvokeConverter extends BaseFlowElementConverter<FlowInvoke, Invoke> {
        
        @Override
        public Invoke convert(FlowInvoke in) {
            Invoke result = new Invoke();
            copyCommonAttributes(in, result);
            InvokeConverter.convert(in, result);
            return result;
        }
        
        public static void convert(FlowInvoke in, Invoke out) {
            out.serviceName = in.getService().getFullName();
            NSRecord source = null;
            NSRecord target = null;
            if(in.getInputMap() != null) {
                out.input = new InputMap();
                out.input.parentInvoke = out;
                MapConverter.convert(in.getInputMap(), out.input);
                
                source = in.getInputMap().getSource(Namespace.current());
            }
            if(in.getOutputMap() != null) {
                out.output = new OutputMap();
                out.output.parentInvoke = out;
                MapConverter.convert(in.getOutputMap(), out.output);
                
                target = in.getOutputMap().getTarget(Namespace.current());
            }
            CompositeType before = source == null ? null : TypeGrinder.convertRecord(source);
            CompositeType after = target == null ? null : TypeGrinder.convertRecord(target);

            if(!(out instanceof Transformer) && before != null && after != null) {
           		PipelineChanges changes = RecordDiffer.getChanges(before, after);
                if(changes != null && changes.structureModified != null && changes.added != null) {
                    out.pipelineChanges = changes;
                }
            }
        }
    }
    
    public static class MapInvokeConverter extends BaseFlowElementConverter<FlowMapInvoke, Transformer> {
        
        @Override
        public Transformer convert(FlowMapInvoke in) {
            Transformer result = new Transformer();
            copyCommonAttributes(in, result);

            InvokeConverter.convert(in, result);
            return result;
        }
        
    }
    
    public static class ExitConverter extends BaseFlowElementConverter<FlowExit, Exit> {
        
        @Override
        public Exit convert(FlowExit in) {
            Exit result = new Exit();
            copyCommonAttributes(in, result);
            
            String failureMessage = getStringProperty(in, FlowExit.KEY_EXIT_FAILURE_MESSAGE);
            String from = getStringProperty(in, FlowExit.KEY_EXIT_FROM);
            String signal = getStringProperty(in, FlowExit.KEY_EXIT_SIGNAL);
            
            result.failureMessage = StringUtils.defaultIfEmpty(failureMessage, null);
            result.signalFailure = FlowExit.EXIT_FAILURE.equals(signal);

            result.from = convertExitFrom(from);
            
            return result;
        }

        private Exit.FromEnum convertExitFrom(String from) {
            final Exit.FromEnum fromEnum;
            if("$parent".equals(from)) {
                fromEnum = FromEnum.PARENT;
            } else if("$loop".equals(from)) { 
                fromEnum = FromEnum.LOOP;
            } else if("$flow".equals(from)) {
                fromEnum = FromEnum.FLOW;
            } else {
                // it's a bug in your flow ! But this can be done in the designer.
                fromEnum = null;
            }
            return fromEnum;
        }
        
    }
    
    
    
    enum ConverterEnum {
        /*
         * names are not random. They closely maps the FlowElement.TYPE_* constants.
         */
        INVOKE(new InvokeConverter()), //
        BRANCH(new BranchConverter()), //
        RETRY(new RetryConverter()), //
        SEQUENCE(new SequenceConverter()), //
        LOOP(new LoopConverter()), //
        EXIT(new ExitConverter()), //
        MAP(new MapConverter()), //
        MAPCOPY(new MapCopyConverter()), //
        MAPDELETE(new MapDeleteConverter()), //
        MAPSET(new MapSetConverter()), //
        MAPINVOKE(new MapInvokeConverter());
        
        private FlowElementConverter<? extends FlowElement,? extends Step> converter;
        
        
        ConverterEnum(FlowElementConverter<? extends FlowElement,? extends Step> converter) {
            this.converter = converter;
        }
        
        public FlowElementConverter<? extends FlowElement,? extends Step> getConverter() {
            return converter;
        }
    }
    

}
