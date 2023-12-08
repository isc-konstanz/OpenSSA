package org.esg.ic.ssa.meter;

import java.time.Instant;
import java.util.Properties;
import java.util.Random;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapter;
import org.esg.ic.ssa.api.GraphPattern;
import org.esg.ic.ssa.api.knowledge.PostKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.ReactKnowledgeInteraction;
import org.esg.ic.ssa.meter.data.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/*
 * Copyright 2023-24 ISC Konstanz
 *
 * This file is part of OpenSSA.
 * For more information visit https://github.com/isc-konstanz/OpenSSA.
 *
 * OpenSSA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
public class MeterService extends ServiceAdapter {

	static final String GRAPH_PATTERN = "meter.gp";

	final GraphPattern graphPattern;

    protected MeterService(GenericAdapter genericAdapter, String servicePropertiesFile)
    		throws GenericAdapterException {
		super(genericAdapter, servicePropertiesFile);
		this.graphPattern = new GraphPattern(getClass().getClassLoader().getResourceAsStream(GRAPH_PATTERN));
	}

    protected MeterService(GenericAdapter genericAdapter, Properties serviceProperties)
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

    public static MeterService register(GenericAdapter adapter, Properties properties) throws GenericAdapterException {
        return new MeterService(adapter, properties);
    }

    public static MeterService registerPosting(GenericAdapter adapter) throws GenericAdapterException {
        return new MeterService(adapter, "post.properties");
    }

    public static MeterService registerReacting(GenericAdapter adapter) throws GenericAdapterException {
        return new MeterService(adapter, "react.properties");
    }

    public static void main(String [] args) throws InterruptedException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        //loggerContext.getLogger(GenericAdapter.class).setLevel(Level.DEBUG);
        
        int interval = 10000;
        
        GenericAdapter adapter = new GenericAdapter();
        try {
            adapter.login("<user>", "<password>");
            
            try (MeterService service = MeterService.registerPosting(adapter)) {
            	MeterPostInteraction interaction = service.registerPostKnowledgeInteraction("node_id", ValueType.POWER);
                Random generator = new Random();
                while (true) {
                    Instant timestamp = Instant.now();
                    float power = generator.nextFloat() * 10000;
                    
                    interaction.post(timestamp, power);
                    
                    long postMillis = Instant.now().toEpochMilli() - timestamp.toEpochMilli();
                    if (postMillis < interval) {
                        Thread.sleep(interval - postMillis);
                    }
                }
            }
        } catch (GenericAdapterException e) {
            e.printStackTrace();
        }
    }

}
