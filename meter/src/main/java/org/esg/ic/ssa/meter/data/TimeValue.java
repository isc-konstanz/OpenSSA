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

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.esg.ic.ssa.data.NodeValue;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class TimeValue extends NodeValue {
	private static final long serialVersionUID = 2552330320720578853L;

	private static final String SAREF_SUFFIX = "^^xsd:dateTime";

	@JsonSerialize(using = SarefAddressSerializer.class)
    @JsonDeserialize(using = SarefAddressDeserializer.class)
    protected String measurement;

    @JsonSerialize(using = SarefAddressSerializer.class)
    @JsonDeserialize(using = SarefAddressDeserializer.class)
    protected String property;

    protected String type;

    protected String unit;

    @JsonSerialize(using = SarefInstantSerializer.class)
    @JsonDeserialize(using = SarefDateTimeDeserializer.class)
    protected ZonedDateTime timestamp;

    protected TimeValue(String node, ValueType type, ZonedDateTime timestamp) {
        super(node);
        String measurementType = type.getSuffix();
        this.measurement = node + measurementType;
        this.property = node + measurementType;
        this.type = type.getType();
        this.unit = type.getUnit();
        this.timestamp = timestamp;
    }

    protected TimeValue() {
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

	public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
            
        } catch (JsonProcessingException e) {
        	return e.getMessage();
        }
    }

    static class SarefInstantSerializer extends JsonSerializer<ZonedDateTime> {

        public SarefInstantSerializer() {
            super();
        }

        @Override
        public void serialize(ZonedDateTime dateTime, JsonGenerator generator, SerializerProvider provider) 
    		    throws IOException, JsonProcessingException {
        	StringBuilder jsonBuilder = new StringBuilder();
        	jsonBuilder.append('"');
        	jsonBuilder.append('\\').append('"');
        	jsonBuilder.append(DateTimeFormatter.ISO_DATE_TIME.format(dateTime));
        	jsonBuilder.append('\\').append('"');
        	jsonBuilder.append(SAREF_SUFFIX);
        	jsonBuilder.append('"');
            generator.writeNumber(jsonBuilder.toString());
        }
    }

    static class SarefDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

        public SarefDateTimeDeserializer() {
            super();
        }

		@Override
		public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
	        String dateTimeStr = parser.getText().replace(SAREF_SUFFIX, "").replaceAll("\"", "");
	        return ZonedDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
		}
    }
}
