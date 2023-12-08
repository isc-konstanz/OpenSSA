package org.esg.ic.ssa.stimulus;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterConnectionException;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.GenericAdapterTimeoutException;
import org.esg.ic.ssa.ServiceAdapter;
import org.esg.ic.ssa.meter.MeterReactInteraction;
import org.esg.ic.ssa.meter.MeterService;
import org.esg.ic.ssa.meter.data.FloatValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class StimulusService extends ServiceAdapter {

    protected StimulusService(GenericAdapter genericAdapter, String servicePropertiesFile)
    		throws GenericAdapterException {
		super(genericAdapter, servicePropertiesFile);
		// TODO: This is a placeholder implementation and has to be filled with content
	}

    public static void main(String [] args) throws InterruptedException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        loggerContext.getLogger(GenericAdapter.class).setLevel(Level.DEBUG);
        
        GenericAdapter adapter = new GenericAdapter();
        try {
            adapter.login("adrian.minde@isc-konstanz.de", "DimICa!468372666328");

            try (MeterService service = MeterService.registerReacting(adapter)) {
            	MeterReactInteraction interaction = service.registerReactKnowledgeInteraction();
                //TariffingService tariffService = adapter.register(TariffingService.class);
                while (true) {
                	try {
                    	List<FloatValue> values = interaction.react();
                    	if (values.size() > 0) {
                        	FloatValue value = values.get(0);
                        	System.out.println(String.format(Locale.US, "%s: %.3f", 
                        			DateTimeFormatter.ISO_INSTANT.format(value.getTimestamp()), 
                        			value.getValue()));
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
