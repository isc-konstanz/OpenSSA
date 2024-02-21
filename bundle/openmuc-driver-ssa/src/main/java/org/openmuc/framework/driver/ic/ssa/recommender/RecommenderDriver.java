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
package org.openmuc.framework.driver.ic.ssa.recommender;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.esg.ic.ssa.recommender.RecommenderServiceAdapter;
import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.DriverInfo;
import org.openmuc.framework.driver.ic.ssa.GenericDriver;
import org.openmuc.framework.driver.spi.Connection;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.openmuc.framework.driver.spi.DriverService;
import org.openmuc.framework.ic.GenericService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component
public class RecommenderDriver extends GenericDriver implements DriverService {

    static final String ID = "ic-ssa-recommender";

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private GenericService genericAdapterService;

    public RecommenderDriver() throws GenericAdapterException {
        super(RecommenderServiceAdapter.class, "service.properties");
    }

    @Override
    protected String getId() {
        return ID;
    }

    @Override
    public DriverInfo getInfo() {
        String description = defaultsProperties.getProperty(ServiceAdapterSettings.KNOWLEDGE_BASE_DESCRIPTION);
        String addressSyntax = "<knowledgeBaseId>";
        String settingsSyntax = "Not needed";
        String scanSettingsSyntax = "Not supported";
        String channelAddressSyntax = "[<hour>]";

        return new DriverInfo(getId(), description, addressSyntax, settingsSyntax, channelAddressSyntax, scanSettingsSyntax);
    }

    @Override
    public Connection connect(String address, String settings)
            throws ArgumentSyntaxException, ConnectionException {
        try {
            return new RecommenderServiceConnection((GenericAdapter) genericAdapterService, createSettings(address));
            
        } catch (GenericAdapterException e) {
            throw new ConnectionException(e);
        }
    }

}
