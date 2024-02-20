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

import java.time.ZonedDateTime;

import org.esg.ic.ssa.data.TimeValue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class NumberValue extends TimeValue {
	private static final long serialVersionUID = 7670443138386699851L;

	@JsonSerialize(using = SarefPrefixSerializer.class)
    @JsonDeserialize(using = SarefPrefixDeserializer.class)
    protected String measurement;

    @JsonSerialize(using = SarefPrefixSerializer.class)
    @JsonDeserialize(using = SarefPrefixDeserializer.class)
    protected String property;

    protected String type;

    protected String unit;

    protected NumberValue(String node, ValueType type, ZonedDateTime timestamp) {
        super(node, timestamp);
        this.measurement = node;
        this.property = type.name().toLowerCase();
        this.type = type.getType();
        this.unit = type.getUnit();
    }

    protected NumberValue() {
    	super();
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
