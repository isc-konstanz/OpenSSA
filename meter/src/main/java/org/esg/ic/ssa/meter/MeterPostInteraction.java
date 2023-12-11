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
package org.esg.ic.ssa.meter;

import java.time.ZonedDateTime;

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

    public void post(ZonedDateTime timestamp, float value) throws GenericAdapterException {
        FloatValue bindingValue = new FloatValue(node, type, timestamp, value);
        if (!serviceAdapter.graphPattern.validateBinding(bindingValue)) {
            throw new GenericAdapterException("Invalid binding to graph pattern: " + bindingValue);
        }
        postKnowledgeInteractionBinding(bindingValue.toSet());
    }
}
