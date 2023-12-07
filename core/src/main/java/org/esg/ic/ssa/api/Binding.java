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

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface Binding extends Serializable {

	public default Set<String> keySet() {
		Set<String> keys = new HashSet<String>();
		Class<?> type = getClass();
		while(type.getSuperclass() != null) {
			keys.addAll(Arrays.asList(type.getDeclaredFields())
					.stream()
					.filter(f -> !Modifier.isStatic(f.getModifiers()))
					.map(f -> f.getName())
					.collect(Collectors.toSet()));
		    type = type.getSuperclass();
		}
		return keys;
	}

	@SuppressWarnings("unchecked")
	public default <B extends Binding> List<B> toList() {
		return new ArrayList<B>(Arrays.asList((B) this));
	}
}
