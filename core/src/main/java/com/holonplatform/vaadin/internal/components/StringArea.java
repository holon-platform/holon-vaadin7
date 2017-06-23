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

import com.holonplatform.core.Validator;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ValidatableField;
import com.holonplatform.vaadin.components.builders.StringFieldBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractStringFieldBuilder;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

/**
 * {@link TextArea} extension to handle {@link String} values, supporting {@link ValidatableField} features.
 * 
 * @since 5.0.0
 */
public class StringArea extends TextArea implements ValidatableField<String> {

	private static final long serialVersionUID = -4631138014420796152L;

	/**
	 * Invalid field notification mode
	 */
	private InvalidFieldNotificationMode invalidFieldNotificationMode = InvalidFieldNotificationMode.defaultMode();

	/**
	 * Flag to temporary suspend validation
	 */
	private boolean suspendValidationNotification = false;

	/**
	 * Treat empty values as <code>null</code> values
	 */
	private boolean emptyValuesAsNull = true;

	/**
	 * Treat blank values as <code>null</code> values
	 */
	private boolean blankValuesAsNull = false;

	/**
	 * Constructs an empty <code>StringArea</code> with no caption.
	 */
	public StringArea() {
		super();
		init();
	}

	/**
	 * Constructs a new <code>StringArea</code> that's bound to the specified <code>Property</code> and has no caption.
	 * @param dataSource the Property to be edited with this editor.
	 */
	@SuppressWarnings("rawtypes")
	public StringArea(Property dataSource) {
		super(dataSource);
		init();
	}

	/**
	 * Constructs a new <code>StringArea</code> that's bound to the specified <code>Property</code> and has the given
	 * caption <code>String</code>.
	 * @param caption the caption for the editor.
	 * @param dataSource the Property to be edited with this editor.
	 */
	@SuppressWarnings("rawtypes")
	public StringArea(String caption, Property dataSource) {
		super(caption, dataSource);
		init();
	}

	/**
	 * Constructs an empty <code>StringArea</code> with given caption and given initial value.
	 * @param caption the field caption.
	 * @param value Initial value.
	 */
	public StringArea(String caption, String value) {
		super(caption, value);
		init();
	}

	/**
	 * Constructs an empty <code>StringArea</code> with given caption.
	 * @param caption the field caption.
	 */
	public StringArea(String caption) {
		super(caption);
		init();
	}

	/**
	 * Init field
	 */
	protected void init() {

		addStyleName("h-field");
		addStyleName("h-stringfield");

		ValidationUtils.setupInvalidFieldNotificationMode(this);
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
	 * @see
	 * com.holonplatform.vaadin.components.ValidatableField#addValidator(com.holonplatform.core.validator.Validator)
	 */
	@Override
	public void addValidator(Validator<String> validator) {
		addValidator(ValidationUtils.asVaadinValidator(validator));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#removeValidator(com.holonplatform.core.validator.
	 * Validator)
	 */
	@Override
	public void removeValidator(Validator<String> validator) {
		ValidationUtils.removeValidator(this, validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#getInvalidFieldNotificationMode()
	 */
	@Override
	public InvalidFieldNotificationMode getInvalidFieldNotificationMode() {
		return invalidFieldNotificationMode;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ValidatableField#setInvalidFieldNotificationMode(com.holonplatform.vaadin.
	 * components.ValidatableField.InvalidFieldNotificationMode)
	 */
	@Override
	public void setInvalidFieldNotificationMode(InvalidFieldNotificationMode invalidFieldNotificationMode) {
		ObjectUtils.argumentNotNull(invalidFieldNotificationMode, "InvalidFieldNotificationMode must be not null");
		this.invalidFieldNotificationMode = invalidFieldNotificationMode;

		ValidationUtils.setupInvalidFieldNotificationMode(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#isSuspendValidationNotification()
	 */
	@Override
	public boolean isSuspendValidationNotification() {
		return suspendValidationNotification;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#setSuspendValidationNotification(boolean)
	 */
	@Override
	public void setSuspendValidationNotification(boolean suspendValidationNotification) {
		this.suspendValidationNotification = suspendValidationNotification;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#changeValidationVisibility(boolean)
	 */
	@Override
	public void changeValidationVisibility(boolean visible) {
		super.setValidationVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setValidationVisible(boolean)
	 */
	@Override
	public void setValidationVisible(boolean validateAutomatically) {
		super.setValidationVisible(validateAutomatically);
		if (!validateAutomatically) {
			setInvalidFieldNotificationMode(InvalidFieldNotificationMode.NEVER);
		} else {
			setInvalidFieldNotificationMode(InvalidFieldNotificationMode.defaultMode());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#beforeClientResponse(boolean)
	 */
	@Override
	public void beforeClientResponse(boolean initial) {
		ValidationUtils.beforeClientResponse(this, initial, (i) -> super.beforeClientResponse(i));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#validate()
	 */
	@Override
	public void validate() throws InvalidValueException {
		ValidationUtils.preValidate(this);
		super.validate();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String newValue) throws ReadOnlyException {
		ValidationUtils.preValueSet(this);
		super.setValue(sanitizeValue(newValue));
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

	// Builder

	/**
	 * Builder to create {@link StringArea} instances.
	 * <p>
	 * By default, this builder sets the empty String as <code>null</code> representation and null settings allowed to
	 * true. The internal field value is inited with <code>null</code>.
	 * </p>
	 */
	public static class Builder extends AbstractStringFieldBuilder<StringArea> {

		public Builder() {
			super(new StringArea());
			getInstance().setNullRepresentation("");
			getInstance().setNullSettingAllowed(true);
			getInstance().initWithNullValue();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected StringFieldBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
		 * AbstractComponent)
		 */
		@Override
		protected Field<String> build(StringArea instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.StringFieldBuilder#emptyValuesAsNull(boolean)
		 */
		@Override
		public StringFieldBuilder emptyValuesAsNull(boolean enable) {
			getInstance().setEmptyValuesAsNull(enable);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.StringFieldBuilder#blankValuesAsNull(boolean)
		 */
		@Override
		public StringFieldBuilder blankValuesAsNull(boolean enable) {
			getInstance().setBlankValuesAsNull(enable);
			return builder();
		}

	}

}
