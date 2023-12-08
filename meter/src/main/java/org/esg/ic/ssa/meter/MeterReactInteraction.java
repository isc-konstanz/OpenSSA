package org.esg.ic.ssa.meter;

import java.util.List;

import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceInteraction;
import org.esg.ic.ssa.api.BindingSet;
import org.esg.ic.ssa.api.knowledge.ReactKnowledgeInteraction;
import org.esg.ic.ssa.meter.data.FloatValue;

public class MeterReactInteraction extends ServiceInteraction<MeterService> {

    public MeterReactInteraction(
            MeterService serviceAdapter,
            ReactKnowledgeInteraction knowledgeInteraction, 
            String knowledgeInteractionId) {
        super(serviceAdapter, knowledgeInteraction, knowledgeInteractionId);
    }

    public List<FloatValue> react() throws GenericAdapterException {
        return reactKnowledgeInteractionBinding(new BindingSet<FloatValue>(), FloatValue.class);
    }
}
