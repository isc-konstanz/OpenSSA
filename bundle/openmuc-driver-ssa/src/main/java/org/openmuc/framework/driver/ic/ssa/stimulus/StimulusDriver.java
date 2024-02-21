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
package org.openmuc.framework.driver.ic.ssa.stimulus;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.stimulus.StimulusServiceAdapter;
import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.driver.ic.ssa.GenericDriver;
import org.openmuc.framework.driver.spi.Connection;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.openmuc.framework.driver.spi.DriverService;
import org.openmuc.framework.ic.GenericService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component
public class StimulusDriver extends GenericDriver implements DriverService {

    static final String ID = "ic-ssa-stimulus";

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private GenericService genericAdapterService;

    public StimulusDriver() throws GenericAdapterException {
        super(StimulusServiceAdapter.class, "react.properties");
    }

    @Override
    protected String getId() {
        return ID;
    }

    @Override
    public Connection connect(String address, String settings)
            throws ArgumentSyntaxException, ConnectionException {
        try {
            return new StimulusServiceConnection((GenericAdapter) genericAdapterService, createSettings(address));
            
        } catch (GenericAdapterException e) {
            throw new ConnectionException(e);
        }
    }

}
