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

public enum RiskEvaluation {

	NOT_AVAILABLE,
	HEALTHY,
	INCREASE,
	DECREASE;

    static class RiskEvaluationSerializer extends JsonSerializer<RiskEvaluation> {

        public RiskEvaluationSerializer() {
            super();
        }

        @Override
        public void serialize(RiskEvaluation riskEvaluation, JsonGenerator generator, SerializerProvider provider) 
    		    throws IOException, JsonProcessingException {
            generator.writeString(riskEvaluation.toString().toLowerCase().replace("_", " "));
        }
    }

    static class RiskEvaluationDeserializer extends JsonDeserializer<RiskEvaluation> {

        public RiskEvaluationDeserializer() {
            super();
        }

		@Override
		public RiskEvaluation deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
			String riskEvaluationStr = parser.getText().replace("<", "").replace(">", "");
			return RiskEvaluation.valueOf(riskEvaluationStr.toUpperCase().replace(" ", "_"));
		}
    }
}
