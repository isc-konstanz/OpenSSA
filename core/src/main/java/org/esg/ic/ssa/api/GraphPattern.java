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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.graph.Node_Variable;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.lang.arq.ARQParser;
import org.apache.jena.sparql.lang.arq.ParseException;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.esg.ic.ssa.GenericAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class GraphPattern {

    private static final Logger logger = LoggerFactory.getLogger(GraphPattern.class);

	private final GraphPrefixes prefixes;

	private final String pattern;

	public GraphPattern(GraphPrefixes prefixes, String... patternFragments) throws GenericAdapterException {
		this.prefixes = prefixes;
		this.pattern = String.join(" ", patternFragments);
		if (logger.isDebugEnabled()) {
			try {
				logger.debug("Built graph pattern: \n{}", getGraphPattern());
				
			} catch (ParseException e) {
				logger.error("Error parsing graph pattern: {}", e);
				throw new GenericAdapterException(e);
			}
		}
	}

	public GraphPattern(GraphPrefixes prefixes, InputStream patternStream) throws GenericAdapterException {
		this(prefixes, readPatternFragments(patternStream));
	}

	public GraphPattern(InputStream patternStream) throws GenericAdapterException {
		this(GraphPrefixes.DEFAULT, readPatternFragments(patternStream));
	}

	public GraphPattern(String... patternFragments) throws GenericAdapterException {
		this(GraphPrefixes.DEFAULT, patternFragments);
	}

	public GraphPrefixes getPrefixes() {
		return this.prefixes;
	}

	public String getPattern() {
		return this.pattern;
	}

	/**
	 * @return A list of all the variable names (excluding the '?') occurring in
	 * this {@link GraphPattern}.
	 * 
	 * @throws ParseException if anything 
	 */
	public Set<String> getVariables() throws ParseException {
		List<TriplePath> triples = this.getGraphPattern().getPattern().getList();
		return triples.stream()
				.flatMap(t -> Stream.of(t.getSubject(), t.getPredicate(), t.getObject()))
				.map(n -> {
					if (n instanceof Node_Variable) {
						return n.getName();
					} else {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	private ElementPathBlock getGraphPattern() throws ParseException {
		logger.trace("Prefixes: {}; Pattern: {}", this.prefixes, this.pattern);
		ARQParser parser = new ARQParser(new StringReader(this.pattern));
		parser.setPrologue(new Prologue(this.prefixes));

		Element element = parser.GroupGraphPatternSub();
		ElementGroup elementGroup = (ElementGroup) element;
		logger.trace("Parsed knowledge: {}", element);

		Element lastElement = elementGroup.getLast();
		if (!(lastElement instanceof ElementPathBlock)) {
			logger.error("The knowledge '{}' should be parseable to a ElementPathBlock", this.pattern);
			throw new ParseException(
					"The knowledge should be parseable to a ARQ ElementPathBlock " +
					"(i.e. a BasicGraphPattern in the SPARQL syntax specification)");
		}
		return (ElementPathBlock) elementGroup.getLast();
	}

    public boolean validateBindingSet(final List<Binding> bindingSet) {
        logger.debug("Validating binding set: {}", bindingSet);
        try {
            Set<String> variables = getVariables();
            return bindingSet.stream().allMatch(b -> validateBinding(b, variables));
    		
		} catch (ParseException e) {
			logger.error("Unable to validate graph pattern: {}", e);
			return false;
		}
    }

    public boolean validateBinding(final Binding binding) {
        logger.debug("Validating binding: {}", binding);
        try {
            Set<String> variables = getVariables();
    		return validateBinding(binding, variables);
    		
		} catch (ParseException e) {
			logger.error("Unable to validate graph pattern: {}", e);
			return false;
		}
    }

    private boolean validateBinding(
    		final Binding binding,
    		final Set<String> variables) {
        return variables.stream().allMatch(v -> binding.keySet().stream().anyMatch(v::contains));
    }

	@Override
	public String toString() {
		return this.pattern != null ? this.pattern : "";
	}

	private static String[] readPatternFragments(InputStream patternStream) {
		BufferedReader patternReader = new BufferedReader(new InputStreamReader(patternStream));
		List<String> patternFragments = new ArrayList<String>();
		try {
			String patternline;
			while ((patternline = patternReader.readLine()) != null) {
				patternFragments.add(patternline);
			}
		} catch (IOException e) {
			logger.warn("Error while reading graph pattern stream: " + e.getMessage());
		}
		return patternFragments.stream().toArray(String[]::new);
	}

    public static class Serializer extends JsonSerializer<GraphPattern> {

        public Serializer() {
            super();
        }

        @Override
        public void serialize(GraphPattern pattern, JsonGenerator generator, SerializerProvider provider) 
                throws IOException, JsonProcessingException {
        	String patternJson = pattern.toString();
        	if (!patternJson.isEmpty()) {
                generator.writeNumber('"' + pattern.toString()  + '"');
        	}
        }
    }

}
