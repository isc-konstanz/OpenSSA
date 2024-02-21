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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapter;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.esg.ic.ssa.api.GraphPattern;
import org.esg.ic.ssa.api.GraphPrefixes;
import org.esg.ic.ssa.api.knowledge.AskKnowledgeInteraction;
import org.esg.ic.ssa.recommender.data.Recommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class RecommenderServiceAdapter extends ServiceAdapter {

    static final String GRAPH_PATTERN = "recommender.gp";

    static final GraphPrefixes GRAPH_PREFIXES = GraphPrefixes.Factory.create()
            .setNsPrefix("dc",      "http://purl.org/dc/elements/1.1/")
            .setNsPrefix("gn",      "https://www.geonames.org/ontology#")
            .setNsPrefix("s4ener",  "https://saref.etsi.org/saref4ener/")
            .setNsPrefix("ic-data", "http://ontology.tno.nl/interconnect/datapoint#")
            .setNsPrefix("time",    "http://www.w3.org/2006/time#")
            .setNsPrefix("saref",   "https://saref.etsi.org/corehasTime")
            .setNsPrefix("rdf",     "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
            .setNsPrefix("iso3166", "http://purl.org/dc/terms/ISO3166")
            .setNsPrefix("xsd",     "http://www.w3.org/2001/XMLSchema#")
            .lock();

    protected final GraphPattern graphPattern;

    public RecommenderServiceAdapter(GenericAdapter genericAdapter, ServiceAdapterSettings settings)
            throws GenericAdapterException {
        super(genericAdapter, settings);
        this.graphPattern = new GraphPattern(GRAPH_PREFIXES, RecommenderServiceAdapter.class.getClassLoader().getResourceAsStream(GRAPH_PATTERN));
    }

    private RecommenderServiceAdapter(GenericAdapter genericAdapter)
            throws GenericAdapterException {
        super(genericAdapter);
        this.graphPattern = new GraphPattern(GRAPH_PREFIXES, RecommenderServiceAdapter.class.getClassLoader().getResourceAsStream(GRAPH_PATTERN));
    }

    private RecommenderServiceAdapter(GenericAdapter genericAdapter, Properties serviceProperties)
            throws GenericAdapterException {
        super(genericAdapter, serviceProperties);
        this.graphPattern = new GraphPattern(GRAPH_PREFIXES, RecommenderServiceAdapter.class.getClassLoader().getResourceAsStream(GRAPH_PATTERN));
    }

    public RecommenderAskInteraction registerAskKnowledgeInteraction(String countryCode)
            throws GenericAdapterException {
        AskKnowledgeInteraction askKnowledgeInteraction = new AskKnowledgeInteraction(graphPattern);

        String knowledgeInteractionId = registerAskKnowledgeInteraction(askKnowledgeInteraction);
        return new RecommenderAskInteraction(this, askKnowledgeInteraction, knowledgeInteractionId, countryCode);
    }

    public static RecommenderServiceAdapter register(GenericAdapter adapter, Properties properties) throws GenericAdapterException {
        return new RecommenderServiceAdapter(adapter, properties);
    }

    public static RecommenderServiceAdapter registerAsking(GenericAdapter adapter) throws GenericAdapterException {
        return new RecommenderServiceAdapter(adapter);
    }

    public static void main(String [] args) throws InterruptedException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        loggerContext.getLogger(GenericAdapter.class).setLevel(Level.DEBUG);
        Logger logger = loggerContext.getLogger(RecommenderServiceAdapter.class);

        GenericAdapter genericAdapter = new GenericAdapter("<username>", "<password>");
        try {
            genericAdapter.login();

            String countryCode = "DE";

            try (RecommenderServiceAdapter service = RecommenderServiceAdapter.registerAsking(genericAdapter)) {
                RecommenderAskInteraction interaction = service.registerAskKnowledgeInteraction(countryCode);
                
                ZoneId timezone = ZoneId.of("CET");
                ZonedDateTime startDateTime = currentDateTime.truncatedTo(ChronoUnit.DAYS);
                ZonedDateTime endDateTime = startDateTime.plusDays(1).minusHours(1);
                
                List<Recommendation> recommendations = interaction.ask(startDateTime, endDateTime);
                
                logger.info("Received {} recommendations" + (recommendations.size() > 0 ? ": \n{}" : ""), 
                        recommendations.size(), 
                        recommendations.stream().map(r -> r.toString()).collect(Collectors.joining(", \n")));
            }
        } catch (GenericAdapterException e) {
            logger.error("Error demonstrating recommender service: {}", e.getMessage());
        }
    }
}
