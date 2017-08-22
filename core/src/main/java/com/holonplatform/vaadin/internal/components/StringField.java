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

import com.holonplatform.vaadin.Registration;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractStringFieldBuilder;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

/**
 * A {@link String} type {@link Input} field.
 * 
 * @since 5.0.0
 */
public class StringField extends TextField implements Input<String>, RequiredIndicatorSupport {

	private static final long serialVersionUID = -4023624843325799165L;

	/**
	 * Required indicator
	 */
	private boolean requiredIndicatorOnly = false;

	/**
	 * Treat empty values as <code>null</code> values
	 */
	private boolean emptyValuesAsNull = true;

	/**
	 * Treat blank values as <code>null</code> values
	 */
	private boolean blankValuesAsNull = false;

	/**
	 * Constructs an empty <code>StringField</code> with no caption.
	 */
	public StringField() {
		super();
		init();
	}

	/**
	 * Constructs a new <code>StringField</code> that's bound to the specified <code>Property</code> and has no caption.
	 * @param dataSource the Property to be edited with this editor.
	 */
	@SuppressWarnings("rawtypes")
	public StringField(Property dataSource) {
		super(dataSource);
		init();
	}

	/**
	 * Constructs a new <code>StringField</code> that's bound to the specified <code>Property</code> and has the given
	 * caption <code>String</code>.
	 * @param caption the caption for the editor.
	 * @param dataSource the Property to be edited with this editor.
	 */
	@SuppressWarnings("rawtypes")
	public StringField(String caption, Property dataSource) {
		super(caption, dataSource);
		init();
	}

	/**
	 * Constructs an empty <code>StringField</code> with given caption and given initial value.
	 * @param caption the field caption.
	 * @param value Initial value.
	 */
	public StringField(String caption, String value) {
		super(caption, value);
		init();
	}

	/**
	 * Constructs an empty <code>StringField</code> with given caption.
	 * @param caption the field caption.
	 */
	public StringField(String caption) {
		super(caption);
		init();
	}

	/**
	 * Init field
	 */
	protected void init() {
		addStyleName("h-field");
		addStyleName("h-stringfield");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Init field with and intenal <code>null</code> value, instead of default empty String value
	 */
	public void initWithNullValue() {
		super.setInternalValue(null);
	}

	/**
	 * Gets whether to treat empty String values as <code>null</code> values
	 * @return <code>true</code> to treat empty String values as <code>null</code> values, false otherwise
	 */
	public boolean isEmptyValuesAsNull() {
		return emptyValuesAsNull;
	}

	/**
	 * Sets whether to treat empty String values as <code>null</code> values
	 * @param emptyValuesAsNull <code>true</code> to treat empty String values as <code>null</code> values, false
	 *        otherwise
	 */
	public void setEmptyValuesAsNull(boolean emptyValuesAsNull) {
		this.emptyValuesAsNull = emptyValuesAsNull;
	}

	/**
	 * Gets whether to treat blank String values (empty or whitespaces only) as <code>null</code> values
	 * @return <code>true</code> to treat blank String values (empty or whitespaces only) as <code>null</code> values,
	 *         false otherwise
	 */
	public boolean isBlankValuesAsNull() {
		return blankValuesAsNull;
	}

	/**
	 * Sets whether to treat blank String values (empty or whitespaces only) as <code>null</code> values
	 * @param blankValuesAsNull <code>true</code> to treat blank String values (empty or whitespaces only) as
	 *        <code>null</code> values, false otherwise
	 */
	public void setBlankValuesAsNull(boolean blankValuesAsNull) {
		this.blankValuesAsNull = blankValuesAsNull;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getEmptyValue()
	 */
	@Override
	public String getEmptyValue() {
		return (isBlankValuesAsNull() || isEmptyValuesAsNull()) ? null : "";
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#setInternalValue(java.lang.String)
	 */
	@Override
	protected void setInternalValue(String newValue) {
		super.setInternalValue(sanitizeValue(newValue));
	}

	/**
	 * Checkup String value to apply empty-as-null or blank-as-null behaviours, if enabled.
	 * @param value Value to check
	 * @return Sanitized value
	 */
	protected String sanitizeValue(String value) {
		if (value != null) {
			if (isBlankValuesAsNull() && value.trim().equals("")) {
				return null;
			}
			if (isEmptyValuesAsNull() && value.length() == 0) {
				return null;
			}
		}
		return value;
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
			com.holonplatform.vaadin.components.Input.ValueChangeListener<String> listener) {
		return ValueChangeNotifierRegistration.adapt(this, this, listener);
	}

	// Builder

	/**
	 * Builder to create {@link StringField} instances.
	 * <p>
	 * By default, this builder sets the empty String as <code>null</code> representation and null settings allowed to
	 * true. The internal field value is inited with <code>null</code>.
	 * </p>
	 */
	public static class Builder extends AbstractStringFieldBuilder<StringField> {

		public Builder() {
			super(new StringField());
			getInstance().setNullRepresentation("");
			getInstance().setNullSettingAllowed(true);
			getInstance().initWithNullValue();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected StringInputBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.StringFieldBuilder#emptyValuesAsNull(boolean)
		 */
		@Override
		public StringInputBuilder emptyValuesAsNull(boolean enable) {
			getInstance().setEmptyValuesAsNull(enable);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.StringFieldBuilder#blankValuesAsNull(boolean)
		 */
		@Override
		public StringInputBuilder blankValuesAsNull(boolean enable) {
			getInstance().setBlankValuesAsNull(enable);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<String> build(StringField instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<String> buildAsField(StringField instance) {
			return instance;
		}

	}

}
