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
import com.holonplatform.vaadin.components.builders.BooleanFieldBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractValidatableFieldBuilder;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;

/**
 * A {@link Boolean} type {@link Field} using a {@link CheckBox} as UI widget, supporting {@link ValidatableField}
 * features.
 *
 * @since 5.0.0
 */
public class BooleanField extends CheckBox implements ValidatableField<Boolean> {

	private static final long serialVersionUID = 8256370094165483325L;

	/**
	 * Invalid field notification mode
	 */
	private InvalidFieldNotificationMode invalidFieldNotificationMode = InvalidFieldNotificationMode.defaultMode();

	/**
	 * Flag to temporary suspend validation
	 */
	private boolean suspendValidationNotification = false;

	/**
	 * Treat <code>null</code> values as {@link Boolean#FALSE} values
	 */
	private boolean nullValueAsFalse = false;

	/**
	 * Creates a new <code>BooleanField</code> without caption.
	 */
	public BooleanField() {
		super();
		init();
	}

	/**
	 * Creates a new <code>BooleanField</code> with given caption and initial value.
	 * @param caption Field caption
	 * @param initialState Initial value
	 */
	public BooleanField(String caption, boolean initialState) {
		super(caption, initialState);
		init();
	}

	/**
	 * Creates a new <code>BooleanField</code> that is connected to a boolean property.
	 * @param caption Field caption
	 * @param dataSource the Property to be edited with this editor
	 */
	public BooleanField(String caption, Property<?> dataSource) {
		super(caption, dataSource);
		init();
	}

	/**
	 * Creates a new <code>BooleanField</code> with given caption.
	 * @param caption Field caption
	 */
	public BooleanField(String caption) {
		super(caption);
		init();
	}

	/**
	 * Init field
	 */
	protected void init() {

		addStyleName("h-field");
		addStyleName("h-booleanfield");

		ValidationUtils.setupInvalidFieldNotificationMode(this);
	}

	/**
	 * Gets whether to treat <code>null</code> values as {@link Boolean#FALSE} values
	 * @return <code>true</code> to treat <code>null</code> values as {@link Boolean#FALSE} values
	 */
	public boolean isNullValueAsFalse() {
		return nullValueAsFalse;
	}

	/**
	 * Sets whether to treat <code>null</code> values as {@link Boolean#FALSE} values
	 * @param nullValueAsFalse <code>true</code> to treat <code>null</code> values as {@link Boolean#FALSE} values
	 */
	public void setNullValueAsFalse(boolean nullValueAsFalse) {
		this.nullValueAsFalse = nullValueAsFalse;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ValidatableField#addValidator(com.holonplatform.core.validator.Validator)
	 */
	@Override
	public void addValidator(Validator<Boolean> validator) {
		addValidator(ValidationUtils.asVaadinValidator(validator));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#removeValidator(com.holonplatform.core.validator.
	 * Validator)
	 */
	@Override
	public void removeValidator(Validator<Boolean> validator) {
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
	 * @see com.vaadin.ui.AbstractTextField#beforeClientResponse(boolean)
	 */
	@Override
	public void beforeClientResponse(boolean initial) {
		ValidationUtils.beforeClientResponse(this, initial, (i) -> super.beforeClientResponse(i));
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
	 * @see com.vaadin.ui.AbstractField#validate()
	 */
	@Override
	public void validate() throws InvalidValueException {
		ValidationUtils.preValidate(this);
		super.validate();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#setValue(java.lang.Boolean)
	 */
	@Override
	public void setValue(Boolean newValue) throws ReadOnlyException {
		ValidationUtils.preValueSet(this);
		super.setValue(newValue);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.CheckBox#setInternalValue(java.lang.Boolean)
	 */
	@Override
	protected void setInternalValue(Boolean newValue) {
		super.setInternalValue(sanitizeValue(newValue));
	}

	/**
	 * Checkup Boolean value to apply null-as-false behaviour, if enabled.
	 * @param value Value to check
	 * @return Sanitized value
	 */
	protected Boolean sanitizeValue(Boolean value) {
		if (value == null && isNullValueAsFalse()) {
			return Boolean.FALSE;
		}
		return value;
	}

	// Builder

	/**
	 * Builder to create {@link BooleanField} instances
	 */
	public static class Builder extends AbstractValidatableFieldBuilder<Boolean, Field<Boolean>, BooleanField, BooleanFieldBuilder>
			implements BooleanFieldBuilder {

		/**
		 * Constructor
		 */
		public Builder() {
			super(new BooleanField());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BooleanFieldBuilder#nullValueAsFalse(boolean)
		 */
		@Override
		public BooleanFieldBuilder nullValueAsFalse(boolean nullValueAsFalse) {
			getInstance().setNullValueAsFalse(nullValueAsFalse);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BooleanFieldBuilder#withFocusListener(com.vaadin.event.
		 * FieldEvents.FocusListener)
		 */
		@Override
		public BooleanFieldBuilder withFocusListener(FocusListener listener) {
			getInstance().addFocusListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BooleanFieldBuilder#withBlurListener(com.vaadin.event.
		 * FieldEvents.BlurListener)
		 */
		@Override
		public BooleanFieldBuilder withBlurListener(BlurListener listener) {
			getInstance().addBlurListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected BooleanFieldBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
		 * AbstractComponent)
		 */
		@Override
		protected Field<Boolean> build(BooleanField instance) {
			return instance;
		}

	}

}
