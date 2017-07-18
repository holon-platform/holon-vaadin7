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
package com.holonplatform.vaadin.internal.components;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.MultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.MultiSelectInputBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.data.container.ItemAdapter;
import com.holonplatform.vaadin.data.container.PropertyBoxItem;
import com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder;
import com.holonplatform.vaadin.internal.data.PropertyItemIdentifier;
import com.holonplatform.vaadin.internal.data.container.PropertyBoxItemAdapter;
import com.vaadin.ui.Field;

/**
 * Default multiple select {@link Field} implementation.
 * 
 * @param <T> Field type
 * 
 * @since 5.0.0
 */
public class MultiSelectField<T, ITEM> extends AbstractSelectField<Set<T>, T, ITEM> implements MultiSelect<T> {

	private static final long serialVersionUID = -7662977233168084151L;

	/**
	 * Constructor
	 * @param type Selection value type
	 * @param renderingMode Rendering mode
	 */
	@SuppressWarnings("unchecked")
	public MultiSelectField(Class<? extends T> type, RenderingMode renderingMode) {
		super((Class<? extends Set<T>>) (Class<?>) Set.class, renderingMode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		final Set<T> value = getValue();
		return (value != null) ? Collections.unmodifiableSet(value) : Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselectAll()
	 */
	@Override
	public void deselectAll() {
		clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.MultiSelect#select(java.lang.Iterable)
	 */
	@Override
	public void select(Iterable<T> items) {
		ObjectUtils.argumentNotNull(items, "Items to select must be not null");
		items.forEach(i -> {
			ObjectUtils.argumentNotNull(i, "Items to select must be not null");
			getInternalField().select(i);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.MultiSelect#deselect(java.lang.Iterable)
	 */
	@Override
	public void deselect(Iterable<T> items) {
		ObjectUtils.argumentNotNull(items, "Items to deselect must be not null");
		items.forEach(i -> {
			ObjectUtils.argumentNotNull(i, "Items to deselect must be not null");
			getInternalField().unselect(i);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.MultiSelect#selectAll()
	 */
	@Override
	public void selectAll() {
		Collection<T> ids = getItemIds();
		if (ids != null) {
			select(ids);
		}
	}

	// Builder

	/**
	 * Base {@link MultiSelect} builder.
	 * @param <T> Selection type
	 */
	public static class Builder<T> extends
			AbstractSelectFieldBuilder<Set<T>, MultiSelect<T>, T, T, MultiSelectField<T, T>, MultiSelectInputBuilder<T>>
			implements MultiSelectInputBuilder<T> {

		/**
		 * Constructor
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 */
		public Builder(Class<? extends T> type, RenderingMode renderingMode) {
			super(new MultiSelectField<>(type, renderingMode));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected Builder<T> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.MultiSelectFieldBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public MultiSelectInputBuilder<T> dataSource(ItemDataProvider<T> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "Item data provider must be not null");
			dataSourceBuilder.dataSource(dataProvider);
			dataSourceBuilder.itemIdentifier(ItemIdentifierProvider.identity());
			dataProviderConfigured = true;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.MultiSelectFieldBuilder#itemAdapter(com.holonplatform.vaadin.
		 * data.container.ItemAdapter)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public MultiSelectInputBuilder<T> itemAdapter(ItemAdapter<T> itemAdapter) {
			dataSourceBuilder.itemAdapter(itemAdapter);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#items(java.lang.Iterable)
		 */
		@Override
		public MultiSelectInputBuilder<T> items(Iterable<T> items) {
			ObjectUtils.argumentNotNull(items, "Items must be not null");
			this.items.clear();
			items.forEach(i -> this.items.add(i));
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#addItem(java.lang.Object)
		 */
		@Override
		public MultiSelectInputBuilder<T> addItem(T item) {
			ObjectUtils.argumentNotNull(item, "Item must be not null");
			this.items.add(item);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#configureDataSource(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected void configureDataSource(MultiSelectField<T, T> instance) {
			super.configureDataSource(instance);
			if (!instance.getItemDataExtractor().isPresent()) {
				// default extractor
				instance.setItemDataExtractor((c, id, i) -> (T) id);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelect(com.
		 * holonframework.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected MultiSelect<T> buildSelect(MultiSelectField<T, T> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelectAsField(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected Field<Set<T>> buildSelectAsField(MultiSelectField<T, T> instance) {
			return instance;
		}

	}

	/**
	 * Base {@link MultiSelect} builder with {@link Property} data source support.
	 * @param <T> Selection type
	 */
	public static class PropertyBuilder<T> extends
			AbstractSelectFieldBuilder<Set<T>, MultiSelect<T>, T, PropertyBox, MultiSelectField<T, PropertyBox>, MultiPropertySelectInputBuilder<T>>
			implements MultiPropertySelectInputBuilder<T> {

		/**
		 * Constructor
		 * @param selectProperty Selection (and identifier) property
		 * @param renderingMode Rendering mode
		 */
		@SuppressWarnings("unchecked")
		public PropertyBuilder(Property<T> selectProperty, RenderingMode renderingMode) {
			super(new MultiSelectField<>(selectProperty.getType(), renderingMode));
			dataSourceBuilder.withProperty(selectProperty, selectProperty.getType());
			dataSourceBuilder.itemIdentifier(new PropertyItemIdentifier<>(selectProperty));
			itemAdapter(new PropertyBoxItemAdapter());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.PropertySelectFieldBuilder#withProperties(java.lang.Iterable)
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <P extends Property> MultiPropertySelectInputBuilder<T> withProperties(Iterable<P> properties) {
			ObjectUtils.argumentNotNull(properties, "Properties must be not null");
			properties.forEach(p -> dataSourceBuilder.withProperty(p, p.getType()));
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.PropertySelectFieldBuilder#dataSource(com.holonplatform.vaadin
		 * .data.ItemDataProvider)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public MultiPropertySelectInputBuilder<T> dataSource(ItemDataProvider<PropertyBox> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
			dataSourceBuilder.dataSource(dataProvider);
			dataProviderConfigured = true;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.PropertySelectFieldBuilder#itemAdapter(com.holonplatform.
		 * vaadin.data.container.ItemAdapter)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public MultiPropertySelectInputBuilder<T> itemAdapter(ItemAdapter<PropertyBox> itemAdapter) {
			ObjectUtils.argumentNotNull(itemAdapter, "ItemAdapter must be not null");
			dataSourceBuilder.itemAdapter(itemAdapter);
			// set extractor
			getInstance().setItemDataExtractor((c, id, i) -> itemAdapter.restore(c, i));
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#configureDataSource(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected void configureDataSource(MultiSelectField<T, PropertyBox> instance) {
			super.configureDataSource(instance);
			if (!instance.getItemDataExtractor().isPresent()) {
				// default extractor
				instance.setItemDataExtractor((c, id, i) -> ((PropertyBoxItem) i).getPropertyBox());
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelect(com.
		 * holonframework.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected MultiSelect<T> buildSelect(MultiSelectField<T, PropertyBox> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelectAsField(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected Field<Set<T>> buildSelectAsField(MultiSelectField<T, PropertyBox> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected MultiPropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

}
