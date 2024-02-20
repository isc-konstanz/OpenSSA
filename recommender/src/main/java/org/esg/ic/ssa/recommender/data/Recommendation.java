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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.esg.ic.ssa.api.Binding;
import org.esg.ic.ssa.recommender.data.RiskEvaluation.RiskEvaluationDeserializer;
import org.esg.ic.ssa.recommender.data.RiskEvaluation.RiskEvaluationSerializer;
import org.esg.ic.ssa.recommender.data.RiskLevel.RiskLevelDeserializer;
import org.esg.ic.ssa.recommender.data.RiskLevel.RiskLevelSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
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

public class Recommendation implements Binding {
	private static final long serialVersionUID = -673813135192249500L;

	@JsonDeserialize(using = UUIDDeserializer.class)
    private UUID id;

    @JsonProperty("end_datetime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime endDatetime;

    @JsonProperty("start_datetime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime startDatetime;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("created_by")
    @JsonDeserialize(using = StringDeserializer.class)
    private String createdBy;

    @JsonProperty("zip_code")
    @JsonDeserialize(using = StringDeserializer.class)
    private String zipCode;

    @JsonProperty("country_code")
    @JsonDeserialize(using = StringDeserializer.class)
    private String countryCode;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime datetime;

    @JsonProperty("risk_level")
    @JsonSerialize(using = RiskLevelSerializer.class)
    @JsonDeserialize(using = RiskLevelDeserializer.class)
    private RiskLevel riskLevel;

    @JsonProperty("risk_evaluation")
    @JsonSerialize(using = RiskEvaluationSerializer.class)
    @JsonDeserialize(using = RiskEvaluationDeserializer.class)
    private RiskEvaluation riskEvaluation;


    public UUID getId() {
		return id;
	}

	public ZonedDateTime getEndDatetime() {
		return endDatetime;
	}

	public ZonedDateTime getStartDatetime() {
		return startDatetime;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public ZonedDateTime getDatetime() {
		return datetime;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public RiskEvaluation getRiskEvaluation() {
		return riskEvaluation;
	}

	@Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
            
        } catch (JsonProcessingException e) {
        	return e.getMessage();
        }
    }

    static class UUIDDeserializer extends JsonDeserializer<UUID> {

        public UUIDDeserializer() {
            super();
        }

		@Override
		public UUID deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
			return UUID.fromString(parser.getText().replace("<", "").replace(">", ""));
		}
    }

    static class StringDeserializer extends JsonDeserializer<String> {

        public StringDeserializer() {
            super();
        }

		@Override
		public String deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
			String text = parser.getText().replace("<", "").replace(">", "");
			if (text.equals("null")) {
				return null;
			}
			return text;
		}
    }

    static class DateTimeSerializer extends JsonSerializer<ZonedDateTime> {

        public DateTimeSerializer() {
            super();
        }

        @Override
        public void serialize(ZonedDateTime dateTime, JsonGenerator generator, SerializerProvider provider) 
    		    throws IOException, JsonProcessingException {
            generator.writeString(DateTimeFormatter.ISO_DATE_TIME.format(dateTime));
        }
    }

    static class DateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

        public DateTimeDeserializer() {
            super();
        }

		@Override
		public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
	        String dateTimeStr = parser.getText().replaceAll("<", "").replaceAll(">", "");
	        return ZonedDateTime.of(LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME), ZoneId.of("UTC"));
		}
    }
}
