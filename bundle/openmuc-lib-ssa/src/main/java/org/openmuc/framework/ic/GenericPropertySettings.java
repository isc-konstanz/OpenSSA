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

import static org.esg.ic.ssa.GenericAdapterSettings.HOST;
import static org.esg.ic.ssa.GenericAdapterSettings.PASSWORD;
import static org.esg.ic.ssa.GenericAdapterSettings.PORT;
import static org.esg.ic.ssa.GenericAdapterSettings.USERNAME;

import org.esg.ic.ssa.GenericAdapterSettings;
import org.openmuc.framework.lib.osgi.config.GenericSettings;
import org.openmuc.framework.lib.osgi.config.ServiceProperty;

public class GenericPropertySettings extends GenericSettings {

    public GenericPropertySettings() {
        super();
        String defaultHost = GenericAdapterSettings.HOST_DEFAULT;
        String defaultPort = String.valueOf(GenericAdapterSettings.PORT_DEFAULT);
        
        properties.put(HOST, new ServiceProperty(HOST, "Hostname of the Generic Adapter", defaultHost, false));
        properties.put(PORT, new ServiceProperty(PORT, "Port for Generic Adapter Communication", defaultPort, false));
        properties.put(USERNAME, new ServiceProperty(USERNAME, "Name of the Service Store Account", null, true));
        properties.put(PASSWORD, new ServiceProperty(PASSWORD, "Password of the Service Store Account", null, true));
    }
}
