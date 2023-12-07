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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExchangeInfo {

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private String initiator;

    private String knowledgeBaseId;
    private String knowledgeInteractionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDateTime exchangeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDateTime exchangeEnd;

    private String status;
    private String failedMessage;

    @JsonAlias({"bindingSet", "argumentBindingSet"})
    private List<Map<String, String>> argumentBindingSet;

    @JsonInclude(Include.NON_NULL)
    private List<Map<String, String>> resultBindingSet;

    public ExchangeInfo(String initiator,
    		String knowledgeBaseId,
    		String knowledgeInteractionId,
			LocalDateTime exchangeStart,
			LocalDateTime exchangeEnd,
			String status,
			String failedMessage,
			List<Map<String, String>> argumentBindingSet,
			List<Map<String, String>> resultBindingSet) {
		this.initiator = initiator;
		this.knowledgeBaseId = knowledgeBaseId;
		this.knowledgeInteractionId = knowledgeInteractionId;
		this.exchangeStart = exchangeStart;
		this.exchangeEnd = exchangeEnd;
		this.status = status;
		this.failedMessage = failedMessage;
		this.argumentBindingSet = argumentBindingSet;
		this.resultBindingSet = resultBindingSet;
	}

	public ExchangeInfo() {
    }

    public String getKnowledgeInteractionId() {
        return knowledgeInteractionId;
    }

    public void setKnowledgeInteractionId(String knowledgeInteractionId) {
        this.knowledgeInteractionId = knowledgeInteractionId;
    }

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public String getKnowledgeBaseId() {
		return knowledgeBaseId;
	}

	public void setKnowledgeBaseId(String knowledgeBaseId) {
		this.knowledgeBaseId = knowledgeBaseId;
	}

	public LocalDateTime getExchangeStart() {
		return exchangeStart;
	}

	public void setExchangeStart(String exchangeStart) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		this.exchangeStart = LocalDateTime.parse(exchangeStart, formatter);
	}

	public void setExchangeStart(LocalDateTime exchangeStart) {
		this.exchangeStart = exchangeStart;
	}

	public LocalDateTime getExchangeEnd() {
		return exchangeEnd;
	}

	public void setExchangeEnd(String exchangeEnd) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		this.exchangeEnd = LocalDateTime.parse(exchangeEnd, formatter);
	}

	public void setExchangeEnd(LocalDateTime exchangeEnd) {
		this.exchangeEnd = exchangeEnd;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFailedMessage() {
		return failedMessage;
	}

	public void setFailedMessage(String failedMessage) {
		this.failedMessage = failedMessage;
	}

	public List<Map<String, String>> getArgumentBindingSet() {
		return argumentBindingSet;
	}

	public void setArgumentBindingSet(List<Map<String, String>> argumentBindingSet) {
		this.argumentBindingSet = argumentBindingSet;
	}

	public List<Map<String, String>> getResultBindingSet() {
		return resultBindingSet;
	}

	public void setResultBindingSet(List<Map<String, String>> resultBindingSet) {
		this.resultBindingSet = resultBindingSet;
	}

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
            
        } catch (JsonProcessingException e) {
        	return e.getMessage();
        }
    }

}
