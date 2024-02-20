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
package org.esg.ic.ssa.stimulus.data;

import java.time.ZonedDateTime;

import org.esg.ic.ssa.data.TimeValue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class NumberValue extends TimeValue {
	private static final long serialVersionUID = 7670443138386699851L;

	@JsonSerialize(using = SarefPrefixSerializer.class)
    @JsonDeserialize(using = SarefPrefixDeserializer.class)
    protected String data;

    @JsonSerialize(using = SarefPrefixSerializer.class)
    @JsonDeserialize(using = SarefPrefixDeserializer.class)
    protected String quantity;

    protected String stimulus;

    protected NumberValue(String node, ValueType type, ZonedDateTime timestamp) {
        super(node, timestamp);
        this.data = node;
        this.quantity = type.name().toLowerCase();
        this.stimulus = type.name().toLowerCase();
    }

    protected NumberValue() {
    	super();
    }

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getStimulus() {
		return stimulus;
	}

	public void setStimulus(String stimulus) {
		this.stimulus = stimulus;
	}

}
