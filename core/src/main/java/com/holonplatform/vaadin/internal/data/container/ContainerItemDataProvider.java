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
package com.holonplatform.vaadin.internal.data.container;

import java.util.stream.Stream;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.container.ItemAdapter;
import com.vaadin.data.Item;

/**
 * An {@link ItemDataProvider} using an {@link ItemAdapter} to convert item data into container {@link Item}s
 * 
 * @param <ITEM> Item data type
 * 
 * @since 5.0.0
 */
public class ContainerItemDataProvider<ITEM> implements ItemDataProvider<Item> {

	private static final long serialVersionUID = 6805081577415808950L;

	private final ItemDataProvider<ITEM> provider;
	private final ItemAdapter<ITEM> adapter;

	/**
	 * Constructor
	 * @param provider Concrete item data provider (not null)
	 * @param adapter Item adapter (not null)
	 */
	public ContainerItemDataProvider(ItemDataProvider<ITEM> provider, ItemAdapter<ITEM> adapter) {
		super();
		ObjectUtils.argumentNotNull(provider, "ItemDataProvider must be not null");
		ObjectUtils.argumentNotNull(adapter, "ItemAdapter must be not null");
		this.provider = provider;
		this.adapter = adapter;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.ItemDataSource.ItemSetCounter#size(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration)
	 */
	@Override
	public long size(Configuration<?> configuration) throws DataAccessException {
		return provider.size(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.ItemDataSource.ItemSetLoader#load(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration, int, int)
	 */
	@Override
	public Stream<Item> load(Configuration<?> configuration, int offset, int limit) throws DataAccessException {
		return provider.load(configuration, offset, limit).map(i -> adapter.adapt(configuration, i));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataProvider#refresh(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration, java.lang.Object)
	 */
	@Override
	public Item refresh(Configuration<?> configuration, Item item)
			throws UnsupportedOperationException, DataAccessException {
		ITEM itm = provider.refresh(configuration, adapter.restore(configuration, item));
		if (itm != null) {
			return adapter.adapt(configuration, itm);
		}
		return null;
	}

}
