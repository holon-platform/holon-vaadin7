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

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidatorSupport;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.internal.components.InputFieldWrapper;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

/**
 * Input component representation, i.e. a UI component that has a user-editable value.
 * <p>
 * Extends {@link ValueHolder} since handles a value, supporting {@link ValueChangeListener}s registration.
 * </p>
 * <p>
 * The actual UI {@link Component} which represents the input component can be obtained through {@link #getComponent()}.
 * </p>
 * <p>
 * The input component supports {@link Validator}s and current value validation through the {@link ValidatableValue}
 * interface.
 * </p>
 * 
 * @param <V> Value type
 * 
 * @since 5.0.0
 */
public interface Input<V> extends ValueHolder<V>, ValueComponent<V>, ValidatorSupport<V>, ValidatableValue {

	/**
	 * Sets the read-only mode of this input component. The user can't change the value when in read-only mode.
	 * @param readOnly the read-only mode of this input component
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * Returns whether this input component is in read-only mode or not.
	 * @return <code>false</code> if the user can modify the value, <code>true</code> if not
	 */
	boolean isReadOnly();

	/**
	 * Gets whether the field is <em>required</em>.
	 * @return <code>true</code> if the field as required, <code>false</code> otherwise
	 */
	public boolean isRequired();

	/**
	 * Sets the field as <em>required</em>.
	 * <p>
	 * Required fields should show a <em>required indicator</em> symbol in UI and the default non-empty validator is
	 * setted up.
	 * </p>
	 * @param required <code>true</code> to set the field as required, <code>false</code> otherwise
	 */
	void setRequired(boolean required);

	/**
	 * Sets the focus for this input component, if supported by concrete component implementation.
	 */
	void focus();

	/**
	 * Create a {@link Input} component type from given {@link Field} instance.
	 * @param field The field instance (not null)
	 * @return A new {@link Input} component which wraps the given <code>field</code>
	 */
	static <T> Input<T> from(Field<T> field) {
		return new InputFieldWrapper<>(field);
	}

	/**
	 * A convenience interface with a fixed {@link Input} rendering type to use a {@link Input} {@link PropertyRenderer}
	 * as a functional interface.
	 * @param <T> Property type
	 */
	@FunctionalInterface
	public interface InputPropertyRenderer<T> extends PropertyRenderer<Input<T>, T> {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		default Class<? extends Input<T>> getRenderType() {
			return (Class<? extends Input<T>>) (Class<?>) Input.class;
		}

	}

	/**
	 * A convenience interface to render a {@link Property} as a {@link Input} using a {@link Field}.
	 * @param <T> Property type
	 */
	public interface InputFieldPropertyRenderer<T> extends InputPropertyRenderer<T> {

		/**
		 * Render given <code>property</code> as consistent value type {@link Field} to handle the property value.
		 * @param property Property to render
		 * @return property {@link Field}
		 */
		Field<T> renderField(Property<T> property);

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
		 */
		@Override
		default Input<T> render(Property<T> property) {
			return Input.from(renderField(property));
		}

	}

}
