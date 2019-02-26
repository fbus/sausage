package org.sausage.serializer.mixin;

import org.sausage.model.step.Branch;
import org.sausage.model.step.Exit;
import org.sausage.model.step.Invoke;
import org.sausage.model.step.Loop;
import org.sausage.model.step.MapStep;
import org.sausage.model.step.Repeat;
import org.sausage.model.step.Sequence;
import org.sausage.model.step.invoke.InputMap;
import org.sausage.model.step.invoke.OutputMap;
import org.sausage.model.step.map.Copy;
import org.sausage.model.step.map.Drop;
import org.sausage.model.step.map.SetValue;
import org.sausage.model.step.map.Transformer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type")
      @JsonSubTypes({ 
        @Type(value = MapStep.class, name = "MAP"), 
        @Type(value = Sequence.class, name = "SEQUENCE"), 
        @Type(value = Loop.class, name = "LOOP"),
        @Type(value = Repeat.class, name = "REPEAT"),
        @Type(value = Branch.class, name = "BRANCH"),
        @Type(value = Exit.class, name = "EXIT"),
        @Type(value = Invoke.class, name = "INVOKE"),
        // following are 'substeps'
        @Type(value = InputMap.class, name = "InputMap"), 
        @Type(value = OutputMap.class, name = "OutputMap"), 
        @Type(value = Copy.class, name = "Copy"),
        @Type(value = Drop.class, name = "Drop"),
        @Type(value = SetValue.class, name = "SetValue"),
        @Type(value = Transformer.class, name = "Transformer"),
      })
@JsonIgnoreProperties({"parent", "id", "parentInvoke"}) // don't put parent to avoid recursion
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ //
		"disable", //
		"label", //
		"comment", //
		"switchProperty", //
		"evaluateLabels", //
		"exitOn", //
		"inputArray", //
		"outputArray", //
		"nodes", //
		"serviceName", //
		"input", //
		"output", //
		"pipelineChanges" })
public class JacksonStepMixin {
	// TODO voir si @JsonBackReference pourrait eviter d'ignorer "parent".
}
