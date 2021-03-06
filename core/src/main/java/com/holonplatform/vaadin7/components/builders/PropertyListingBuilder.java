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
package com.holonplatform.vaadin7.components.builders;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin7.components.Input;
import com.holonplatform.vaadin7.components.PropertyListing;
import com.holonplatform.vaadin7.data.ItemDataProvider;
import com.holonplatform.vaadin7.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin7.internal.components.ValidatorWrapper;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Table;
import com.vaadin.ui.renderers.Renderer;

/**
 * An {@link ItemListingBuilder} using {@link Property} as item properties and {@link PropertyBox} as item data type.
 *
 * @param <B> Concrete builder type
 * @param <X> Concrete backing component type
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public interface PropertyListingBuilder<B extends PropertyListingBuilder<B, X>, X extends Component>
		extends ItemListingBuilder<PropertyBox, Property, PropertyListing, B, X> {

	/**
	 * Adds a {@link Validator} to the field bound to given <code>property</code> in the item listing editor.
	 * @param <V> Property type
	 * @param property Property (not null)
	 * @param validator Validator to add (not null)
	 * @return this
	 */
	default <V> B withValidator(Property<V> property, Validator<V> validator) {
		return withValidator(property, new ValidatorWrapper<>(validator));
	}

	/**
	 * Adds a {@link com.vaadin.data.Validator} to the field bound to given <code>property</code> in the item listing
	 * editor.
	 * @param <V> Property type
	 * @param property Property (not null)
	 * @param validator Validator to add (not null)
	 * @return this
	 */
	<V> B withValidator(Property<V> property, com.vaadin.data.Validator validator);

	/**
	 * Set the given property as required in the item listing editor. If a property is required, the field bound to the
	 * property will be setted as required, and its validation will fail when empty.
	 * @param property Property to set as required (not null)
	 * @return this
	 */
	default B required(Property<?> property) {
		return required(property, (Localizable) null);
	}

	/**
	 * Set the given property as required in the item listing editor. If a property is required, the field bound to the
	 * property will be setted as required, and its validation will fail when empty.
	 * @param property Property to set as required (not null)
	 * @param message The message to use to notify the required validation failure
	 * @return this
	 */
	B required(Property<?> property, Localizable message);

	/**
	 * Set the given property as required in the item listing editor. If a property is required, the field bound to the
	 * property will be setted as required, and its validation will fail when empty.
	 * @param property Property to set as required (not null)
	 * @param message The default message to use to notify the required validation failure
	 * @param messageCode The message localization code
	 * @param arguments Optional message translation arguments
	 * @return this
	 */
	default B required(Property<?> property, String message, String messageCode, Object... arguments) {
		return required(property,
				Localizable.builder().message(message).messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param property Property to set as required (not null)
	 * @param message The default message to use to notify the required validation failure
	 * @return this
	 */
	default B required(Property<?> property, String message) {
		return required(property, Localizable.builder().message(message).build());
	}

	/**
	 * Set the items data provider.
	 * @param dataProvider Items data provider (not null)
	 * @param identifierProperties Properties wich act as item identifier
	 * @return this
	 */
	B dataSource(ItemDataProvider<PropertyBox> dataProvider, Property... identifierProperties);

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * <p>
	 * A {@link Datastore} based {@link CommitHandler} is also configured by default.
	 * </p>
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param identifierProperties Properties wich act as item identifier
	 * @return this
	 */
	B dataSource(Datastore datastore, DataTarget<?> dataTarget, Property... identifierProperties);

	/**
	 * Builder to create {@link PropertyListing} components using a {@link Grid} as backing component.
	 */
	public interface GridPropertyListingBuilder extends PropertyListingBuilder<GridPropertyListingBuilder, Grid>,
			BaseGridItemListingBuilder<PropertyBox, Property, PropertyListing, GridPropertyListingBuilder> {

		/**
		 * Set a custom {@link Renderer} for given item property.
		 * @param <T> Property type
		 * @param property Item property to set the renderer for (not null)
		 * @param renderer Renderer to use
		 * @return this
		 */
		<T> GridPropertyListingBuilder render(Property<T> property, Renderer<? super T> renderer);

		/**
		 * Set a custom {@link Renderer} and {@link Converter} for given item property.
		 * @param <T> Property type
		 * @param <P> Presentation value type
		 * @param property Item property to set the renderer for
		 * @param converter Value converter
		 * @param renderer Renderer to use
		 * @return this
		 */
		<T, P> GridPropertyListingBuilder render(Property<T> property, Converter<P, T> converter,
				Renderer<? super P> renderer);

	}

	/**
	 * Builder to create {@link PropertyListing} components using a {@link Table} as backing component.
	 */
	public interface TablePropertyListingBuilder extends PropertyListingBuilder<TablePropertyListingBuilder, Table>,
			BaseTableItemListingBuilder<PropertyBox, Property, PropertyListing, TablePropertyListingBuilder> {

	}

}
