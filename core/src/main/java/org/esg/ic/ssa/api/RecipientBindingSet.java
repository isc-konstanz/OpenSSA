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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipientBindingSet {

	private List<Binding> bindingSet;

	private RecipientSelector recipientSelector;

	public RecipientBindingSet(List<Binding> bindingSet, RecipientSelector recipientSelector) {
		this.bindingSet = bindingSet;
		this.recipientSelector = recipientSelector;
	}

	public RecipientBindingSet(Binding binding, RecipientSelector recipientSelector) {
		this(new ArrayList<Binding>(Arrays.asList(binding)), recipientSelector);
	}

	public RecipientBindingSet(RecipientSelector recipientSelector) {
		this(new ArrayList<Binding>(new ArrayList<Binding>()), recipientSelector);
	}

	public boolean addBinding(Binding binding) {
		return bindingSet.add(binding);
	}

	public List<Binding> getBindingSet() {
		return bindingSet;
	}

	public void setBindingSet(List<Binding> bindingSet) {
		this.bindingSet = bindingSet;
	}

	public RecipientSelector getRecipientSelector() {
		return recipientSelector;
	}

	public void setRecipientSelector(RecipientSelector recipientSelector) {
		this.recipientSelector = recipientSelector;
	}
}
