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
package org.esg.ic.ssa.stimulus;

import java.util.List;

import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceInteraction;
import org.esg.ic.ssa.api.BindingSet;
import org.esg.ic.ssa.api.knowledge.ReactKnowledgeInteraction;
import org.esg.ic.ssa.stimulus.data.PercentValue;

public class StimulusReactInteraction extends ServiceInteraction<StimulusServiceAdapter> {

    public StimulusReactInteraction(
    		StimulusServiceAdapter serviceAdapter,
            ReactKnowledgeInteraction knowledgeInteraction, 
            String knowledgeInteractionId) {
        super(serviceAdapter, knowledgeInteraction, knowledgeInteractionId);
    }

    public List<PercentValue> react() throws GenericAdapterException {
        return reactKnowledgeInteractionBinding(new BindingSet<PercentValue>(), PercentValue.class);
    }
}
