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

import java.util.Dictionary;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.openmuc.framework.lib.osgi.config.DictionaryPreprocessor;
import org.openmuc.framework.lib.osgi.config.PropertyHandler;
import org.openmuc.framework.lib.osgi.config.ServicePropertyException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ServiceSpecificComponent implements ManagedService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceSpecificComponent.class);

    private final PropertyHandler propertyHandler;

    public ServiceSpecificComponent(PropertyHandler properties) throws GenericAdapterException {
        this.propertyHandler = new PropertyHandler(new GenericPropertySettings(),
                GenericAdapter.class.getName());
    }

    @Override
    public void updated(Dictionary<String, ?> propertyDict) {
        DictionaryPreprocessor dict = new DictionaryPreprocessor(propertyDict);
        if (!dict.wasIntermediateOsgiInitCall()) {
            try {
				updateConfig(dict);
				
			} catch (GenericAdapterException e) {
	            logger.error("Updating InterConnect Service Adapter Settings failed", e);
			}
        }
    }

    private void updateConfig(DictionaryPreprocessor properties) throws GenericAdapterException {
        try {
            propertyHandler.processConfig(properties);
            if (propertyHandler.configChanged() || 
            		propertyHandler.isDefaultConfig()) {
                applyConfigChanges();
            }
        } catch (ServicePropertyException | GenericAdapterException e) {
            deregister();
        	throw new GenericAdapterException(e);
        }
    }

    private void applyConfigChanges() throws GenericAdapterException {
        logger.info("Applying InterConnect Service Adapter Settings: {}", propertyHandler.toString());
    	deregister();
        register(createSettings());
    }

    private ServiceAdapterSettings createSettings() {
    	return new ServiceAdapterSettings(
                propertyHandler.getString(ServiceAdapterSettings.HASH),
                propertyHandler.getString(ServiceAdapterSettings.KNOWLEDGE_BASE_ID),
                propertyHandler.getString(ServiceAdapterSettings.KNOWLEDGE_BASE_NAME),
                propertyHandler.getString(ServiceAdapterSettings.KNOWLEDGE_BASE_DESCRIPTION));
    }

    protected abstract void register(ServiceAdapterSettings settings) throws GenericAdapterException;

    protected abstract void deregister() throws GenericAdapterException;

}
