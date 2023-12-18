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
package org.openmuc.framework.datalogger.ic.ssa.meter;

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
import org.esg.ic.ssa.meter.MeterPostInteraction;
import org.esg.ic.ssa.meter.MeterServiceAdapter;
import org.esg.ic.ssa.meter.data.ValueType;
import org.openmuc.framework.data.Flag;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.datalogger.ic.ssa.LoggingServiceAdapter;
import org.openmuc.framework.datalogger.ic.ssa.LoggingSettings;
import org.openmuc.framework.datalogger.spi.LoggingRecord;
import org.openmuc.framework.ic.ssa.ServiceSpecificPropertySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MeterLogger extends LoggingServiceAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MeterLogger.class);

    static final String ID = "ic-ssa-meter";

    private MeterServiceAdapter serviceAdapter;

    private Map<String, MeterPostInteraction> nodeInteractions = new HashMap<String, MeterPostInteraction>();

    public MeterLogger(GenericAdapter genericAdapter) throws GenericAdapterException {
        super(genericAdapter, 
                ServiceSpecificPropertySettings.ofResource(MeterServiceAdapter.class, "post.properties"));
    }

    @Override
    public String getId() {
        return ID;
    }

    public boolean hasAdapter() {
    	return serviceAdapter != null;
    }

    public MeterServiceAdapter getAdapter() {
    	return serviceAdapter;
    }

    @Override
    protected void register(GenericAdapter adapter, ServiceAdapterSettings settings) throws GenericAdapterException {
        logger.info("Registering Meter Server Adapter");
        
        serviceAdapter = new MeterServiceAdapter(adapter, settings);
        configure(channelSettings.values());
    }

    @Override
    public void deregister() throws GenericAdapterException {
        if (hasAdapter()) {
            logger.info("Unregistering Meter Server Adapter");
            
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
        for (MeterPostInteraction nodeInteraction : nodeInteractions.values()) {
            if (!nodes.contains(nodeInteraction.getNode())) {
                nodeInteractions.remove(nodeInteraction.getNode());
            }
        }
        for (LoggingSettings settings : channelSettings) {
            try {
                String node = settings.getNode();
                if (!nodeInteractions.containsKey(node)) {
                    nodeInteractions.put(node, getAdapter().registerPostKnowledgeInteraction(node, ValueType.POWER));
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
                ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(record.getTimestamp()), ZoneOffset.UTC);
                logger.info("Posting node \"{}\" value {} at {}", settings.getNode(), record.getValue(), dateTime);
                
                MeterPostInteraction meterPostInteraction = nodeInteractions.get(settings.getNode());
                meterPostInteraction.post(dateTime, record.getValue().asFloat());
                
            } catch (GenericAdapterException e) {
                logger.warn("Error posting node \"{}\" record: {}", settings.getNode(), record);
            }
        }
    }
}
