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

import org.esg.ic.ssa.api.CommunicativeAct;
import org.esg.ic.ssa.api.GraphPrefixes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class KnowledgeInteraction {

	protected final String knowledgeInteractionType;

    @JsonInclude(Include.NON_NULL)
    protected final String knowledgeInteractionName;

    @JsonInclude(Include.NON_NULL)
    protected CommunicativeAct communicativeAct;

    @JsonInclude(Include.NON_NULL)
	@JsonSerialize(using = GraphPrefixes.Serializer.class)
    protected GraphPrefixes prefixes;

    protected KnowledgeInteraction(KnowledgeInteractionType knowledgeInteractionType) {
    	this(knowledgeInteractionType, null);
    }

    protected KnowledgeInteraction(KnowledgeInteractionType knowledgeInteractionType, 
    		String knowledgeInteractionName) {
    	this.knowledgeInteractionType = knowledgeInteractionType.toJsonString();
    	this.knowledgeInteractionName = knowledgeInteractionName;
    }

    public String getKnowledgeInteractionType() {
        return knowledgeInteractionType;
    }

    public String getKnowledgeInteractionName() {
        return knowledgeInteractionName;
    }

    public CommunicativeAct getCommunicativeAct() {
        return communicativeAct;
    }

    public void setCommunicativeAct(CommunicativeAct communicativeAct) {
        this.communicativeAct = communicativeAct;
    }

    public GraphPrefixes getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(GraphPrefixes prefixes) {
        this.prefixes = prefixes;
    }
}
