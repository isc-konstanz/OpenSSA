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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.Random;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapter;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.esg.ic.ssa.api.GraphPattern;
import org.esg.ic.ssa.api.knowledge.PostKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.ReactKnowledgeInteraction;
import org.esg.ic.ssa.meter.data.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class MeterServiceAdapter extends ServiceAdapter {

	static final String GRAPH_PATTERN = "meter.gp";

	final GraphPattern graphPattern;

    public MeterServiceAdapter(GenericAdapter genericAdapter, ServiceAdapterSettings settings)
    		throws GenericAdapterException {
		super(genericAdapter, settings);
		this.graphPattern = new GraphPattern(getClass().getClassLoader().getResourceAsStream(GRAPH_PATTERN));
	}

    private MeterServiceAdapter(GenericAdapter genericAdapter, String servicePropertiesFile)
    		throws GenericAdapterException {
		super(genericAdapter, servicePropertiesFile);
		this.graphPattern = new GraphPattern(getClass().getClassLoader().getResourceAsStream(GRAPH_PATTERN));
	}

    private MeterServiceAdapter(GenericAdapter genericAdapter, Properties serviceProperties)
    		throws GenericAdapterException {
		super(genericAdapter, serviceProperties);
		this.graphPattern = new GraphPattern(getClass().getClassLoader().getResourceAsStream(GRAPH_PATTERN));
	}

    public MeterReactInteraction registerReactKnowledgeInteraction()
    		throws GenericAdapterException {
        ReactKnowledgeInteraction reactKnowledgeInteraction = new ReactKnowledgeInteraction(graphPattern);

        String knowledgeInteractionId = registerReactKnowledgeInteraction(reactKnowledgeInteraction);
        return new MeterReactInteraction(this, reactKnowledgeInteraction, knowledgeInteractionId);
    }

    public MeterPostInteraction registerPostKnowledgeInteraction(String node, ValueType type) 
    		throws GenericAdapterException {
        PostKnowledgeInteraction postKnowledgeInteraction = new PostKnowledgeInteraction(graphPattern);
        
        String knowledgeInteractionId = registerPostKnowledgeInteraction(postKnowledgeInteraction);
        return new MeterPostInteraction(this, postKnowledgeInteraction, knowledgeInteractionId, node, type);
    }

    public static MeterServiceAdapter register(GenericAdapter adapter, Properties properties) throws GenericAdapterException {
        return new MeterServiceAdapter(adapter, properties);
    }

    public static MeterServiceAdapter registerPosting(GenericAdapter adapter) throws GenericAdapterException {
        return new MeterServiceAdapter(adapter, "post.properties");
    }

    public static MeterServiceAdapter registerReacting(GenericAdapter adapter) throws GenericAdapterException {
        return new MeterServiceAdapter(adapter, "react.properties");
    }

    public static void main(String [] args) throws InterruptedException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        loggerContext.getLogger(GenericAdapter.class).setLevel(Level.DEBUG);
        Logger logger = loggerContext.getLogger(MeterServiceAdapter.class);
        
        int interval = 10000;
        
        GenericAdapter genericAdapter = new GenericAdapter("<username>", "<password>");
        try {
        	genericAdapter.login();
            
            try (MeterServiceAdapter service = MeterServiceAdapter.registerPosting(genericAdapter)) {
            	MeterPostInteraction interaction = service.registerPostKnowledgeInteraction("demo", ValueType.POWER);
                Random generator = new Random();
                while (true) {
                	ZonedDateTime timestamp = ZonedDateTime.now();
                    float power = generator.nextFloat() * 10000;
                    
                    interaction.post(timestamp, power);
                    
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
