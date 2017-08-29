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
package com.holonplatform.vaadin.components;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.TablePropertyListingBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultGridPropertyListingBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultTablePropertyListingBuilder;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Table;

/**
 * An {@link ItemListing} component using {@link Property}s as item properties and {@link PropertyBox} as item data
 * type.
 * 
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public interface PropertyListing extends ItemListing<PropertyBox, Property> {

	// Builders

	/**
	 * Builder to create an {@link PropertyListing} instance.
	 * <p>
	 * By default, a {@link #gridBuilder()} is returned.
	 * </p>
	 * @param <P> Actual property type
	 * @param properties The property set to use for the listing
	 * @return {@link PropertyListing} builder
	 */
	static <P extends Property<?>> GridPropertyListingBuilder builder(Iterable<P> properties) {
		return gridBuilder(properties);
	}

	/**
	 * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
	 * @param <P> Actual property type
	 * @param properties The property set to use for the listing
	 * @return Grid {@link PropertyListing} builder
	 */
	static <P extends Property<?>> GridPropertyListingBuilder gridBuilder(Iterable<P> properties) {
		return new DefaultGridPropertyListingBuilder(properties);
	}

	/**
	 * Builder to create an {@link PropertyListing} instance using a {@link Table} as backing component.
	 * @param <P> Actual property type
	 * @param properties The property set to use for the listing
	 * @return Table {@link PropertyListing} builder
	 */
	static <P extends Property<?>> TablePropertyListingBuilder tableBuilder(Iterable<P> properties) {
		return new DefaultTablePropertyListingBuilder(properties);
	}

}
