/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin7.internal.data.container;

import java.util.stream.Stream;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin7.data.ItemDataProvider;
import com.holonplatform.vaadin7.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin7.data.container.ItemAdapter;
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
	private final Configuration<?> configuration;
	private final ItemAdapter<ITEM> adapter;

	/**
	 * Constructor
	 * @param provider Concrete item data provider (not null)
	 * @param configuration Data source configuration
	 * @param adapter Item adapter (not null)
	 */
	public ContainerItemDataProvider(ItemDataProvider<ITEM> provider, Configuration<?> configuration,
			ItemAdapter<ITEM> adapter) {
		super();
		ObjectUtils.argumentNotNull(provider, "ItemDataProvider must be not null");
		ObjectUtils.argumentNotNull(adapter, "ItemAdapter must be not null");
		this.provider = provider;
		this.configuration = configuration;
		this.adapter = adapter;
	}

	/**
	 * Get the data source configuration
	 * @return the configuration
	 */
	protected Configuration<?> getConfiguration() {
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetCounter#size(com.holonplatform.core.query.QueryConfigurationProvider)
	 */
	@Override
	public long size(QueryConfigurationProvider configuration) throws DataAccessException {
		return provider.size(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetLoader#load(com.holonplatform.core.query.QueryConfigurationProvider,
	 * int, int)
	 */
	@Override
	public Stream<Item> load(QueryConfigurationProvider configuration, int offset, int limit)
			throws DataAccessException {
		return provider.load(configuration, offset, limit).map(i -> adapter.adapt(getConfiguration(), i));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataProvider#refresh(java.lang.Object)
	 */
	@Override
	public Item refresh(Item item) throws UnsupportedOperationException, DataAccessException {
		ITEM itm = provider.refresh(adapter.restore(configuration, item));
		if (itm != null) {
			return adapter.adapt(configuration, itm);
		}
		return null;
	}

}
