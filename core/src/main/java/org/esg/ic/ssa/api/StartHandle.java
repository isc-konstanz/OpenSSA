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

public class StartHandle<B extends Binding> extends BindingSetHandle<B> {

	private String knowledgeInteractionId;
	private String handleRequestId;

	private String requestingKnowledgeBaseId;

    public StartHandle(
    		String knowledgeInteractionId, 
    		String handleRequestId, 
    		BindingSet<B> bindingSet, 
    		String requestingKnowledgeBaseId) {
        super(bindingSet);
    	this.knowledgeInteractionId = knowledgeInteractionId;
        this.handleRequestId = handleRequestId;
        this.requestingKnowledgeBaseId = requestingKnowledgeBaseId;
    }

    public StartHandle() {
    }

    public String getKnowledgeInteractionId() {
        return knowledgeInteractionId;
    }

    public void setKnowledgeInteractionId(String knowledgeInteractionId) {
        this.knowledgeInteractionId = knowledgeInteractionId;
    }

    public String getHandleRequestId() {
        return handleRequestId;
    }

    public void setHandleRequestId(String handleRequestId) {
        this.handleRequestId = handleRequestId;
    }

    public String getRequestingKnowledgeBaseId() {
        return requestingKnowledgeBaseId;
    }

    public void setRequestingKnowledgeBaseId(String requestingKnowledgeBaseId) {
        this.requestingKnowledgeBaseId = requestingKnowledgeBaseId;
    }
}
