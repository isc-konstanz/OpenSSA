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

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.openmuc.framework.datalogger.ic.ssa.meter.MeterLogger;
import org.openmuc.framework.datalogger.spi.DataLoggerService;
import org.openmuc.framework.ic.GenericService;
import org.openmuc.framework.lib.osgi.deployment.RegistrationHandler;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate = true, service = {})
public class LoggingServiceComponent {
    private static final Logger logger = LoggerFactory.getLogger(LoggingServiceComponent.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private GenericService genericAdapterService;

    private RegistrationHandler registrationHandler;

    private MeterLogger meterLogger;

    @Activate
    protected void activate(BundleContext context) throws GenericAdapterException {
        logger.info("Activating InterConnect Logging Service Adapter");
        
        meterLogger = new MeterLogger((GenericAdapter) genericAdapterService);
        
        registrationHandler = new RegistrationHandler(context);
        registerDataLogger(meterLogger);
    }

    protected void registerDataLogger(LoggingServiceAdapter logger) {
        registrationHandler.provideInFrameworkAsManagedService(logger, logger.getClass().getName());
        registrationHandler.provideInFrameworkWithoutConfiguration(DataLoggerService.class.getName(), logger);
    }

    @Deactivate
    protected void deactivate(ComponentContext context) throws GenericAdapterException {
        logger.info("Deactivating InterConnect Logging Service Adapter");
        
        meterLogger.deregister();
    }

}
