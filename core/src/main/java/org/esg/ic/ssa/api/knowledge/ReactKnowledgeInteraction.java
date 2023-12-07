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

public class ReactKnowledgeInteraction extends PostReactKnowledgeInteraction {

	public final static KnowledgeInteractionType TYPE = KnowledgeInteractionType.REACT;

    public ReactKnowledgeInteraction(GraphPattern argumentGraphPattern, GraphPattern resultGraphPattern) {
    	super(TYPE, argumentGraphPattern, resultGraphPattern);
    }

    public ReactKnowledgeInteraction(GraphPattern argumentGraphPattern) {
    	this(argumentGraphPattern, null); //new GraphPattern());
    }
}
