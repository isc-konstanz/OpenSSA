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
package org.esg.ic.ssa.data;

import java.io.IOException;

import org.esg.ic.ssa.api.Binding;

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

public abstract class NodeValue implements Binding {
	private static final long serialVersionUID = -1261490748515092272L;

	private static final String SAREF_PREFIX = "https://ontology.easysg.de/interconnect/";

    @JsonSerialize(using = SarefStringSerializer.class)
    @JsonDeserialize(using = SarefStringDeserializer.class)
    protected String node;

    protected NodeValue(String node) {
        super();
        this.node = node;
    }

    protected NodeValue() {
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
            
        } catch (JsonProcessingException e) {
        	return e.getMessage();
        }
    }

    public static class SarefStringSerializer extends JsonSerializer<String> {

        public SarefStringSerializer() {
            super();
        }

        @Override
        public void serialize(String value, JsonGenerator generator, SerializerProvider provider) 
    		    throws IOException, JsonProcessingException {
            generator.writeString(String.format("<%s>", value));
        }
    }

    public static class SarefStringDeserializer extends JsonDeserializer<String> {

        public SarefStringDeserializer() {
            super();
        }

		@Override
		public String deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
			return parser.getText().replace("<", "").replace(">", "").replace("\"", "");
		}
    }

    public static class SarefPrefixSerializer extends JsonSerializer<String> {

        public SarefPrefixSerializer() {
            super();
        }

        @Override
        public void serialize(String value, JsonGenerator generator, SerializerProvider provider) 
    		    throws IOException, JsonProcessingException {
        	String valueName = generator.getOutputContext().getCurrentName();
            generator.writeString(String.format("<%s/%s#%s>", SAREF_PREFIX, valueName, value));
        }
    }

    public static class SarefPrefixDeserializer extends JsonDeserializer<String> {

        public SarefPrefixDeserializer() {
            super();
        }

		@Override
		public String deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
        	String valueName = parser.getCurrentName();
			return parser.getText()
					.replace(SAREF_PREFIX + "/", "")
					.replace(valueName + "#", "")
					.replace("<", "").replace(">", "")
					.replace("\"", "");
		}
    }
}
