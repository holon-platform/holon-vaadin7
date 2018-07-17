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
package com.holonplatform.vaadin7.internal.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin7.Registration;
import com.holonplatform.vaadin7.components.ViewComponent;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterUtil;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

/**
 * Base {@link ViewComponent} implementation, using a {@link Label} as backing UI component.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public abstract class AbstractViewComponent<T> extends CustomComponent
		implements ViewComponent<T>, Property<T>, Property.Editor, Property.ValueChangeListener {

	private static final long serialVersionUID = 1112624954933562344L;

	/**
	 * Concrete value type
	 */
	private final Class<? extends T> type;

	/**
	 * Value change listeners
	 */
	private final List<com.holonplatform.vaadin7.components.Input.ValueChangeListener<T>> valueChangeListeners = new LinkedList<>();

	/**
	 * Data source property
	 */
	private Property<T> dataSource;

	/**
	 * Optional converter to use when component is bound to a data source property
	 */
	private Converter<T, Object> converter = null;

	/**
	 * Component value
	 */
	private T value;

	/**
	 * Constructor
	 * @param type Concrete value type
	 * @param root Root component
	 */
	public AbstractViewComponent(Class<? extends T> type, Component root) {
		super(root);
		this.type = TypeUtils.box(type);

		setSizeUndefined();

		addStyleName("h-viewcomponent");
	}

	/**
	 * Called when a new value is setted in view component using {@link #setValue(Object)}. Subclasses should update the
	 * state of the backing UI component to display the value.
	 * @param value The new value
	 */
	protected abstract void updateValue(T value);

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setHeight(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public void setHeight(float height, Unit unit) {
		super.setHeight(height, unit);
		updateRootSize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setWidth(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public void setWidth(float width, Unit unit) {
		super.setWidth(width, unit);
		updateRootSize();
	}

	/**
	 * Update root component size when component size changes
	 */
	protected void updateRootSize() {
		if (getCompositionRoot() != null) {
			if (getWidth() > -1) {
				getCompositionRoot().setWidth(100, Unit.PERCENTAGE);
			} else {
				getCompositionRoot().setWidthUndefined();
			}
			if (getHeight() > -1) {
				getCompositionRoot().setHeight(100, Unit.PERCENTAGE);
			} else {
				getCompositionRoot().setHeightUndefined();
			}
		}
	}

	/**
	 * Get the concrete content {@link Component}.
	 * @return Content {@link Component}
	 */
	protected Optional<Component> getContent() {
		return Optional.ofNullable(getCompositionRoot());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setStyleName(java.lang.String)
	 */
	@Override
	public void setStyleName(String style) {
		super.setStyleName(style);
		// replicate to content
		getContent().ifPresent(c -> c.setStyleName(style));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#addStyleName(java.lang.String)
	 */
	@Override
	public void addStyleName(String style) {
		super.addStyleName(style);
		// replicate to content
		getContent().ifPresent(c -> c.addStyleName(style));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#removeStyleName(java.lang.String)
	 */
	@Override
	public void removeStyleName(String style) {
		super.removeStyleName(style);
		// replicate to content
		getContent().ifPresent(c -> c.removeStyleName(style));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property#getValue()
	 */
	@Override
	public T getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T newValue) throws com.vaadin.data.Property.ReadOnlyException {
		this.value = newValue;
		// update internal component
		updateValue(newValue);
		// fire value change
		fireValueChange(newValue);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property#getType()
	 */
	@Override
	public Class<? extends T> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ViewComponent#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getValue() == null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ViewComponent#clear()
	 */
	@Override
	public void clear() {
		setValue(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			com.holonplatform.vaadin7.components.Input.ValueChangeListener<T> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		valueChangeListeners.add(listener);
		return () -> removeValueChangeListener(listener);
	}

	/**
	 * Removes a {@link com.holonplatform.vaadin7.components.Input.ValueChangeListener}.
	 * @param listener the listener to remove
	 */
	public void removeValueChangeListener(com.holonplatform.vaadin7.components.Input.ValueChangeListener<T> listener) {
		if (listener != null) {
			valueChangeListeners.remove(listener);
		}
	}

	/**
	 * Emits the value change event
	 * @param value the changed value
	 */
	protected void fireValueChange(T value) {
		final com.holonplatform.vaadin7.components.Input.ValueChangeEvent<T> valueChangeEvent = new DefaultValueChangeEvent<>(
				this, value);
		valueChangeListeners.forEach(l -> l.valueChange(valueChangeEvent));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		updateValueFromDataSource();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property.Viewer#setPropertyDataSource(com.vaadin.data.Property)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyDataSource(@SuppressWarnings("rawtypes") Property newDataSource) {
		// Stops listening the old data source changes
		if (dataSource != null && Property.ValueChangeNotifier.class.isAssignableFrom(dataSource.getClass())) {
			((Property.ValueChangeNotifier) dataSource).removeValueChangeListener(this);
		}

		// Check if the current converter is compatible.
		if (newDataSource != null
				&& !ConverterUtil.canConverterPossiblyHandle(getConverter(), getType(), newDataSource.getType())) {
			// There is no converter set or there is no way the current converter can be compatible
			Converter<T, Object> c = ConverterUtil.getConverter(getType(), newDataSource.getType(), getSession());
			setConverter(c);
		}

		dataSource = newDataSource;
		if (dataSource != null) {
			// Update value from the data source
			updateValueFromDataSource();
		}

		// Listens the new data source
		if (dataSource != null && Property.ValueChangeNotifier.class.isAssignableFrom(dataSource.getClass())) {
			((Property.ValueChangeNotifier) dataSource).addValueChangeListener(this);
		}
		markAsDirty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property.Viewer#getPropertyDataSource()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Property getPropertyDataSource() {
		return dataSource;
	}

	/**
	 * Gets the converter used to convert the property data source value to the component value.
	 * @return The converter or null if none is set.
	 */
	public Converter<T, Object> getConverter() {
		return converter;
	}

	/**
	 * Sets the converter used to convert the component value to the property data source type.
	 * @param converter The converter to use.
	 */
	public void setConverter(Converter<T, Object> converter) {
		this.converter = converter;
		markAsDirty();
	}

	/**
	 * Returns the current value of the data source
	 * @return Data source value
	 */
	private T getDataSourceValue() {
		return ConverterUtil.convertFromModel(getPropertyDataSource().getValue(), getType(), getConverter(),
				getLocale());
	}

	/**
	 * Update component value using data source value
	 */
	private void updateValueFromDataSource() {
		// Update the internal value from the data source
		T newConvertedValue = getDataSourceValue();
		if (!SharedUtil.equals(newConvertedValue, value)) {
			value = newConvertedValue;
			// update internal component
			updateValue(value);
			// fire value change
			fireValueChange(value);
		}
	}

}
