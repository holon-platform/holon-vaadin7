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
package com.holonplatform.vaadin.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

/**
 * A wrapper to wrap a {@link Field} instance into a {@link Input} component.
 * 
 * @param <V> Value type
 * 
 * @since 5.0.0
 */
public class InputFieldWrapper<V> implements Input<V> {

	private static final long serialVersionUID = -2456516308895591627L;

	/**
	 * Wrapped field
	 */
	private final Field<V> field;

	/**
	 * Constructor
	 * @param field Wrapped field (not null)
	 */
	public InputFieldWrapper(Field<V> field) {
		super();
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		this.field = field;
	}

	/**
	 * Get the wrapped {@link Field}.
	 * @return the wrapped field
	 */
	public Field<V> getField() {
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(V value) {
		field.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValue()
	 */
	@Override
	public V getValue() {
		return field.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return field.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#clear()
	 */
	@Override
	public void clear() {
		field.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		field.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return field.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return field.isRequired();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		field.setRequired(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#focus()
	 */
	@Override
	public void focus() {
		field.focus();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getComponent()
	 */
	@Override
	public Component getComponent() {
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#addValueChangeListener(com.holonplatform.vaadin.components.Input.
	 * ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			com.holonplatform.vaadin.components.Input.ValueChangeListener<V> listener) {
		return ValueChangeNotifierRegistration.adapt(this, field, listener);
	}

}
