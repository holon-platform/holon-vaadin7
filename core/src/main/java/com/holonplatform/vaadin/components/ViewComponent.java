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

import com.holonplatform.vaadin.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultViewComponentBuilder;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;

/**
 * A {@link Component} to display a value in UI.
 * 
 * <p>
 * Extends {@link Viewer} to allow the use of a {@link Property} as data source and {@link ValueChangeNotifier} to
 * register listeners for value change events.
 * </p>
 * 
 * @param <T> Value type
 *
 * @since 5.0.0
 */
public interface ViewComponent<T> extends Component, Property<T>, Property.Viewer, Property.ValueChangeNotifier {

	/**
	 * Checks whether the view component is empty, i.e. has no value to display.
	 * @return <code>true</code> fi view component is empty
	 */
	boolean isEmpty();

	/**
	 * Clears component value
	 */
	void clear();

	// Builder

	/**
	 * Gets a builder to create {@link ViewComponent} instances.
	 * @param <T> Value type
	 * @param valueType Value type handled by the ViewComponent
	 * @return ViewComponent instance
	 */
	static <T> ViewComponentBuilder<T> builder(Class<? extends T> valueType) {
		return new DefaultViewComponentBuilder<>(valueType);
	}

}
