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

import java.util.Arrays;

import javax.management.openmbean.InvalidKeyException;

import org.esg.ic.ssa.GenericAdapterException;

public class LoggingSettings {

	private static final String NODE = "nodeid";

    private final String settings;

    public LoggingSettings(String settings) throws GenericAdapterException {
        if (settings == null || settings.isEmpty()) {
            throw new GenericAdapterException("No settings specified");
        }
        this.settings = settings;
    }

	public String getSettings() {
		return settings;
	}

    public String getNode() {
        return get(NODE);
    }

    public String get(String key) {
        return Arrays.stream(settings.split(","))
                .filter(segment -> segment.contains(key))
                .map(keyVal -> keyVal.split("=")[1].trim())
                .findFirst()
                .orElseThrow(() -> new InvalidKeyException("Node ID is missing"));
    }

}
