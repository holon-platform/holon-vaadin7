/*
 * Copyright 2000-2016 Holon TDCN.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.data;

import java.util.function.Function;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.internal.data.DatastoreItemDataProvider;
import com.holonplatform.vaadin.internal.data.DefaultItemDataProvider;
import com.holonplatform.vaadin.internal.data.ItemDataProviderWrapper;

/**
 * Iterface to load items data from a data source.
 * 
 * @param <ITEM> Item data type
 * 
 * @since 5.0.0
 */
public interface ItemDataProvider<ITEM> extends ItemSetCounter, ItemSetLoader<ITEM>, ItemRefresher<ITEM> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemRefresher#refresh(java.lang.Object)
	 */
	@Override
	default ITEM refresh(ITEM item) throws UnsupportedOperationException, DataAccessException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create an {@link ItemDataProvider} using given operations.
	 * @param <ITEM> Item data type
	 * @param counter Items counter (not null)
	 * @param loader Items loader (not null)
	 * @return A new {@link ItemDataProvider} instance
	 */
	static <ITEM> ItemDataProvider<ITEM> create(ItemSetCounter counter, ItemSetLoader<ITEM> loader) {
		return new DefaultItemDataProvider<>(counter, loader);
	}

	/**
	 * Create an {@link ItemDataProvider} using given operations.
	 * @param <ITEM> Item data type
	 * @param counter Items counter (not null)
	 * @param loader Items loader (not null)
	 * @param refresher Item refresher
	 * @return A new {@link ItemDataProvider} instance
	 */
	static <ITEM> ItemDataProvider<ITEM> create(ItemSetCounter counter, ItemSetLoader<ITEM> loader,
			ItemRefresher<ITEM> refresher) {
		return new DefaultItemDataProvider<>(counter, loader, refresher);
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore}.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param propertySet Property set to load
	 */
	static ItemDataProvider<PropertyBox> create(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet) {
		return new DatastoreItemDataProvider(datastore, target, propertySet);
	}

	/**
	 * Create a new {@link ItemDataProvider} which wraps a concrete data provider and converts items into a different
	 * type using a converter function.
	 * @param provider Concrete data privider (not null)
	 * @param converter Converter function (not null)
	 * @return the data provider wrapper
	 */
	static <T, ITEM> ItemDataProvider<T> convert(ItemDataProvider<ITEM> provider, Function<ITEM, T> converter) {
		return new ItemDataProviderWrapper<>(provider, converter);
	}

}
