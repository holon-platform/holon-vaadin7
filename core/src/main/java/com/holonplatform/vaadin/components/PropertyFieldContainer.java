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

import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.PropertyFieldGroup.Binding;
import com.vaadin.ui.Field;

/**
 * Container of {@link Field}s bound to a {@link Property} set.
 *
 * @since 5.0.0
 */
public interface PropertyFieldContainer {

	/**
	 * Gets all the available properties
	 * @return An {@link Iterable} on this {@link PropertyFieldContainer} property set
	 */
	@SuppressWarnings("rawtypes")
	Iterable<Property> getProperties();

	/**
	 * Gets all the Fields that have been bound to a property.
	 * @return An {@link Iterable} on all bound Fields
	 */
	Iterable<Field<?>> getFields();

	/**
	 * Get the {@link Field} bound to given <code>property</code>, if any.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @return Optional {@link Field} bound to given <code>property</code>
	 */
	<T> Optional<Field<T>> getField(Property<T> property);

	/**
	 * Return a {@link Stream} of the properties and their bound fields of this {@link PropertyFieldContainer}.
	 * @param <T> Property type
	 * @return Property-Field {@link Binding} stream
	 */
	<T> Stream<Binding<T>> stream();

}
