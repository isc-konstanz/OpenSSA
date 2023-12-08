package org.esg.ic.ssa.meter.data;

import java.io.IOException;
import java.time.Instant;

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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class TimeValue extends NodeValue {
	private static final long serialVersionUID = 2552330320720578853L;

	private static final String SAREF_SUFFIX = "^^xsd:float";

	@JsonSerialize(using = SarefAddressSerializer.class)
    @JsonDeserialize(using = SarefAddressDeserializer.class)
    protected String measurement;

    @JsonSerialize(using = SarefAddressSerializer.class)
    @JsonDeserialize(using = SarefAddressDeserializer.class)
    protected String property;

    protected String type;

    protected String unit;

    @JsonSerialize(using = SarefInstantSerializer.class)
    @JsonDeserialize(using = SarefInstantDeserializer.class)
    protected Instant timestamp;

    protected TimeValue(String node, ValueType type, Instant timestamp) {
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

	public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
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

    static class SarefInstantSerializer extends JsonSerializer<Instant> {

        public SarefInstantSerializer() {
            super();
        }

        @Override
        public void serialize(Instant value, JsonGenerator generator, SerializerProvider provider) 
    		    throws IOException, JsonProcessingException {
        	StringBuilder jsonBuilder = new StringBuilder();
        	jsonBuilder.append('"');
        	jsonBuilder.append('\\').append('"');
        	jsonBuilder.append(serializeInstant(value));
        	jsonBuilder.append('\\').append('"');
        	jsonBuilder.append(SAREF_SUFFIX);
        	jsonBuilder.append('"');
            generator.writeNumber(jsonBuilder.toString());
        }

        private static String serializeInstant(Instant value) throws JsonProcessingException {
        	return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(value);
        }
    }

    static class SarefInstantDeserializer extends JsonDeserializer<Instant> {

        public SarefInstantDeserializer() {
            super();
        }

		@Override
		public Instant deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
	        String instantStr = parser.getText().replace(SAREF_SUFFIX, "").replaceAll("\"", "");
	        String[] instant = instantStr.split("[^0-9]+");
	        if (instant.length == 1) {
		        return Instant.ofEpochSecond(Long.valueOf(instant[0]));
	        }
	        return Instant.ofEpochSecond(Long.valueOf(instant[0]),
	        		Integer.valueOf(instant[1]));
		}
    }
}
