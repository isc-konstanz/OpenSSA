/*
 * Copyright 2011-2022 Fraunhofer ISE
 *
 * This file is part of OpenMUC.
 * For more information visit http://www.openmuc.org
 *
 * OpenMUC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenMUC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenMUC.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openmuc.framework.datalogger.ic.ssa.stimulus;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.esg.ic.ssa.stimulus.StimulusPostInteraction;
import org.esg.ic.ssa.stimulus.StimulusServiceAdapter;
import org.openmuc.framework.data.Flag;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.datalogger.ic.ssa.LoggingServiceAdapter;
import org.openmuc.framework.datalogger.ic.ssa.LoggingSettings;
import org.openmuc.framework.datalogger.spi.LoggingRecord;
import org.openmuc.framework.ic.ssa.ServiceSpecificPropertySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StimulusLogger extends LoggingServiceAdapter {
    private static final Logger logger = LoggerFactory.getLogger(StimulusLogger.class);

    static final String ID = "ic-ssa-stimulus";

    private StimulusServiceAdapter serviceAdapter;

    private Map<String, StimulusPostInteraction> nodeInteractions = new HashMap<String, StimulusPostInteraction>();

    public StimulusLogger(GenericAdapter genericAdapter) throws GenericAdapterException {
        super(genericAdapter, ServiceSpecificPropertySettings.ofResource(StimulusServiceAdapter.class, "post.properties"));
    }

    @Override
    public String getId() {
        return ID;
    }

    public boolean hasAdapter() {
    	return serviceAdapter != null;
    }

    public StimulusServiceAdapter getAdapter() {
    	return serviceAdapter;
    }

	@Override
    protected void register(GenericAdapter adapter, ServiceAdapterSettings settings) throws GenericAdapterException {
        logger.info("Registering Stimulus Server Adapter");
        
        serviceAdapter = new StimulusServiceAdapter(adapter, settings);
        configure(channelSettings.values());
    }

    @Override
    public void deregister() throws GenericAdapterException {
        if (hasAdapter()) {
            logger.info("Unregistering Stimulus Server Adapter");
            
            serviceAdapter.close();
            serviceAdapter = null;
        }
        nodeInteractions.clear();
    }

    @Override
    protected void configure(Collection<LoggingSettings> channelSettings) {
    	if (!hasAdapter()) {
    		return;
    	}
        List<String> nodes = channelSettings.stream().map(s -> s.getNode()).distinct().collect(Collectors.toList());
        for (StimulusPostInteraction nodeInteraction : nodeInteractions.values()) {
            if (!nodes.contains(nodeInteraction.getNode())) {
                nodeInteractions.remove(nodeInteraction.getNode());
            }
        }
        for (LoggingSettings settings : channelSettings) {
            try {
                String node = settings.getNode();
                if (!nodeInteractions.containsKey(node)) {
                    nodeInteractions.put(node, getAdapter().registerPostKnowledgeInteraction(node));
                }
            } catch (GenericAdapterException e) {
                logger.warn("Error registering Post Knowledge Interaction: {}", e.getMessage());
            }
        }
    }

    @Override
    protected void post(List<LoggingRecord> loggingRecordList, long timestamp) {
        for (LoggingRecord loggingRecord : loggingRecordList) {
            Record record = loggingRecord.getRecord();
            if (record.getFlag() != Flag.VALID) {
                logger.debug("Skipping posting invalid record:", record);
                continue;
            }
            LoggingSettings settings = channelSettings.get(loggingRecord.getChannelId());
            if (!nodeInteractions.containsKey(settings.getNode())) {
                logger.debug("Skipping posting unconfigured node \"{}\"", settings.getNode());
                continue;
            }
            try {
            	// TODO: Make scaling configurable
                float value = record.getValue().asFloat() * 100f;
                
                ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(record.getTimestamp()), ZoneOffset.UTC);
                logger.debug("Posting node \"{}\" value {} at {}", settings.getNode(), value, dateTime);
                
                StimulusPostInteraction stimulusPostInteraction = nodeInteractions.get(settings.getNode());
                stimulusPostInteraction.post(dateTime, value);
                
            } catch (GenericAdapterException e) {
                logger.warn("Error posting node \"{}\" record: {}", settings.getNode(), record, e);
                logger.debug("Reregistering Stimulus Server Adapter");
                try {
					serviceAdapter.close();
	                serviceAdapter.register();
	                configure(channelSettings.values());
					
				} catch (GenericAdapterException e1) {
		            logger.warn("Error reregistering Stimulus Server Adapter: {}", e.getMessage());
				}
            }
        }
    }
}
