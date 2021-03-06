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
package com.holonplatform.vaadin7.internal.components.builders;

import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin7.components.Input;
import com.holonplatform.vaadin7.components.ItemSet.ItemCaptionGenerator;
import com.holonplatform.vaadin7.components.ItemSet.ItemIconGenerator;
import com.holonplatform.vaadin7.components.builders.BaseSelectInputBuilder;
import com.holonplatform.vaadin7.components.builders.SelectInputBuilder;
import com.holonplatform.vaadin7.data.container.ItemDataSourceContainer;
import com.holonplatform.vaadin7.data.container.ItemDataSourceContainerBuilder.BaseItemDataSourceContainerBuilder;
import com.holonplatform.vaadin7.internal.components.AbstractSelectField;
import com.holonplatform.vaadin7.internal.data.container.BeanItemAdapter;
import com.vaadin.server.Resource;
import com.vaadin.ui.Field;

/**
 * Base {@link SelectInputBuilder} implementation.
 * 
 * @param <T> Field type
 * @param <S> Selection type
 * @param <ITEM> Selection item type
 * @param <C> Internal field type
 * @param <B> Concrete builder type
 * @param <I> Internal component type
 * 
 * @since 5.0.0
 */
public abstract class AbstractSelectFieldBuilder<T, C extends Input<T>, S, ITEM, I extends AbstractSelectField<T, S, ITEM>, B extends BaseSelectInputBuilder<T, C, S, ITEM, B>>
		extends AbstractFieldBuilder<T, C, I, B> implements BaseSelectInputBuilder<T, C, S, ITEM, B> {

	/**
	 * Explicitly added selection items
	 */
	protected final List<S> items = new LinkedList<>();

	/**
	 * Data source container builder
	 */
	@SuppressWarnings("rawtypes")
	protected final BaseItemDataSourceContainerBuilder dataSourceBuilder = ItemDataSourceContainer.builder();

	protected boolean dataProviderConfigured = false;

	/**
	 * Constructor
	 * @param instance Field instance to build
	 */
	@SuppressWarnings("unchecked")
	public AbstractSelectFieldBuilder(I instance) {
		super(instance);
		// default adapter
		dataSourceBuilder.itemAdapter(new BeanItemAdapter());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.SelectFieldBuilder#itemCaptionGenerator(com.holonplatform.vaadin.
	 * components.ItemSetComponent.ItemCaptionGenerator)
	 */
	@Override
	public B itemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
		getInstance().setItemCaptionGenerator(itemCaptionGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#itemIconGenerator(com.holonplatform.vaadin.
	 * components.ItemSetComponent.ItemIconGenerator)
	 */
	@Override
	public B itemIconGenerator(ItemIconGenerator<ITEM> itemIconGenerator) {
		getInstance().setItemIconGenerator(itemIconGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#itemCaption(java.lang.Object,
	 * com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B itemCaption(ITEM item, Localizable caption) {
		getInstance().setItemCaption(item, caption);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#itemIcon(java.lang.Object,
	 * com.vaadin.server.Resource)
	 */
	@Override
	public B itemIcon(ITEM item, Resource icon) {
		getInstance().setItemIcon(item, icon);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#autoRefresh(boolean)
	 */
	@Override
	public B autoRefresh(boolean autoRefresh) {
		dataSourceBuilder.autoRefresh(autoRefresh);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#batchSize(int)
	 */
	@Override
	public B batchSize(int batchSize) {
		dataSourceBuilder.batchSize(batchSize);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#maxCacheSize(int)
	 */
	@Override
	public B maxCacheSize(int maxCacheSize) {
		dataSourceBuilder.maxCacheSize(maxCacheSize);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#withQueryConfigurationProvider(
	 * com.holonplatform.core.query.QueryConfigurationProvider)
	 */
	@Override
	public B withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		dataSourceBuilder.withQueryConfigurationProvider(queryConfigurationProvider);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#fixedFilter(com.holonplatform.
	 * core.query.QueryFilter)
	 */
	@Override
	public B fixedFilter(QueryFilter filter) {
		dataSourceBuilder.fixedFilter(filter);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#fixedSort(com.holonplatform.
	 * core.query.QuerySort)
	 */
	@Override
	public B fixedSort(QuerySort sort) {
		dataSourceBuilder.fixedSort(sort);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#defaultSort(com.holonplatform.
	 * core.query.QuerySort)
	 */
	@Override
	public B defaultSort(QuerySort sort) {
		dataSourceBuilder.defaultSort(sort);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#queryParameter(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public B queryParameter(String name, Object value) {
		dataSourceBuilder.queryParameter(name, value);
		return builder();
	}

	/**
	 * Configure items data source
	 * @param instance Building instance
	 */
	protected void configureDataSource(I instance) {
		if (!items.isEmpty()) {
			items.forEach(i -> instance.addItem(i));
		} else {
			// set data source
			if (dataProviderConfigured) {
				instance.setContainerDataSource(dataSourceBuilder.build());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected C build(I instance) {
		configureDataSource(instance);
		return buildSelect(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
	 * AbstractField)
	 */
	@Override
	protected Field<T> buildAsField(I instance) {
		configureDataSource(instance);
		return buildSelectAsField(instance);
	}

	protected abstract C buildSelect(I instance);

	protected abstract Field<T> buildSelectAsField(I instance);

}
