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
import java.io.Serializable;

import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sys.JenaSystem;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

public class GraphPrefixes extends PrefixMappingImpl implements PrefixMapping, Serializable {
    private static final long serialVersionUID = -1576074109745380956L;

    private GraphPrefixes() {
    }

    @Override
    public GraphPrefixes lock() { 
        return (GraphPrefixes) super.lock();
    }

    @Override
    public GraphPrefixes setNsPrefix(String prefix, String uri) {
        return (GraphPrefixes) super.setNsPrefix(prefix, uri);
    }

    @Override
    public String toString() {
    	return getNsPrefixMap().toString();
    }

    public static class Serializer extends JsonSerializer<GraphPrefixes> {

        public Serializer() {
            super();
        }

        @Override
        public void serialize(GraphPrefixes prefixes, JsonGenerator generator, SerializerProvider provider) 
                throws IOException, JsonProcessingException {
            generator.writeNumber(new ObjectMapper().writeValueAsString(prefixes.getNsPrefixMap()));
        }
    }

    public static class Factory {
        static { JenaSystem.init(); }
        public static GraphPrefixes create() { return new GraphPrefixes(); }
    }

    public static final GraphPrefixes DEFAULT = GraphPrefixes.Factory.create()
            .setNsPrefix("saref", "https://saref.etsi.org/core/")
            .setNsPrefix("rdf",   RDF.getURI())
            .setNsPrefix("xsd",   XSD.getURI());
}
