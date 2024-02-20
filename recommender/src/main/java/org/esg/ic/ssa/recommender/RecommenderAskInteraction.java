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

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceInteraction;
import org.esg.ic.ssa.api.BindingMap;
import org.esg.ic.ssa.api.knowledge.AskKnowledgeInteraction;
import org.esg.ic.ssa.recommender.data.Recommendation;

public class RecommenderAskInteraction extends ServiceInteraction<RecommenderServiceAdapter> {

    private final String countryCode;

    public RecommenderAskInteraction(
    		RecommenderServiceAdapter serviceAdapter,
            AskKnowledgeInteraction knowledgeInteraction, 
            String knowledgeInteractionId,
            String countryCode) {
        super(serviceAdapter, knowledgeInteraction, knowledgeInteractionId);
        this.countryCode = countryCode;
    }

    public List<Recommendation> ask(
            ZonedDateTime startDateTime,
            ZonedDateTime endDateTime) throws GenericAdapterException {
    	
    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral('T')
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .toFormatter();
    	
        BindingMap binding = new BindingMap();
        binding.put("country_code",   "<" + countryCode + ">");
        binding.put("start_datetime", "<" + startDateTime.withZoneSameInstant(ZoneOffset.UTC).format(dateTimeFormatter) + ">");
        binding.put("end_datetime",   "<" + endDateTime.withZoneSameInstant(ZoneOffset.UTC).format(dateTimeFormatter) + ">");
        
//        if (!serviceAdapter.graphPattern.validateBinding(binding)) {
//            throw new GenericAdapterException("Invalid binding to graph pattern: " + binding);
//        }
        return askKnowledgeInteractionBinding(binding.toSet(), Recommendation.class);
    }
}
