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
package org.openmuc.framework.driver.ic.ssa.meter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.GenericAdapterTimeoutException;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.esg.ic.ssa.meter.MeterReactInteraction;
import org.esg.ic.ssa.meter.MeterServiceAdapter;
import org.esg.ic.ssa.meter.data.FloatValue;
import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.ChannelScanInfo;
import org.openmuc.framework.config.ScanException;
import org.openmuc.framework.data.Flag;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.driver.spi.ChannelRecordContainer;
import org.openmuc.framework.driver.spi.ChannelValueContainer;
import org.openmuc.framework.driver.spi.Connection;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.openmuc.framework.driver.spi.RecordsReceivedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeterServiceConnection extends MeterServiceAdapter implements Connection {
    private static final Logger logger = LoggerFactory.getLogger(MeterServiceConnection.class);

    private final MeterReactInteraction interaction;

    private final List<Meteristener> listeners = new ArrayList<Meteristener>();

    private RecordsReceivedListener listener;

	private ExecutorService executor;

    public MeterServiceConnection(GenericAdapter genericAdapter, ServiceAdapterSettings settings)
            throws GenericAdapterException {
        super(genericAdapter, settings);
        this.executor = Executors.newCachedThreadPool(new MeterListenerThreadFactory());
        this.interaction = registerReactKnowledgeInteraction();
    }

    @Override
    public List<ChannelScanInfo> scanForChannels(String settings)
            throws UnsupportedOperationException, ArgumentSyntaxException, ScanException, ConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startListening(List<ChannelRecordContainer> containers, RecordsReceivedListener recordListener)
            throws UnsupportedOperationException, ConnectionException {

        listeners.stream().forEach(l -> l.cancel());
        listeners.clear();
        listener = recordListener;
        
        for (ChannelRecordContainer container : containers) {
            String nodeId = container.getChannelAddress();
            
            Meteristener listener = listeners.stream().filter(l -> l.nodeId.equals(nodeId)).findFirst().orElse(null);
            if (listener == null) {
                listener = new Meteristener(nodeId);
                listeners.add(listener);
            }
            listener.containers.add(container);
        }
        listeners.stream().forEach(l -> executor.submit(l));
    }

    @Override
    public Object read(List<ChannelRecordContainer> containers, Object containerListHandle, String samplingGroup)
            throws UnsupportedOperationException, ConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object write(List<ChannelValueContainer> containers, Object containerListHandle)
            throws UnsupportedOperationException, ConnectionException {
        // TODO: Implement POST knowledgeInteraction here
        throw new UnsupportedOperationException();
    }

    private Record listenForRecord(String nodeId) {
        try {
            List<FloatValue> values = interaction.react();
            
            if (values.size() > 0) {
                for (FloatValue value : values) {
                    if (!value.getNode().equals(nodeId)) {
                        continue;
                    }
                    logger.debug("{}: {}",
                            DateTimeFormatter.ISO_INSTANT.format(value.getTimestamp()), 
                            String.format(Locale.US, "%.3f", value.getValue()));
                    
                    return new Record(
                            new org.openmuc.framework.data.FloatValue(value.getValue()), 
                            value.getTimestamp().toInstant().toEpochMilli(), 
                            Flag.VALID);
                }
            }
            return null;
            
        } catch (GenericAdapterTimeoutException e) {
            return new Record(Flag.DRIVER_ERROR_TIMEOUT);
            
        } catch (GenericAdapterException e) {
            logger.warn("Error while listening for react of node: {}.", nodeId, e);
            listener.connectionInterrupted(MeterDriver.ID, this);
        }
        return new Record(Flag.DRIVER_ERROR_READ_FAILURE);
    }

    @Override
    public void disconnect() {
      try {
            listeners.stream().forEach(l -> l.cancel());
            listeners.clear();

    		executor.shutdown();

            close();
            
        } catch (GenericAdapterException e) {
            logger.warn("Unknown error while closing service adapter: {}", e.getMessage());
        }
    }

    public class Meteristener implements Runnable {

        final List<ChannelRecordContainer> containers;

        final String nodeId;

        private boolean running = false;

        public Meteristener(String nodeId) {
            this.containers = new ArrayList<ChannelRecordContainer>();
            this.nodeId = nodeId;
        }

        public void cancel() {
            running = false;
        }

        @Override
        public void run() {
            logger.debug("Starting SSA React Listener for {} channels of node: {}", containers.size(), nodeId);
            
            running = true;
            while (running) {
                Record record = listenForRecord(nodeId);
                if (record != null) {
                    for (ChannelRecordContainer container : containers) {
                        container.setRecord(record);
                    }
                    listener.newRecords(containers);
                }
            }
        }
    }

	private class MeterListenerThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			String name = "EasySmartMetering SSA React Listener";
			if (r instanceof Meteristener) {
				name += " - " + ((Meteristener) r).nodeId;
			}
			return new Thread(r, name);
		}
	}
}
