/*
 * Copyright 2023-24 ISC Konstanz
 *
 * This file is part of OpenSSA.
 * For more information visit https://github.com/isc-konstanz/OpenSSA.
 *
 * OpenSSA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenSSA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenSSA. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.esg.ic.ssa;

import java.util.List;

import org.esg.ic.ssa.api.Binding;
import org.esg.ic.ssa.api.StartHandle;
import org.esg.ic.ssa.api.knowledge.KnowledgeInteraction;

public abstract class ServiceInteraction<S extends ServiceAdapter> {

    protected final KnowledgeInteraction knowledgeInteraction;

    protected final String knowledgeInteractionId;

    protected final S serviceAdapter;

    protected ServiceInteraction(
    		S serviceAdapter,
    		KnowledgeInteraction knowledgeInteraction,
    		String knowledgeInteractionId) {
        this.knowledgeInteractionId = knowledgeInteractionId;
        this.knowledgeInteraction = knowledgeInteraction;
        this.serviceAdapter = serviceAdapter;
    }

    public KnowledgeInteraction getKnowledgeInteraction() {
        return knowledgeInteraction;
    }

    public String getKnowledgeInteractionId() {
        return knowledgeInteractionId;
    }

    public S getServiceAdapter() {
        return serviceAdapter;
    }

    protected <B extends Binding> String postKnowledgeInteractionBinding(List<B> bindingSet)
    		throws GenericAdapterException {
    	return serviceAdapter.postKnowledgeInteractionBinding(knowledgeInteractionId, bindingSet);
    }

    protected StartHandle reactKnowledgeInteractionBinding() throws GenericAdapterException {
        return serviceAdapter.reactKnowledgeInteractionBinding();
    }

    protected <B extends Binding> List<B> askKnowledgeInteractionBinding(List<Binding> bindingSet,
    		Class<B> resultType) throws GenericAdapterException {
    	return serviceAdapter.askKnowledgeInteractionBinding(knowledgeInteractionId, bindingSet, resultType);
    }

    protected StartHandle answerKnowledgeInteractionBinding() throws GenericAdapterException {
        return serviceAdapter.answerKnowledgeInteractionBinding();
    }

}
