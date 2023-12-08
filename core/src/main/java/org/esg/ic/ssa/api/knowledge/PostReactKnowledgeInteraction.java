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
package org.esg.ic.ssa.api.knowledge;

import org.esg.ic.ssa.api.GraphPattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class PostReactKnowledgeInteraction extends KnowledgeInteraction {

	@JsonSerialize(using = GraphPattern.Serializer.class)
    public GraphPattern argumentGraphPattern;

    @JsonInclude(Include.NON_NULL)
	@JsonSerialize(using = GraphPattern.Serializer.class)
    public GraphPattern resultGraphPattern;

    public PostReactKnowledgeInteraction(KnowledgeInteractionType knowledgeInteractionType,
    		GraphPattern argumentGraphPattern, GraphPattern resultGraphPattern) {
    	super(knowledgeInteractionType);
        this.prefixes = argumentGraphPattern.getPrefixes();
        this.argumentGraphPattern = argumentGraphPattern;
        this.resultGraphPattern = resultGraphPattern;
    }

    public String getKnowledgeInteractionType() {
        return knowledgeInteractionType;
    }

    public GraphPattern getArgumentGraphPattern() {
        return argumentGraphPattern;
    }

    public void setArgumentGraphPattern(GraphPattern argumentGraphPattern) {
        this.argumentGraphPattern = argumentGraphPattern;
    }

    public GraphPattern getResultGraphPattern() {
        return resultGraphPattern;
    }

    public void setResultGraphPattern(GraphPattern resultGraphPattern) {
        this.resultGraphPattern = resultGraphPattern;
    }
}
