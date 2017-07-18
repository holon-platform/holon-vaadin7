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
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.Registration;
import com.holonplatform.vaadin.components.builders.InvalidFieldNotificationMode;
import com.holonplatform.vaadin.components.builders.SecretFieldBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractValidatableFieldBuilder;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.PasswordField;

/**
 * A field that is used to enter secret text information like passwords. The entered text is not displayed on the
 * screen.
 *
 * @since 5.0.0
 */
public class SecretField extends PasswordField implements Input<String>, ValidatableField<String> {

	private static final long serialVersionUID = -288626996273192490L;

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
	 * Constructor
	 */
	public SecretField() {
		super();
		addStyleName("h-field");
		addStyleName("h-secretfield");

		ValidationUtils.setupInvalidFieldNotificationMode(this);
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
	 * @see com.holonplatform.vaadin.components.ValidatableField#addValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public void addValidator(Validator<String> validator) {
		addValidator(ValidationUtils.asVaadinValidator(validator));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#removeValidator(com.holonplatform.core.Validator)
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
	 * @see com.holonplatform.vaadin.components.ValidatableValue#validateValue()
	 */
	@Override
	public void validateValue() throws ValidationException {
		try {
			validate();
		} catch (InvalidValueException e) {
			throw ValidationUtils.translateValidationException(e);
		}
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
	 * Default {@link SecretFieldBuilder} implementation.
	 *
	 * @since 5.0.0
	 */
	public static class Builder
			extends AbstractValidatableFieldBuilder<String, Input<String>, SecretField, SecretFieldBuilder>
			implements SecretFieldBuilder {

		/**
		 * Constructor
		 */
		public Builder() {
			super(new SecretField());
			getInstance().setNullRepresentation("");
			getInstance().setNullSettingAllowed(true);
			getInstance().initWithNullValue();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SecretFieldBuilder#emptyValuesAsNull(boolean)
		 */
		@Override
		public SecretFieldBuilder emptyValuesAsNull(boolean enable) {
			getInstance().setEmptyValuesAsNull(enable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SecretFieldBuilder#blankValuesAsNull(boolean)
		 */
		@Override
		public SecretFieldBuilder blankValuesAsNull(boolean enable) {
			getInstance().setBlankValuesAsNull(enable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected SecretFieldBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<String> build(SecretField instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<String> buildAsField(SecretField instance) {
			return instance;
		}

	}

}
