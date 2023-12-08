package org.esg.ic.ssa.meter;

import java.time.Instant;

import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceInteraction;
import org.esg.ic.ssa.api.knowledge.PostKnowledgeInteraction;
import org.esg.ic.ssa.meter.data.FloatValue;
import org.esg.ic.ssa.meter.data.ValueType;

public class MeterPostInteraction extends ServiceInteraction<MeterService> {

    private final String node;
    private final ValueType type;

    public MeterPostInteraction(
            MeterService serviceAdapter,
            PostKnowledgeInteraction knowledgeInteraction, 
            String knowledgeInteractionId,
            String node,
            ValueType type) {
        super(serviceAdapter, knowledgeInteraction, knowledgeInteractionId);
        this.node = node;
        this.type = type;
    }

    public void post(Instant timestamp, float value) throws GenericAdapterException {
        FloatValue bindingValue = new FloatValue(node, type, timestamp, value);
        if (!serviceAdapter.graphPattern.validateBinding(bindingValue)) {
            throw new GenericAdapterException("Invalid binding to graph pattern: " + bindingValue);
        }
        postKnowledgeInteractionBinding(bindingValue.toSet());
    }
}
