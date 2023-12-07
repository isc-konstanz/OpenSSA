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

import com.fasterxml.jackson.annotation.JsonAlias;

public class ResponseHandle<B extends Binding> {

    @JsonAlias({"bindingSet", "resultBindingSet"})
    List<B> resultBindingSet;

	private List<ExchangeInfo> exchangeInfo;

	public ResponseHandle(List<B> resultBindingSet, List<ExchangeInfo> exchangeInfo) {
		this.resultBindingSet = resultBindingSet;
		this.exchangeInfo = exchangeInfo;
	}

	public ResponseHandle() {
	}

	public List<B> getResultBindingSet() {
		return resultBindingSet;
	}

	public void setResultBindingSet(List<B> resultBindingSet) {
		this.resultBindingSet = resultBindingSet;
	}

	public List<ExchangeInfo> getExchangeInfo() {
		return exchangeInfo;
	}

	public void setExchangeInfo(List<ExchangeInfo> exchangeInfo) {
		this.exchangeInfo = exchangeInfo;
	}
}
