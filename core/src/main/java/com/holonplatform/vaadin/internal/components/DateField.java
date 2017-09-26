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
package com.holonplatform.vaadin.internal.components;

import java.util.Date;

import com.holonplatform.vaadin.Registration;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractDateFieldBuilder;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

/**
 * A {@link Date} type {@link Input} field.
 * 
 * @since 5.0.0
 */
public class DateField extends com.vaadin.ui.DateField implements Input<Date>, RequiredIndicatorSupport {

	private static final long serialVersionUID = -4077699329772463783L;

	/**
	 * Required indicator
	 */
	private boolean requiredIndicatorOnly = false;

	/**
	 * Constructs an empty <code>DateField</code> with no caption.
	 */
	public DateField() {
		super();
		init();
	}

	/**
	 * Constructs a new <code>DateField</code> that's bound to the specified <code>Property</code> and has no caption.
	 * @param dataSource the Property to be edited with this editor.
	 */
	@SuppressWarnings("rawtypes")
	public DateField(Property dataSource) throws IllegalArgumentException {
		super(dataSource);
		init();
	}

	/**
	 * Constructs a new <code>DateField</code> with the given caption and initial date value.
	 * @param caption Field caption.
	 * @param value the Date value.
	 */
	public DateField(String caption, Date value) {
		super(caption, value);
		init();
	}

	/**
	 * Constructs a new <code>DateField</code> that's bound to the specified <code>Property</code> and has the given
	 * caption.
	 * @param caption Field caption.
	 * @param dataSource the Property to be edited with this editor.
	 */
	@SuppressWarnings("rawtypes")
	public DateField(String caption, Property dataSource) {
		super(caption, dataSource);
		init();
	}

	/**
	 * Constructs an empty <code>DateField</code> with caption.
	 * @param caption Field caption.
	 */
	public DateField(String caption) {
		super(caption);
		init();
	}

	/**
	 * Init field
	 */
	protected void init() {
		addStyleName("h-field");
		addStyleName("h-datefield");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		super.setRequired(required);
		this.requiredIndicatorOnly = false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.RequiredIndicatorSupport#setRequiredIndicatorVisible(boolean)
	 */
	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		super.setRequired(requiredIndicatorVisible);
		this.requiredIndicatorOnly = requiredIndicatorVisible;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.RequiredIndicatorSupport#isRequiredIndicatorVisible()
	 */
	@Override
	public boolean isRequiredIndicatorVisible() {
		return isRequired() || requiredIndicatorOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#validateValue()
	 */
	@Override
	public void validate() throws InvalidValueException {
		if (requiredIndicatorOnly) {
			super.validate(getValue());
		} else {
			super.validate();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#addValueChangeListener(com.holonplatform.vaadin.components.Input.
	 * ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			com.holonplatform.vaadin.components.Input.ValueChangeListener<Date> listener) {
		return ValueChangeNotifierRegistration.adapt(this, this, listener);
	}

	// Builder

	/**
	 * Builder to create {@link DateField} instances
	 */
	public static class Builder extends AbstractDateFieldBuilder<DateField> {

		public Builder(Resolution resolution) {
			super(new DateField());
			if (resolution != null) {
				getInstance().setResolution(resolution);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected DateInputBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<Date> build(DateField instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<Date> buildAsField(DateField instance) {
			return instance;
		}

	}

}
