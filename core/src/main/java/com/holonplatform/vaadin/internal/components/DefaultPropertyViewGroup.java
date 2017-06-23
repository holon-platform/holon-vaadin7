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
package com.holonplatform.vaadin.internal.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.vaadin.components.PropertyViewGroup;
import com.holonplatform.vaadin.components.ViewComponent;

/**
 * Default {@link PropertyViewGroup} implementation.
 *
 * @since 5.0.0
 */
public class DefaultPropertyViewGroup implements PropertyViewGroup {

	private static final long serialVersionUID = -2110591918893531742L;

	/**
	 * Property set
	 */
	@SuppressWarnings("rawtypes")
	private final List<Property> properties = new LinkedList<>();

	/**
	 * Bound ViewComponents
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, ViewComponent> propertyViews = new HashMap<>();

	/**
	 * Custom ViewComponentPropertyRenderers
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, ViewComponentPropertyRenderer> propertyRenderers = new HashMap<>(8);

	/**
	 * ViewComponent configurators
	 */
	private final List<ViewComponentConfigurator> viewComponentConfigurators = new LinkedList<>();

	/**
	 * Ignore missing ViewComponents
	 */
	private boolean ignoreMissingViewComponent = false;

	/**
	 * Constructor
	 */
	public DefaultPropertyViewGroup() {
		super();
	}

