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
package org.openmuc.framework.datalogger.ic.ssa;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.datalogger.spi.DataLoggerService;
import org.openmuc.framework.datalogger.spi.LogChannel;
import org.openmuc.framework.datalogger.spi.LoggingRecord;
import org.openmuc.framework.ic.ssa.ServiceSpecificAdapter;
import org.openmuc.framework.ic.ssa.ServiceSpecificPropertySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class LoggingServiceAdapter extends ServiceSpecificAdapter implements DataLoggerService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingServiceAdapter.class);

    protected final HashMap<String, LoggingSettings> channelSettings = new HashMap<>();

    protected LoggingServiceAdapter(
    		GenericAdapter genericAdapter, 
    		ServiceSpecificPropertySettings propertySettings) throws GenericAdapterException {
        super(genericAdapter, propertySettings);
    }

    @Override
    public boolean logSettingsRequired() { return true; }

    @Override
    public final void setChannelsToLog(List<LogChannel> channels) {
        channelSettings.clear();
        
        for (LogChannel channel : channels) {
            if (channel.getLoggingSettings().contains(getId())) {
                try {
                    String settingStr = Arrays.stream(channel.getLoggingSettings().split(";"))
                            .filter(segment -> segment.contains(getId()))
                            .map(keyVal -> keyVal.split(":")[1].trim())
                            .findFirst()
                            .orElseThrow(() -> new GenericAdapterException("Logger ID is missing: " + getId()));
                    
                    LoggingSettings settings = new LoggingSettings(settingStr);
                    channelSettings.put(channel.getId(), settings);
                    
                } catch (GenericAdapterException e) {
                	logger.warn("Error parsing channel settings: {}", e.getMessage());
                }
            }
        }
        configure(channelSettings.values());
    }

    protected abstract void configure(Collection<LoggingSettings> channelSettings);

    @Override
    public final void logEvent(List<LoggingRecord> containers, long timestamp) {
        log(containers, timestamp);
    }

    @Override
    public final void log(List<LoggingRecord> loggingRecordList, long timestamp) {
        List<LoggingRecord> postRecordList = loggingRecordList.stream()
                .filter(record -> channelSettings.containsKey(record.getChannelId()))
                .collect(Collectors.toList());
        
        post(postRecordList, timestamp);
    }

    protected abstract void post(List<LoggingRecord> loggingRecordList, long timestamp);

    @Override
    public List<Record> getRecords(String channelId, long startTime, long endTime) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Record getLatestLogRecord(String channelId) throws IOException {
        throw new UnsupportedOperationException();
    }
}
