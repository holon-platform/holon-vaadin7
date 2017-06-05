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
package com.holonplatform.vaadin.components.builders;

import java.util.Arrays;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.container.ItemAdapter;
import com.holonplatform.vaadin.internal.data.DatastoreItemDataProvider;
import com.vaadin.ui.Field;

/**
 * Builder to create selection {@link Field}s with {@link Property} data source support.
 * 
 * @param <T> Field type
 * @param <C> Component type
 * @param <S> Selection type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface PropertySelectFieldBuilder<T, C extends Field<T>, S, B extends PropertySelectFieldBuilder<T, C, S, B>>
		extends BaseSelectFieldBuilder<T, C, S, PropertyBox, B> {
	
	/**
	 * Add properties to include in {@link PropertyBox} selection items.
	 * @param <P> Property type
	 * @param properties Properties to add (not null)
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	<P extends Property> B withProperties(Iterable<P> properties);

	/**
	 * Add properties to include in {@link PropertyBox} selection items.
	 * @param properties Properties to add (not null)
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	default B withProperties(Property... properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return withProperties(Arrays.asList(properties));
	}

	/**
	 * Set the selection items data provider to obtain item ids.
	 * <p>
	 * Item ids type must be compatible with the selection item type.
	 * </p>
	 * @param dataProvider Items data provider (not null)
	 * @return this
	 */
	B dataSource(ItemDataProvider<PropertyBox> dataProvider);

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @return this
	 */
	default B dataSource(Datastore datastore, DataTarget<?> dataTarget) {
		return dataSource(new DatastoreItemDataProvider(datastore, dataTarget));
	}

	/**
	 * Set the item adapter to use to convert data source items into container items and back.
	 * @param itemAdapter the item adapter (not null)
	 * @return this
	 */
	B itemAdapter(ItemAdapter<PropertyBox> itemAdapter);

}