	/**
	 * Add a property to the property set
	 * @param property Property to add
	 */
	@SuppressWarnings("rawtypes")
	protected void addProperty(Property property) {
		if (property != null) {
			properties.add(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getViewComponents()
	 */
	@Override
	public Iterable<ViewComponent<?>> getViewComponents() {
		final List<ViewComponent<?>> vs = new ArrayList<>(propertyViews.size());
		properties.forEach(p -> vs.add(propertyViews.get(p)));
		return vs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getViewComponent(com.holonplatform.core.property.
	 * Property)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<ViewComponent<T>> getViewComponent(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return Optional.ofNullable(propertyViews.get(property));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#stream()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Stream<Binding<T>> stream() {
		return propertyViews.entrySet().stream().map(e -> new PropertyViewBinding<>(e.getKey(), e.getValue()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#clear()
	 */
	@Override
	public void clear() {
		properties.forEach(p -> propertyViews.get(p).clear());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getValue()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertyBox getValue() {
		final PropertyBox value = PropertyBox.builder(properties).invalidAllowed(true).build();
		properties.forEach(p -> {
			value.setValue(p, propertyViews.get(p).getValue());
		});
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyViewGroup#setValue(com.holonplatform.core.property.PropertyBox)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setValue(PropertyBox propertyBox) {
		if (propertyBox == null) {
			clear();
		} else {
			properties.forEach(p -> {
				final ViewComponent vc = propertyViews.get(p);
				if (vc != null) {
					Object value = getPropertyValue(propertyBox, p);
					if (value != null) {
						// ignore read-only
						boolean ro = vc.isReadOnly();
						if (ro)
							vc.setReadOnly(false);
						vc.setValue(value);
						if (ro)
							vc.setReadOnly(true);
					} else {
						vc.clear();
					}
				}
			});
		}
	}

	/**
	 * Get the value of given <code>property</code> using given <code>propertyBox</code>.
	 * @param <T> Property type
	 * @param propertyBox PropertyBox
	 * @param property Property
	 * @return Property value
	 */
	protected <T> T getPropertyValue(PropertyBox propertyBox, Property<T> property) {
		if (VirtualProperty.class.isAssignableFrom(property.getClass())) {
			if (((VirtualProperty<T>) property).getValueProvider() != null) {
				return ((VirtualProperty<T>) property).getValueProvider().getPropertyValue(propertyBox);
			}
			return null;
		}
		if (propertyBox.containsValue(property)) {
			return propertyBox.getValue(property);
		}
		return null;
	}

	/**
	 * Whether to ignore missing property ViewComponents
	 * @return <code>true</code> if missing property ViewComponents must be ignored
	 */
	protected boolean isIgnoreMissingViewComponent() {
		return ignoreMissingViewComponent;
	}

	/**
	 * Set whether to ignore missing property ViewComponents
	 * @param ignoreMissingViewComponent <code>true</code> to ignore missing property ViewComponents
	 */
	protected void setIgnoreMissingViewComponent(boolean ignoreMissingViewComponent) {
		this.ignoreMissingViewComponent = ignoreMissingViewComponent;
	}

	/**
	 * Register a {@link ViewComponentConfigurator}.
	 * @param viewComponentConfigurator the ViewComponentConfigurator to register
	 */
	protected void addViewComponentConfigurator(ViewComponentConfigurator viewComponentConfigurator) {
		if (viewComponentConfigurator != null) {
			viewComponentConfigurators.add(viewComponentConfigurator);
		}
	}

	/**
	 * Set the {@link ViewComponentPropertyRenderer} to use with given property
	 * @param <T> Property type
	 * @param property Property
	 * @param renderer Renderer
	 */
	protected <T> void setPropertyRenderer(Property<T> property, ViewComponentPropertyRenderer<T> renderer) {
		if (property != null && renderer != null) {
			propertyRenderers.put(property, renderer);
		}
	}

	/**
	 * Render given property as a {@link ViewComponent}, using custom {@link ViewComponentPropertyRenderer} or default
	 * {@link Property#render(Class)} method
	 * @param <T> Property type
	 * @param property Property to render
	 * @return Rendered ViewComponent
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> Optional<ViewComponent> render(Property<T> property) {
		// check custom renderer
		if (propertyRenderers.containsKey(property)) {
			final ViewComponentPropertyRenderer<T> r = propertyRenderers.get(property);
			return Optional.ofNullable(r.render(property));
		}
		// use registry
		return property.renderIfAvailable(ViewComponent.class);
	}

	/**
	 * Configure ViewComponent before binding
	 * @param property Property
	 * @param viewComponent ViewComponent to configure
	 */
	protected void configureViewComponent(final Property<?> property, final ViewComponent<?> viewComponent) {
		// Configurators
		viewComponentConfigurators.forEach(fc -> fc.configureViewComponent(property, viewComponent));
	}

	/**
	 * Build and bind {@link ViewComponent}s to the properties of the property set.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void build() {
		propertyViews.clear();
		// render and bind ViewComponents
		properties.forEach(p -> {
			final Optional<ViewComponent> viewComponent = render(p);
			if (!viewComponent.isPresent() && !isIgnoreMissingViewComponent()) {
				throw new NoSuitableRendererAvailableException(
						"No ViewComponent render available to render the property [" + p.toString()
								+ "] as a ViewComponent");
			}
			viewComponent.ifPresent(v -> {
				// configure
				configureViewComponent(p, v);
				// bind
				propertyViews.put(p, v);
			});
		});
	}

	// Builder

	public static class DefaultBuilder implements PropertyViewGroupBuilder {

		private final DefaultPropertyViewGroup instance = new DefaultPropertyViewGroup();

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyViewGroup.Builder#properties(com.holonplatform.core.property.
		 * Property[])
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <P extends Property> PropertyViewGroupBuilder properties(P... properties) {
			if (properties != null) {
				for (P property : properties) {
					instance.addProperty(property);
				}
			}
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#properties(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> PropertyViewGroupBuilder properties(Iterable<P> properties) {
			ObjectUtils.argumentNotNull(properties, "Properties must be not null");
			for (P property : properties) {
				instance.addProperty(property);
			}
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T, F extends T> PropertyViewGroupBuilder bind(Property<T> property,
				PropertyRenderer<ViewComponent<F>, T> renderer) {
			ObjectUtils.argumentNotNull(renderer, "PropertyRenderer must be not null");
			return bind(property, p -> renderer.render(p));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.vaadin.components.PropertyViewGroup.ViewComponentPropertyRenderer)
		 */
		@Override
		public <T> PropertyViewGroupBuilder bind(Property<T> property, ViewComponentPropertyRenderer<T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "PropertyRenderer must be not null");
			instance.setPropertyRenderer(property, renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#withViewComponentConfigurator(com.
		 * holonframework.vaadin.components.PropertyViewGroup.ViewComponentConfigurator)
		 */
		@Override
		public PropertyViewGroupBuilder withViewComponentConfigurator(
				ViewComponentConfigurator viewComponentConfigurator) {
			ObjectUtils.argumentNotNull(viewComponentConfigurator, "ViewComponentConfigurator must be not null");
			instance.addViewComponentConfigurator(viewComponentConfigurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#ignoreMissingViewComponents(boolean)
		 */
		@Override
		public PropertyViewGroupBuilder ignoreMissingViewComponents(boolean ignoreMissingViewComponents) {
			instance.setIgnoreMissingViewComponent(ignoreMissingViewComponents);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#build()
		 */
		@Override
		public PropertyViewGroup build() {
			instance.build();
			return instance;
		}

	}

	/**
	 * Default {@link Binding} implementation.
	 * @param <T> Property type
	 */
	private static class PropertyViewBinding<T> implements Binding<T> {

		private final Property<T> property;
		private final ViewComponent<T> viewComponent;

		public PropertyViewBinding(Property<T> property, ViewComponent<T> viewComponent) {
			super();
			this.property = property;
			this.viewComponent = viewComponent;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Binding#getProperty()
		 */
		@Override
		public Property<T> getProperty() {
			return property;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Binding#getViewComponent()
		 */
		@Override
		public ViewComponent<T> getViewComponent() {
			return viewComponent;
		}

	}

}
