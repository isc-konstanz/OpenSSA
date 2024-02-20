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
package org.esg.ic.ssa.recommender.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public enum RiskLevel {

	HEALTHY(0),
	LOW(1),
	MEDIUM(2),
	HIGH(3),
	VERY_HIGH(4);

	private final int level;

	private RiskLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public static RiskLevel valueOf(int level) throws IllegalArgumentException {
		switch (level) {
		case 0:
			return HEALTHY;
		case 1:
			return LOW;
		case 2:
			return MEDIUM;
		case 3:
			return HIGH;
		case 4:
			return VERY_HIGH;
		default:
			throw new IllegalArgumentException("Unknown risk level received: " + level);
		}
	}

    static class RiskLevelSerializer extends JsonSerializer<RiskLevel> {

        public RiskLevelSerializer() {
            super();
        }

        @Override
        public void serialize(RiskLevel riskLevel, JsonGenerator generator, SerializerProvider provider) 
    		    throws IOException, JsonProcessingException {
            generator.writeString(riskLevel.toString().toLowerCase().replace("_", " "));
        }
    }

    static class RiskLevelDeserializer extends JsonDeserializer<RiskLevel> {

        public RiskLevelDeserializer() {
            super();
        }

		@Override
		public RiskLevel deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
			String riskLevelStr = parser.getText().replace("<", "").replace(">", "");
			if (riskLevelStr.equals("null")) {
				return null;
			}
			return RiskLevel.valueOf(Integer.parseInt(riskLevelStr));
		}
    }
}
