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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.Random;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapter;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.esg.ic.ssa.api.GraphPattern;
import org.esg.ic.ssa.api.GraphPrefixes;
import org.esg.ic.ssa.api.knowledge.PostKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.ReactKnowledgeInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class StimulusServiceAdapter extends ServiceAdapter {

	static final String GRAPH_PATTERN = "stimulus.gp";

    static final GraphPrefixes GRAPH_PREFIXES = GraphPrefixes.Factory.defaults()
	        .setNsPrefix("om", "http://www.ontology-of-units-of-measure.org/resource/om-2/")
	        .setNsPrefix("ic-data", "http://ontology.tno.nl/interconnect/datapoint#")
	        .lock();

	final GraphPattern graphPattern;

    public StimulusServiceAdapter(GenericAdapter genericAdapter, ServiceAdapterSettings settings)
    		throws GenericAdapterException {
		super(genericAdapter, settings);
		this.graphPattern = new GraphPattern(GRAPH_PREFIXES, StimulusServiceAdapter.class.getClassLoader().getResourceAsStream(GRAPH_PATTERN));
	}

    private StimulusServiceAdapter(GenericAdapter genericAdapter, String servicePropertiesFile)
    		throws GenericAdapterException {
		super(genericAdapter, servicePropertiesFile);
		this.graphPattern = new GraphPattern(GRAPH_PREFIXES, StimulusServiceAdapter.class.getClassLoader().getResourceAsStream(GRAPH_PATTERN));
	}

    private StimulusServiceAdapter(GenericAdapter genericAdapter, Properties serviceProperties)
    		throws GenericAdapterException {
		super(genericAdapter, serviceProperties);
		this.graphPattern = new GraphPattern(GRAPH_PREFIXES, StimulusServiceAdapter.class.getClassLoader().getResourceAsStream(GRAPH_PATTERN));
	}

    public StimulusReactInteraction registerReactKnowledgeInteraction()
    		throws GenericAdapterException {
        ReactKnowledgeInteraction reactKnowledgeInteraction = new ReactKnowledgeInteraction(graphPattern);

        String knowledgeInteractionId = registerReactKnowledgeInteraction(reactKnowledgeInteraction);
        return new StimulusReactInteraction(this, reactKnowledgeInteraction, knowledgeInteractionId);
    }

    public StimulusPostInteraction registerPostKnowledgeInteraction(String node) 
    		throws GenericAdapterException {
        PostKnowledgeInteraction postKnowledgeInteraction = new PostKnowledgeInteraction(graphPattern);
        
        String knowledgeInteractionId = registerPostKnowledgeInteraction(postKnowledgeInteraction);
        return new StimulusPostInteraction(this, postKnowledgeInteraction, knowledgeInteractionId, node);
    }

	public static StimulusServiceAdapter register(GenericAdapter adapter, Properties properties) throws GenericAdapterException {
        return new StimulusServiceAdapter(adapter, properties);
    }

	public static StimulusServiceAdapter registerPosting(GenericAdapter adapter) throws GenericAdapterException {
        return new StimulusServiceAdapter(adapter, "post.properties");
    }

    public static StimulusServiceAdapter registerReacting(GenericAdapter adapter) throws GenericAdapterException {
        return new StimulusServiceAdapter(adapter, "react.properties");
    }

    public static void main(String [] args) throws InterruptedException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        loggerContext.getLogger(GenericAdapter.class).setLevel(Level.DEBUG);
        Logger logger = loggerContext.getLogger(StimulusServiceAdapter.class);

        GenericAdapter genericAdapter = new GenericAdapter("<username>", "<password>");
        try {
        	genericAdapter.login();
        	
        	int interval = 10000;
            
            try (StimulusServiceAdapter service = StimulusServiceAdapter.registerReacting(genericAdapter)) {
            	StimulusPostInteraction interaction = service.registerPostKnowledgeInteraction("demo");
                Random generator = new Random();
                while (true) {
                	ZonedDateTime timestamp = ZonedDateTime.now();
                    float percent = generator.nextFloat() * 100;
                    
                    interaction.post(timestamp, percent);
                    
                    long postMillis = Instant.now().toEpochMilli() - timestamp.toInstant().toEpochMilli();
                    if (postMillis < interval) {
                        Thread.sleep(interval - postMillis);
                    }
                }
            }
        } catch (GenericAdapterException e) {
        	logger.error("Error demonstrating meter service: {}", e);
        }
    }

}
