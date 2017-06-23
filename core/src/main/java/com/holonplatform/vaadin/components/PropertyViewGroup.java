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

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.internal.components.DefaultPropertyViewGroup;

/**
 * Provides functionalities to build and manage a group of {@link ViewComponent}s bound to a {@link Property} set.
 * 
 * @since 5.0.0
 */
public interface PropertyViewGroup extends Serializable {

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
	 * @return Property-ViewComponent {@link Binding} stream
	 */
	<T> Stream<Binding<T>> stream();

	/**
	 * Clear all the {@link ViewComponent}s.
	 */
	void clear();

	/**
	 * Get the all the property {@link ViewComponent}s values using a {@link PropertyBox}.
	 * <p>
	 * The {@link PropertyViewGroup} property set is used as the {@link PropertyBox} property set.
	 * </p>
	 * @return A {@link PropertyBox} containing the values of the properties of this {@link PropertyViewGroup}
	 */
	PropertyBox getValue();

	/**
	 * Load the values contained in given {@link PropertyBox} to the bound {@link ViewComponent}s of this
	 * {@link PropertyViewGroup}.
	 * @param propertyBox PropertyBox from which to read the values. If <code>null</code>, all ViewComponents are
	 *        cleared.
	 */
	void setValue(PropertyBox propertyBox);

	// ------- Builders

	/**
	 * Get a {@link Builder} to create and setup a {@link PropertyViewGroup}.
	 * @return {@link PropertyViewGroupBuilder} builder
	 */
	static PropertyViewGroupBuilder builder() {
		return new DefaultPropertyViewGroup.DefaultBuilder();
	}

	/**
	 * {@link PropertyViewGroup} builder.
	 */
	public interface PropertyViewGroupBuilder extends Builder<PropertyViewGroup, PropertyViewGroupBuilder> {

	}

	/**
	 * Base {@link PropertyViewGroup} builder.
	 * @param <G> Actual {@link PropertyViewGroup} type
	 * @param <B> Concrete builder type
	 */
	public interface Builder<G extends PropertyViewGroup, B extends Builder<G, B>> {

		/**
		 * Add given properties to the {@link PropertyViewGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add
		 * @return this
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		<P extends Property> B properties(P... properties);

		/**
		 * Add given properties to the {@link PropertyViewGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		<P extends Property> B properties(Iterable<P> properties);

		/**
		 * Set a specific {@link PropertyRenderer} to use to render the {@link ViewComponent} to bind to given
		 * <code>property</code>.
		 * @param <T> Property type
		 * @param <F> Rendered ViewComponent type
		 * @param property Property (not null)
		 * @param renderer Property renderer (not null)
		 * @return this
		 */
		<T, F extends T> B bind(Property<T> property, PropertyRenderer<ViewComponent<F>, T> renderer);

		/**
		 * Convenience method to set a specific {@link PropertyRenderer} to use to render the {@link ViewComponent} to
		 * bind to given <code>property</code> using the {@link ViewComponentPropertyRenderer} functional interface.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param renderer Property renderer (not null)
		 * @return this
		 */
		<T> B bind(Property<T> property, ViewComponentPropertyRenderer<T> renderer);

		/**
		 * Bind the given <code>property</code> to given <code>viewComponent</code> instance. If the property was
		 * already bound to a ViewComponent, the old ViewComponent will be replaced by the new ViewComponent.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param viewComponent ViewComponent to bind (not null)
		 * @return this
		 */
		default <T> B bind(Property<T> property, ViewComponent<? extends T> viewComponent) {
			ObjectUtils.argumentNotNull(viewComponent, "ViewComponent must be not null");
			return bind(property, p -> viewComponent);
		}

		/**
		 * Add a {@link ViewComponentConfigurator} to allow further {@link ViewComponent} configuration after generation
		 * and before the ViewComponent is actually bound to a property.
		 * @param viewComponentConfigurator {@link ViewComponentConfigurator} to add (not null)
		 * @return this
		 */
		B withViewComponentConfigurator(ViewComponentConfigurator viewComponentConfigurator);

		/**
		 * Set whether to ignore properties without a bound {@link ViewComponent}. Default is <code>false</code>, and an
		 * exception is thrown if a property of the {@link PropertyViewGroup} cannot be bound to any ViewComponent.
		 * @param ignoreMissingViewComponents Whether to ignore when the ViewComponent for a property is missing
		 * @return this
		 */
		B ignoreMissingViewComponents(boolean ignoreMissingViewComponents);

		/**
		 * Build the {@link PropertyViewGroup}
		 * @return PropertyViewGroup instance
		 */
		G build();

	}

	// -------

	/**
	 * Interface to allow further {@link ViewComponent} configuration after generation and before the ViewComponent is
	 * actually bound to a property.
	 */
	@FunctionalInterface
	public interface ViewComponentConfigurator {

		/**
		 * Configure the given {@link ViewComponent} before binding it to its property.
		 * @param property Property to which the ViewComponent is bound
		 * @param viewComponent ViewComponent to configure
		 */
		void configureViewComponent(Property<?> property, ViewComponent<?> viewComponent);

	}

	/**
	 * A {@link PropertyRenderer} with a fixed {@link ViewComponent} rendering type.
	 * @param <T> Property type
	 */
	@SuppressWarnings("rawtypes")
	@FunctionalInterface
	public interface ViewComponentPropertyRenderer<T> extends PropertyRenderer<ViewComponent, T> {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
		 */
		@Override
		default Class<? extends ViewComponent> getRenderType() {
			return ViewComponent.class;
		}

	}

	/**
	 * Represents a binding between a {@link Property} and its {@link ViewComponent} in a {@link PropertyViewGroup}.
	 * @param <T> Property type
	 */
	public interface Binding<T> {

		/**
		 * Get the property
		 * @return The property
		 */
		Property<T> getProperty();

		/**
		 * Get the {@link ViewComponent} to which the property is bound
		 * @return The {@link ViewComponent}
		 */
		ViewComponent<T> getViewComponent();

	}

}
