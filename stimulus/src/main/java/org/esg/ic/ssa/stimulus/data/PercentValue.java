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

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PercentValue extends NumberValue {
	private static final long serialVersionUID = -8292291133720480580L;

	@JsonSerialize(using = SarefFloatSerializer.class)
    @JsonDeserialize(using = SarefFloatDeserializer.class)
    protected Float value;

    public PercentValue(String node, ZonedDateTime timestamp, Float value) {
        super(node, ValueType.PERCENT, timestamp);
        this.value = value;
    }

    public PercentValue() {
    	super();
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    static class SarefFloatSerializer extends JsonSerializer<Float> {

        public SarefFloatSerializer() {
            super();
        }

        @Override
        public void serialize(Float value, JsonGenerator generator, SerializerProvider provider) 
                throws IOException, JsonProcessingException {
        	StringBuilder jsonBuilder = new StringBuilder();
        	jsonBuilder.append('<');
        	jsonBuilder.append(String.format(Locale.US, "%.2f", value));
        	jsonBuilder.append('>');
            generator.writeString(jsonBuilder.toString());
        }
    }

    static class SarefFloatDeserializer extends JsonDeserializer<Float> {

        public SarefFloatDeserializer() {
            super();
        }

		@Override
		public Float deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
	        String floatStr = parser.getText().replaceAll("<", "").replaceAll(">", "");
	        return Float.valueOf(floatStr);
		}
    }

}
