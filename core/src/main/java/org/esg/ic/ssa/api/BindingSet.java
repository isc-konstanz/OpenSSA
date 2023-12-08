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
package org.esg.ic.ssa.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

public class BindingSet<B extends Binding> extends ArrayList<B> implements List<B> {
	private static final long serialVersionUID = 8030595352845833206L;

	public BindingSet(B binding) {
		super();
		add(binding);
	}

	public BindingSet() {
		super();
	}

	public static class BindingDeserializerModifier<B extends Binding> extends BeanDeserializerModifier {

    	private final Class<B> bindingType;

        public BindingDeserializerModifier(Class<B> bindingType) {
        	super();
        	this.bindingType = bindingType;
        }

        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, 
        		BeanDescription description, JsonDeserializer<?> deserializer) {
        	Class<?> jsonClass = description.getBeanClass();
            if (jsonClass.equals(Binding.class)) {
                return new JsonDeserializer<B>() {
                	@Override
                	public B deserialize(JsonParser parser, DeserializationContext context) throws
                			IOException, JsonProcessingException {
                        
                        return new ObjectMapper()
                	    		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                	    		.readValue(parser, bindingType);
                    }
                };
            }
            return deserializer;
        }
	}
}
