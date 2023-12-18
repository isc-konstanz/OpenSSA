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
package org.openmuc.framework.ic.ssa;

import static org.esg.ic.ssa.ServiceAdapterSettings.HASH;
import static org.esg.ic.ssa.ServiceAdapterSettings.KNOWLEDGE_BASE_DESCRIPTION;
import static org.esg.ic.ssa.ServiceAdapterSettings.KNOWLEDGE_BASE_ID;
import static org.esg.ic.ssa.ServiceAdapterSettings.KNOWLEDGE_BASE_NAME;

import java.io.IOException;
import java.util.Properties;

import org.esg.ic.ssa.GenericAdapterException;
import org.openmuc.framework.lib.osgi.config.GenericSettings;
import org.openmuc.framework.lib.osgi.config.ServiceProperty;


public class ServiceSpecificPropertySettings extends GenericSettings {

    private ServiceSpecificPropertySettings(Properties defaults) {
        super();
        String defaultHash = defaults.getProperty(HASH);
        String defaultId = defaults.getProperty(KNOWLEDGE_BASE_ID, null);
        String defaultName = defaults.getProperty(KNOWLEDGE_BASE_NAME);
        String defaultDescription = defaults.getProperty(KNOWLEDGE_BASE_DESCRIPTION);

        properties.put(HASH, new ServiceProperty(HASH, "Hash ID of the Service Adapter in the store", defaultHash, true));
        properties.put(KNOWLEDGE_BASE_ID, new ServiceProperty(KNOWLEDGE_BASE_ID, "Unique Identifier of the Knowledge Base", defaultId, false));
        properties.put(KNOWLEDGE_BASE_NAME, new ServiceProperty(KNOWLEDGE_BASE_NAME, "Name of the Knowledge Base", defaultName, true));
        properties.put(KNOWLEDGE_BASE_DESCRIPTION, new ServiceProperty(KNOWLEDGE_BASE_DESCRIPTION, "Description of the Knowledge Base", defaultDescription, true));
    }

    public static ServiceSpecificPropertySettings ofProperties(Properties properties) {
    	return new ServiceSpecificPropertySettings(properties);
    }

    public static ServiceSpecificPropertySettings ofResource(Class<?> serviceType, String defaultsFile) 
    		throws GenericAdapterException {
    	Properties defaultsProperties = new Properties();
    	try {
    		defaultsProperties.load(serviceType.getClassLoader().getResourceAsStream(defaultsFile));
			
		} catch (IOException e) {
			throw new GenericAdapterException("Unable to read service properties file: " + e.getMessage());
		}
    	return ofProperties(defaultsProperties);
    }
}
