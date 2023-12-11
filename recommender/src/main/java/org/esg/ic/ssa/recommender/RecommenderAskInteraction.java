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
package org.esg.ic.ssa.recommender;

import java.util.List;

import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceInteraction;
import org.esg.ic.ssa.api.BindingMap;
import org.esg.ic.ssa.api.knowledge.AskKnowledgeInteraction;
import org.esg.ic.ssa.recommender.dto.Recommendation;

public class RecommenderAskInteraction extends ServiceInteraction<RecommenderService> {

    private final String countryCode;
    private final int zipCode;

    public RecommenderAskInteraction(
    		RecommenderService serviceAdapter,
            AskKnowledgeInteraction knowledgeInteraction, 
            String knowledgeInteractionId,
            String countryCode,
            int zipCode) {
        super(serviceAdapter, knowledgeInteraction, knowledgeInteractionId);
        this.countryCode = countryCode;
        this.zipCode = zipCode;
    }

    public List<Recommendation> ask(
            String startDateTime,
            String endDateTime) throws GenericAdapterException {
    	
        BindingMap binding = new BindingMap();
        binding.put("country_code",   "<" + countryCode + ">");
        binding.put("start_datetime", "<" + startDateTime + ">");
        binding.put("end_datetime",   "<" + endDateTime + ">");
//        binding.put("zip_code",       "<" + zipCode + ">");
        
//        if (!serviceAdapter.graphPattern.validateBinding(binding)) {
//            throw new GenericAdapterException("Invalid binding to graph pattern: " + binding);
//        }
        return askKnowledgeInteractionBinding(binding.toSet(), Recommendation.class);
    }
}
