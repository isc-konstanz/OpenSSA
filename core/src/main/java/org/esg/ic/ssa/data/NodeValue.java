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

	private static final String ADDRESS = "http://interconnectproject.eu/pilots/esg";

    @JsonSerialize(using = SarefAddressSerializer.class)
    @JsonDeserialize(using = SarefAddressDeserializer.class)
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

    public static class SarefAddressSerializer extends JsonSerializer<String> {

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

    public static class SarefAddressDeserializer extends JsonDeserializer<String> {

        public SarefAddressDeserializer() {
            super();
        }

		@Override
		public String deserialize(JsonParser parser, DeserializationContext context) throws
				IOException, JsonProcessingException {
        	String valueName = parser.getCurrentName();
			return parser.getText()
					.replace(ADDRESS + "/", "")
					.replace(valueName + "#", "")
					.replace("<", "").replace(">", "")
					.replace("\"", "");
		}
    }
}
