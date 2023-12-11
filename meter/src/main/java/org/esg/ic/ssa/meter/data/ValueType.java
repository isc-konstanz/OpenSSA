/*
 * Copyright 2023-24 ISC Konstanz
 *
 * This file is part of OpenSSA.
 * For more information visit https://github.com/isc-konstanz/OpenSSA.
 *
 * OpenSSA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenSSA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenSSA. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.esg.ic.ssa.meter.data;

public enum ValueType {

    POWER("<https://saref.etsi.org/core/Power>", "<http://www.ontology-of-units-of-measure.org/resource/om-2/kilowatt>"),
    ENERGY("<https://saref.etsi.org/core/Energy>", "<http://www.ontology-of-units-of-measure.org/resource/om-2/kilowatthour>");

    private final String type;
    private final String unit;

    private ValueType(String type, String unit) {
        this.type = type;
        this.unit = unit;
    }

    public String getSuffix() {
    	return String.format("_%s", name().toLowerCase());
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

}
