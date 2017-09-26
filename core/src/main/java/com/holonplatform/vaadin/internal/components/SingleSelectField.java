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
package com.holonplatform.vaadin.internal.components;

import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.SelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SingleSelectInputBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.data.container.ItemAdapter;
import com.holonplatform.vaadin.data.container.PropertyBoxItem;
import com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder;
import com.holonplatform.vaadin.internal.data.PropertyItemIdentifier;
import com.holonplatform.vaadin.internal.data.container.PropertyBoxItemAdapter;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;

/**
 * Default single select {@link Field} implementation.
 * 
 * @param <T> Field type
 * 
 * @since 5.0.0
 */
public class SingleSelectField<T, ITEM> extends AbstractSelectField<T, T, ITEM> implements SingleSelect<T> {

	private static final long serialVersionUID = -52116276228997170L;

	/**
	 * Constructor
	 * @param type Selection value type
	 * @param renderingMode Rendering mode
	 */
	public SingleSelectField(Class<? extends T> type, RenderingMode renderingMode) {
		super(type, renderingMode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#select(java.lang.Object)
	 */
	@Override
	public void select(T item) {
		setValue(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselect(java.lang.Object)
	 */
	@Override
	public void deselect(T item) {
		clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.SingleSelect#getSelectedItem()
	 */
	@Override
	public Optional<T> getSelectedItem() {
		return Optional.of(getValue());
	}

	/**
	 * Set whether is possible to input text into the field. If the concrete select component does not support user
	 * input, this method has no effect.
	 * @param textInputAllowed true to allow entering text, false to just show the current selection
	 */
	public void setTextInputAllowed(boolean textInputAllowed) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox) getInternalField()).setTextInputAllowed(textInputAllowed);
		}
	}

	/**
	 * Sets whether to scroll the selected item visible (directly open the page on which it is) when opening the
	 * suggestions popup or not. This requires finding the index of the item, which can be expensive in many large lazy
	 * loading containers.
	 * <p>
	 * Only applies to select field with backing components supporting a suggestion popup.
	 * </p>
	 * @param scrollToSelectedItem true to find the page with the selected item when opening the selection popup
	 */
	public void setScrollToSelectedItem(boolean scrollToSelectedItem) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox) getInternalField()).setScrollToSelectedItem(scrollToSelectedItem);
		}
	}

	/**
	 * Sets the suggestion pop-up's width as a CSS string. By using relative units (e.g. "50%") it's possible to set the
	 * popup's width relative to the selection component itself.
	 * <p>
	 * Only applies to select field with backing components supporting a suggestion popup.
	 * </p>
	 * @param width the suggestion pop-up width
	 */
	public void setSuggestionPopupWidth(String width) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox) getInternalField()).setPopupWidth(width);
		}
	}

	/**
	 * Sets the option filtering mode.
	 * <p>
	 * Only applies to select field with backing components supporting a suggestion popup.
	 * </p>
	 * @param filteringMode the filtering mode to use
	 */
	public void setFilteringMode(FilteringMode filteringMode) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox) getInternalField()).setFilteringMode(filteringMode);
		}
	}

	/**
	 * Gets the current filtering mode.
	 * <p>
	 * Only applies to select field with backing components supporting a suggestion popup.
	 * </p>
	 * @return the filtering mode in use, or <code>null</code> if the backing component does not supports a suggestion
	 *         popup
	 */
	public FilteringMode getFilteringMode() {
		if (getInternalField() instanceof ComboBox) {
			return ((ComboBox) getInternalField()).getFilteringMode();
		}
		return null;
	}

	// Builders

	/**
	 * Base {@link SingleSelect} builder.
	 * @param <T> Selection type
	 */
	static abstract class AbstractSingleSelectFieldBuilder<T, ITEM, B extends SelectInputBuilder.SingleSelectConfigurator<T, ITEM, B>>
			extends AbstractSelectFieldBuilder<T, SingleSelect<T>, T, ITEM, SingleSelectField<T, ITEM>, B>
			implements SelectInputBuilder.SingleSelectConfigurator<T, ITEM, B> {

		/**
		 * Constructor
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 */
		public AbstractSingleSelectFieldBuilder(Class<? extends T> type, RenderingMode renderingMode) {
			super(new SingleSelectField<>(type, renderingMode));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#disableTextInput()
		 */
		@Override
		public B disableTextInput() {
			getInstance().setTextInputAllowed(false);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#scrollToSelectedItem(boolean)
		 */
		@Override
		public B scrollToSelectedItem(boolean scrollToSelectedItem) {
			getInstance().setScrollToSelectedItem(scrollToSelectedItem);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#suggestionPopupWidth(java.lang.String)
		 */
		@Override
		public B suggestionPopupWidth(String width) {
			getInstance().setSuggestionPopupWidth(width);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#filteringMode(com.vaadin.shared.ui.
		 * combobox.FilteringMode)
		 */
		@Override
		public B filteringMode(FilteringMode filteringMode) {
			getInstance().setFilteringMode(filteringMode);
			return builder();
		}

	}

	/**
	 * Default {@link SingleSelectInputBuilder} implementation.
	 * @param <T> Selection type
	 */
	public static class Builder<T> extends AbstractSingleSelectFieldBuilder<T, T, SingleSelectInputBuilder<T>>
			implements SingleSelectInputBuilder<T> {

		/**
		 * Constructor
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 */
		public Builder(Class<? extends T> type, RenderingMode renderingMode) {
			super(type, renderingMode);
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
		 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#items(java.lang.Iterable)
		 */
		@Override
		public SingleSelectInputBuilder<T> items(Iterable<T> items) {
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
		public SingleSelectInputBuilder<T> addItem(T item) {
			ObjectUtils.argumentNotNull(item, "Item must be not null");
			this.items.add(item);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SelectItemDataSourceBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public SingleSelectInputBuilder<T> dataSource(ItemDataProvider<T> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "Item data provider must be not null");
			dataSourceBuilder.dataSource(dataProvider);
			dataSourceBuilder.itemIdentifier(ItemIdentifierProvider.identity());
			dataProviderConfigured = true;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SelectItemDataSourceBuilder#itemAdapter(com.holonplatform.vaadin
		 * .data.container.ItemAdapter)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public SingleSelectInputBuilder<T> itemAdapter(ItemAdapter<T> itemAdapter) {
			dataSourceBuilder.itemAdapter(itemAdapter);
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
		protected void configureDataSource(SingleSelectField<T, T> instance) {
			super.configureDataSource(instance);
			if (!instance.getItemDataExtractor().isPresent()) {
				// default extractor
				instance.setItemDataExtractor((c, id, i) -> (T) id);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelect(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected SingleSelect<T> buildSelect(SingleSelectField<T, T> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelectAsField(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected Field<T> buildSelectAsField(SingleSelectField<T, T> instance) {
			return instance;
		}

	}

	/**
	 * Default {@link SinglePropertySelectInputBuilder} implementation.
	 * @param <T> Selection type
	 */
	public static class PropertyBuilder<T>
			extends AbstractSingleSelectFieldBuilder<T, PropertyBox, SinglePropertySelectInputBuilder<T>>
			implements SinglePropertySelectInputBuilder<T> {

		/**
		 * Constructor
		 * @param selectProperty Selection (and identifier) property
		 * @param renderingMode Rendering mode
		 */
		@SuppressWarnings("unchecked")
		public PropertyBuilder(Property<T> selectProperty, RenderingMode renderingMode) {
			super(selectProperty.getType(), renderingMode);
			dataSourceBuilder.withProperty(selectProperty, selectProperty.getType());
			dataSourceBuilder.itemIdentifier(new PropertyItemIdentifier<>(selectProperty));
			itemAdapter(new PropertyBoxItemAdapter());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.PropertySelectInputBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider, java.lang.Iterable)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public <P extends Property<?>> SinglePropertySelectInputBuilder<T> dataSource(
				ItemDataProvider<PropertyBox> dataProvider, Iterable<P> properties) {
			ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
			ObjectUtils.argumentNotNull(properties, "Item property set must be not null");
			properties.forEach(p -> dataSourceBuilder.withProperty(p, p.getType()));
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
		public SinglePropertySelectInputBuilder<T> itemAdapter(final ItemAdapter<PropertyBox> itemAdapter) {
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
		protected void configureDataSource(SingleSelectField<T, PropertyBox> instance) {
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
		protected SingleSelect<T> buildSelect(SingleSelectField<T, PropertyBox> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelectAsField(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected Field<T> buildSelectAsField(SingleSelectField<T, PropertyBox> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected SinglePropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

}
