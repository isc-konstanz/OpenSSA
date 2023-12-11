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
import java.time.Instant;
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

public class FloatValue extends TimeValue {
	private static final long serialVersionUID = -631127749660454791L;

	private static final String SAREF_SUFFIX = "^^xsd:float";

	@JsonSerialize(using = SarefFloatSerializer.class)
    @JsonDeserialize(using = SarefFloatDeserializer.class)
    protected Float value;

    public FloatValue(String node, ValueType type, Instant timestamp, Float value) {
        super(node, type, timestamp);
        this.value = value;
    }

    public FloatValue() {
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
        	jsonBuilder.append('"');
        	jsonBuilder.append('\\').append('"');
        	jsonBuilder.append(String.format(Locale.US, "%.3f", value));
        	jsonBuilder.append('\\').append('"');
        	jsonBuilder.append(SAREF_SUFFIX);
        	jsonBuilder.append('"');
            generator.writeNumber(jsonBuilder.toString());
        }
    }

    static class SarefFloatDeserializer extends JsonDeserializer<Float> {

        public SarefFloatDeserializer() {
            super();
        }

		@Override
		public Float deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
	        String floatStr = parser.getText().replace(SAREF_SUFFIX, "").replaceAll("\"", "");
	        return Float.valueOf(floatStr);
		}
    }

}
