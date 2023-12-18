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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterConnectionException;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.GenericAdapterTimeoutException;
import org.esg.ic.ssa.ServiceAdapter;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.esg.ic.ssa.meter.MeterReactInteraction;
import org.esg.ic.ssa.meter.MeterServiceAdapter;
import org.esg.ic.ssa.meter.data.FloatValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class StimulusServiceAdapter extends ServiceAdapter {

    public StimulusServiceAdapter(GenericAdapter genericAdapter, ServiceAdapterSettings settings)
    		throws GenericAdapterException {
		super(genericAdapter, settings);
		// TODO: This is a placeholder implementation and has to be filled with content
	}

    public static void main(String [] args) throws InterruptedException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        loggerContext.getLogger(GenericAdapter.class).setLevel(Level.DEBUG);
        Logger logger = loggerContext.getLogger(StimulusServiceAdapter.class);

        GenericAdapter genericAdapter = new GenericAdapter("<username>", "<password>");
        try {
        	genericAdapter.login();
            
            try (MeterServiceAdapter service = MeterServiceAdapter.registerReacting(genericAdapter)) {
            	MeterReactInteraction interaction = service.registerReactKnowledgeInteraction();
                //TariffingService tariffService = adapter.register(TariffingService.class);
                while (true) {
                	try {
                    	List<FloatValue> values = interaction.react();
                    	if (values.size() > 0) {
                    		for (FloatValue value : values) {
                            	logger.info("{}: {}",
                            			DateTimeFormatter.ISO_INSTANT.format(value.getTimestamp()), 
                            			String.format(Locale.US, "%.3f", value.getValue()));
                    		}
                    	}
                	} catch (GenericAdapterTimeoutException e) {
                		// Continue to wait for power values
                	} catch (GenericAdapterConnectionException e) {
                        e.printStackTrace();
                	}
                }
            }
        } catch (GenericAdapterException e) {
            e.printStackTrace();
        }
    }

}
