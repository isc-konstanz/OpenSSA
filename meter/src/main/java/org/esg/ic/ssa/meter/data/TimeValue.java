package org.esg.ic.ssa.meter.data;

import java.io.IOException;
import java.time.Instant;

import org.esg.ic.ssa.api.Binding;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class TimeValue implements Binding {
	private static final long serialVersionUID = 8991805475265666393L;

	private static final String ADDRESS = "http://interconnectproject.eu/pilots/esg";

    @JsonSerialize(using = SarefAddressSerializer.class)
    protected String node;

    @JsonSerialize(using = SarefAddressSerializer.class)
    protected String measurement;

    @JsonSerialize(using = SarefAddressSerializer.class)
    protected String property;

    protected String type;

    protected String unit;

    @JsonSerialize(using = SarefInstantSerializer.class)
    protected Instant timestamp;

    protected TimeValue(String node, ValueType type, Instant timestamp) {
        super();
        this.node = node;

        String measurementType = type.getSuffix();
        this.measurement = node + measurementType;
        this.property = node + measurementType;
        this.type = type.getType();
        this.unit = type.getUnit();
        this.timestamp = timestamp;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
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

    static class SarefAddressSerializer extends JsonSerializer<String> {

        public SarefAddressSerializer() {
            super();
        }

        @Override
        public void serialize(String value, JsonGenerator generator, SerializerProvider provider) 
    		    throws IOException, JsonProcessingException {
        	String valueName = generator.getOutputContext().getCurrentName();
            generator.writeNumber('"' + String.format("<%s/%s#%s>", ADDRESS, valueName, value) + '"');
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
        	jsonBuilder.append("^^time:Instant");
        	jsonBuilder.append('"');
            generator.writeNumber(jsonBuilder.toString());
        }

        private static String serializeInstant(Instant value) throws JsonProcessingException {
        	return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(value);
        }
    }
}
