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

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendHandle<B extends Binding> {

	private String handleRequestId;

	private List<B> bindingSet;

    public SendHandle(String handleRequestId) {
        super();
        this.handleRequestId = handleRequestId;
    }

    public SendHandle(String handleRequestId, List<B> bindingSet) {
        this(handleRequestId);
        this.bindingSet = bindingSet;
    }

    public SendHandle() {
        super();
    }

    public String getHandleRequestId() {
        return handleRequestId;
    }

    public void setHandleRequestId(String handleRequestId) {
        this.handleRequestId = handleRequestId;
    }

    public List<B> getBindingSet() {
        return bindingSet;
    }

    public void setBindingSet(List<B> bindingSet) {
        this.bindingSet = bindingSet;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
            
        } catch (JsonProcessingException e) {
        	return e.getMessage();
        }
    }

    public static <B extends Binding> SendHandle<B> ofStartHandle(StartHandle<B> startHandle) {
    	return new SendHandle<B>(startHandle.getHandleRequestId());
    }

}
