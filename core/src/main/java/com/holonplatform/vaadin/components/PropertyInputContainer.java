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

/**
 * Container of {@link Input}s bound to a {@link Property} set, with methods to obtain the managed properties and the
 * associated inputs.
 *
 * @since 5.0.0
 */
public interface PropertyInputContainer {

	/**
	 * Gets all the available properties
	 * @return An {@link Iterable} on this {@link PropertyInputContainer} property set
	 */
	@SuppressWarnings("rawtypes")
	Iterable<Property> getProperties();

	/**
	 * Gets all the {@link Input}s that have been bound to a property.
	 * @return An {@link Iterable} on all bound {@link Input}s
	 */
	Iterable<Input<?>> getInputs();

	/**
	 * Get the {@link Input} bound to given <code>property</code>, if any.
	 * @param <T> Property type
	 * @param property Property for which to get the associated {@link Input} (not null)
	 * @return Optional {@link Input} bound to given <code>property</code>
	 */
	<T> Optional<Input<T>> getInput(Property<T> property);

	/**
	 * Return a {@link Stream} of the properties and their bound {@link Input}s of this {@link PropertyInputContainer}.
	 * @param <T> Property type
	 * @return Property-Input {@link Binding} stream
	 */
	<T> Stream<Binding<T>> stream();

	/**
	 * Represents a binding between a {@link Property} and its {@link Input}.
	 * @param <T> Property type
	 */
	public interface Binding<T> {

		/**
		 * Get the {@link Property}.
		 * @return The property
		 */
		Property<T> getProperty();

		/**
		 * Get the {@link Input} to which the property is bound.
		 * @return The input
		 */
		Input<T> getInput();

	}

}
