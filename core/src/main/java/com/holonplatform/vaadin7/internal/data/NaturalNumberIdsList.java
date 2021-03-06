/*
 * Copyright 2010 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Modifications copyright 2016 Holon TDCN.
 */
package com.holonplatform.vaadin7.internal.data;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * Natural numbers list implementation. This list is immutable and contains integer numbers from 0 to size - 1.
 * 
 * <p>
 * Original version of this class by Tommi Laukkanen https://github.com/tlaukkan/vaadin-lazyquerycontainer
 * </p>
 */
public class NaturalNumberIdsList extends AbstractList<Integer> implements RandomAccess, Serializable {

	private static final long serialVersionUID = 4534742141377166935L;

	/**
	 * The size of the list.
	 */
	private final int size;
	/**
	 * Array containing list values. This array is created on demand.
	 */
	private Integer[] array = null;

	/**
	 * Constructor which sets the size of the constructed list.
	 *
	 * @param size Size of the constructed list.
	 */
	public NaturalNumberIdsList(final int size) {
		this.size = size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Integer[] toArray() {
		if (array == null) {
			array = new Integer[size];
			for (int i = 0; i < size; i++) {
				array[i] = i;
			}
		}
		return array.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(final T[] a) {
		if (a.length < size) {
			return (T[]) toArray();
		}
		for (int i = 0; i < size; i++) {
			a[i] = (T) Integer.valueOf(i);
		}
		if (a.length > size) {
			a[size] = null;
		}
		return a;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer get(final int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int indexOf(final Object o) {
		if (o == null) {
			return -1;
		}
		if (o instanceof Integer) {
			int i = (Integer) o;
			if (i < 0 || i >= size) {
				return -1;
			}
			return i;
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final Object o) {
		return indexOf(o) != -1;
	}

}
