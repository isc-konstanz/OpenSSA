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
package org.openmuc.framework.driver.ic.ssa;

import java.io.IOException;
import java.util.Properties;

import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.DriverInfo;
import org.openmuc.framework.config.ScanException;
import org.openmuc.framework.config.ScanInterruptedException;
import org.openmuc.framework.driver.spi.DriverDeviceScanListener;
import org.openmuc.framework.driver.spi.DriverService;


public abstract class GenericDriver implements DriverService {

    protected final Properties defaultsProperties;

    protected GenericDriver(Class<?> serviceType, String defaultsFile) 
    		throws GenericAdapterException {
    	defaultsProperties = new Properties();
    	try {
    		defaultsProperties.load(serviceType.getClassLoader().getResourceAsStream(defaultsFile));
			
		} catch (IOException e) {
			throw new GenericAdapterException("Unable to read service properties file: " + e.getMessage());
		}
    }

    protected abstract String getId();

    @Override
    public DriverInfo getInfo() {
    	String description = defaultsProperties.getProperty(ServiceAdapterSettings.KNOWLEDGE_BASE_DESCRIPTION);
        String addressSyntax = "<knowledgeBaseId>";
        String settingsSyntax = "Not needed";
        String scanSettingsSyntax = "Not supported";
        String channelAddressSyntax = "<nodeId>";

        return new DriverInfo(getId(), description, addressSyntax, settingsSyntax, channelAddressSyntax, scanSettingsSyntax);
    }

    protected ServiceAdapterSettings createSettings(String knowledgeBaseId) {
    	ServiceAdapterSettings settings = new ServiceAdapterSettings(
    			defaultsProperties.getProperty(ServiceAdapterSettings.HASH),
    			defaultsProperties.getProperty(ServiceAdapterSettings.KNOWLEDGE_BASE_ID, null),
    			defaultsProperties.getProperty(ServiceAdapterSettings.KNOWLEDGE_BASE_NAME),
    			defaultsProperties.getProperty(ServiceAdapterSettings.KNOWLEDGE_BASE_DESCRIPTION));
    	
    	settings.setKnowledgeBaseId(knowledgeBaseId);
    	return settings;
    }

	@Override
	public void scanForDevices(String settings, DriverDeviceScanListener listener)
	        throws UnsupportedOperationException, ArgumentSyntaxException, ScanException, ScanInterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void interruptDeviceScan()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
