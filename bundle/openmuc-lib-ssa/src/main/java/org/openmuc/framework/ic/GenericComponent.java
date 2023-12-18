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
package org.openmuc.framework.ic;

import java.io.IOException;
import java.util.Dictionary;
import java.util.concurrent.TimeoutException;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.GenericAdapterSettings;
import org.openmuc.framework.lib.osgi.config.DictionaryPreprocessor;
import org.openmuc.framework.lib.osgi.config.PropertyHandler;
import org.openmuc.framework.lib.osgi.config.ServicePropertyException;
import org.openmuc.framework.lib.osgi.deployment.RegistrationHandler;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate = true, service = {})
public class GenericComponent extends GenericAdapter implements GenericService, ManagedService {
    private static final Logger logger = LoggerFactory.getLogger(GenericComponent.class);

    private final PropertyHandler propertyHandler;
    private RegistrationHandler registrationHandler;

    public GenericComponent() {
        super();
        
        propertyHandler = new PropertyHandler(new GenericPropertySettings(),
                GenericAdapter.class.getName());
    }

    @Activate
    protected void activate(BundleContext context) {
        logger.info("Activating InterConnect Generic Adapter");
        
        registrationHandler = new RegistrationHandler(context);
        registrationHandler.provideInFrameworkAsManagedService(this, GenericAdapter.class.getName());
    }

    protected void activate() {
        logger.info("Authenticating with InterConnect Generic Adapter");
        try {
            login();
        	registrationHandler.provideInFrameworkWithoutConfiguration(GenericService.class.getName(), this);
            
        } catch (GenericAdapterException e) {
            logger.error("Error authenticating InterConnect Generic Adapter: {}", e.getMessage());
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) throws IOException, TimeoutException {
        logger.info("Deativating InterConnect Generic Adapter");
        deactivate();
    }

    protected void deactivate() {
        // Placeholder to be overridden
    }

    @Override
    public void updated(Dictionary<String, ?> propertyDict) {
        DictionaryPreprocessor dict = new DictionaryPreprocessor(propertyDict);
        if (!dict.wasIntermediateOsgiInitCall()) {
            updateConfig(dict);
        }
    }

    private void updateConfig(DictionaryPreprocessor properties) {
        try {
            propertyHandler.processConfig(properties);
            if (propertyHandler.configChanged()) {
                applyConfigChanges();
            }
        } catch (ServicePropertyException e) {
            logger.error("Update properties failed", e);
            deactivate();
        }
    }

    private void applyConfigChanges() {
        logger.info("Applying InterConnect Generic Adapter Settings: {}", propertyHandler.toString());
        settings = createSettings();
        deactivate();
        activate();
    }

    private GenericAdapterSettings createSettings() {
        return new GenericAdapterSettings(
                propertyHandler.getString(GenericAdapterSettings.HOST),
                propertyHandler.getInt(GenericAdapterSettings.PORT),
                propertyHandler.getString(GenericAdapterSettings.USERNAME),
                propertyHandler.getString(GenericAdapterSettings.PASSWORD));
    }

}
