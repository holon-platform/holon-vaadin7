/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin7.internal.data;

import java.util.function.Function;
import java.util.stream.Stream;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin7.data.ItemDataProvider;

/**
 * A {@link ItemDataProvider} which converts items to a different object type using a converter function.
 * 
 * @param <ITEM> Item type
 * @param <W> Wrapped type
 *
 * @since 5.0.0
 */
public class ItemDataProviderWrapper<ITEM, W> implements ItemDataProvider<ITEM> {

	private static final long serialVersionUID = 7413797681760979940L;

	private final ItemDataProvider<W> wrapped;
	private final Function<W, ITEM> converter;

	/**
	 * Constructor
	 * @param wrapped Wrapped provider (not null)
	 * @param converter Item converter (not null)
	 */
	public ItemDataProviderWrapper(ItemDataProvider<W> wrapped, Function<W, ITEM> converter) {
		super();
		ObjectUtils.argumentNotNull(wrapped, "Wrapped data provider must be not null");
		ObjectUtils.argumentNotNull(converter, "Item converter must be not null");
		this.wrapped = wrapped;
		this.converter = converter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetCounter#size(com.holonplatform.core.query.QueryConfigurationProvider)
	 */
	@Override
	public long size(QueryConfigurationProvider configuration) throws DataAccessException {
		return wrapped.size(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetLoader#load(com.holonplatform.core.query.QueryConfigurationProvider,
	 * int, int)
	 */
	@Override
	public Stream<ITEM> load(QueryConfigurationProvider configuration, int offset, int limit)
			throws DataAccessException {
		return wrapped.load(configuration, offset, limit).map(p -> converter.apply(p));
	}

}
