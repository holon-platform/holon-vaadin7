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
package com.holonplatform.vaadin.components;

import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.property.Property;

/**
 * Represents a source of {@link Property} - {@link ViewComponent} bindings, i.e. a container of {@link ViewComponent}
 * components bound to a {@link Property} set, providing methods to obtain the managed properties and the associated
 * view components.
 *
 * @since 5.0.0
 */
public interface PropertyViewSource {

	/**
	 * Gets all the available properties
	 * @return An {@link Iterable} on this {@link PropertyViewGroup} property set
	 */
	@SuppressWarnings("rawtypes")
	Iterable<Property> getProperties();

	/**
	 * Gets all the {@link ViewComponent}s that have been bound to a property.
	 * @return An {@link Iterable} on all bound ViewComponents
	 */
	Iterable<ViewComponent<?>> getViewComponents();

	/**
	 * Get the {@link ViewComponent} bound to given <code>property</code>, if any.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @return Optional {@link ViewComponent} bound to given <code>property</code>
	 */
	<T> Optional<ViewComponent<T>> getViewComponent(Property<T> property);

	/**
	 * Return a {@link Stream} of the properties and their bound {@link ViewComponent}s of this
	 * {@link PropertyViewGroup}.
	 * @param <T> Property type
	 * @return Property-ViewComponent {@link PropertyBinding} stream
	 */
	<T> Stream<PropertyBinding<T, ViewComponent<T>>> stream();

}
