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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.container.ItemAdapter;
import com.vaadin.data.Item;

/**
 * A {@link ItemIdentifierProvider} for container {@link Item}s using an {@link ItemAdapter} for items conversion.
 * 
 * @param <ITEM> Item data type
 * 
 * @since 5.0.0
 */
public class ContainerItemIdentifierProvider<ITEM> implements ItemIdentifierProvider<Item, Object> {

	private static final long serialVersionUID = -4209319399665820223L;

	private final ItemIdentifierProvider<ITEM, ?> provider;
	private final ItemAdapter<ITEM> adapter;
	private final Configuration<?> configuration;

	/**
	 * Constructor
	 * @param configuration Configuration
	 * @param provider Concrete item identifier provider (not null)
	 * @param adapter Item adapter (not null)
	 */
	public ContainerItemIdentifierProvider(ItemIdentifierProvider<ITEM, ?> provider, ItemAdapter<ITEM> adapter,
			Configuration<?> configuration) {
		super();
		ObjectUtils.argumentNotNull(provider, "ItemIdentifierProvider must be not null");
		ObjectUtils.argumentNotNull(adapter, "ItemAdapter must be not null");
		this.provider = provider;
		this.adapter = adapter;
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemIdentifierProvider#getItemId(java.lang.Object)
	 */
	@Override
	public Object getItemId(Item item) {
		if (item != null) {
			return provider.getItemId(adapter.restore(configuration, item));
		}
		return null;
	}

}
