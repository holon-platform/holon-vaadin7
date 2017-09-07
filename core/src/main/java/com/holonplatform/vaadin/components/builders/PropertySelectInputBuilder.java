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

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.container.ItemAdapter;

/**
 * Builder to create selection {@link Input}s with {@link Property} data source support.
 * 
 * @param <T> Value type
 * @param <C> Component type
 * @param <S> Selection type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface PropertySelectInputBuilder<T, C extends Input<T>, S, B extends PropertySelectInputBuilder<T, C, S, B>>
		extends BaseSelectInputBuilder<T, C, S, PropertyBox, B> {

	/**
	 * Set the selection items data provider to obtain items.
	 * @param <P> Property type
	 * @param dataProvider Items data provider (not null)
	 * @param properties Item property set (not null)
	 * @return this
	 */
	<P extends Property<?>> B dataSource(ItemDataProvider<PropertyBox> dataProvider, Iterable<P> properties);

	/**
	 * Set the selection items data provider to obtain items.
	 * @param <P> Property type
	 * @param dataProvider Items data provider (not null)
	 * @param properties Item property set (not null)
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default <P extends Property<?>> B dataSource(ItemDataProvider<PropertyBox> dataProvider, P... properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return dataSource(dataProvider, PropertySet.of(properties));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param <P> Property type
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param properties Item property set (not null)
	 * @return this
	 */
	default <P extends Property<?>> B dataSource(Datastore datastore, DataTarget<?> dataTarget,
			Iterable<P> properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return dataSource(
				ItemDataProvider.create(datastore, dataTarget,
						(properties instanceof PropertySet) ? (PropertySet<?>) properties : PropertySet.of(properties)),
				properties);
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param <P> Property type
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param properties Item property set (not null)
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default <P extends Property<?>> B dataSource(Datastore datastore, DataTarget<?> dataTarget, P... properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		final PropertySet<?> set = PropertySet.of(properties);
		return dataSource(ItemDataProvider.create(datastore, dataTarget, set), set);
	}

	/**
	 * Set the item adapter to use to convert data source items into container items and back.
	 * @param itemAdapter the item adapter (not null)
	 * @return this
	 */
	B itemAdapter(ItemAdapter<PropertyBox> itemAdapter);

}
