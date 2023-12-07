package org.esg.ic.ssa.meter.data;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class FloatValue extends TimeValue {
	private static final long serialVersionUID = -631127749660454791L;

	@JsonSerialize(using = SarefFloatSerializer.class)
    protected Float value;

    public FloatValue(String node, ValueType type, Instant timestamp, Float value) {
        super(node, type, timestamp);
        this.value = value;
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
        	jsonBuilder.append("^^xsd:float");
        	jsonBuilder.append('"');
            generator.writeNumber(jsonBuilder.toString());
        }
    }

}
