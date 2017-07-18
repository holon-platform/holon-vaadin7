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

import java.util.Date;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.Registration;
import com.holonplatform.vaadin.components.builders.DateFieldBuilder;
import com.holonplatform.vaadin.components.builders.InvalidFieldNotificationMode;
import com.holonplatform.vaadin.internal.components.builders.AbstractDateFieldBuilder;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

/**
 * {@link com.vaadin.ui.DateField} extension to handle {@link Date} values, supporting {@link ValidatableField}
 * features.
 * 
 * @since 5.0.0
 */
public class DateField extends com.vaadin.ui.DateField implements Input<Date>, ValidatableField<Date> {

	private static final long serialVersionUID = -4077699329772463783L;

	/**
	 * Invalid field notification mode
	 */
	private InvalidFieldNotificationMode invalidFieldNotificationMode = InvalidFieldNotificationMode.defaultMode();

	/**
	 * Flag to temporary suspend validation
	 */
	private boolean suspendValidationNotification = false;

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

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ValidatableField#addValidator(com.holonplatform.core.validator.Validator)
	 */
	@Override
	public void addValidator(Validator<Date> validator) {
		addValidator(ValidationUtils.asVaadinValidator(validator));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#removeValidator(com.holonplatform.core.validator.
	 * Validator)
	 */
	@Override
	public void removeValidator(Validator<Date> validator) {
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
	 * @see com.vaadin.ui.AbstractTextField#setValue(java.util.Date)
	 */
	@Override
	public void setValue(Date newValue) throws ReadOnlyException {
		ValidationUtils.preValueSet(this);
		super.setValue(newValue);
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

		public Builder() {
			super(new DateField());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected DateFieldBuilder builder() {
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
