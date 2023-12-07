package org.esg.ic.ssa.meter;

import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceInteraction;
import org.esg.ic.ssa.api.StartHandle;
import org.esg.ic.ssa.api.knowledge.ReactKnowledgeInteraction;

public class MeterReactInteraction extends ServiceInteraction<MeterService> {

    public MeterReactInteraction(
            MeterService serviceAdapter,
            ReactKnowledgeInteraction knowledgeInteraction, 
            String knowledgeInteractionId) {
        super(serviceAdapter, knowledgeInteraction, knowledgeInteractionId);
    }

    public StartHandle react() throws GenericAdapterException {
        return reactKnowledgeInteractionBinding();
    }
}
