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
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.Registration;
import com.holonplatform.vaadin.components.builders.InvalidInputNotificationMode;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Field;

/**
 * Base class to build a custom {@link Field} wrapping another Field, maybe with a different value type, that acts as
 * presenter and inout handler in UI.
 * 
 * <p>
 * This field automatically adds {@link Components#DEFAULT_INVALID_FIELD_STYLE_CLASS} style name to the wrapped internal
 * field when it is in an invalid state, according to any registered validator.
 * </p>
 * 
 * <p>
 * Any style name added to this Field using {@link #addStyleName(String)} is reflected to internal field, to ensure
 * visual consistency using common field styles.
 * </p>
 * 
 * @param <T> Field type
 * @param <F> Internal field type
 * 
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractCustomField<T, F extends Field> extends CustomField<T>
		implements Input<T>, ValidatableField<T> {

	private static final long serialVersionUID = -5952052029882768908L;

	/**
	 * Default style class name for invalid fields
	 */
	static final String DEFAULT_INVALID_FIELD_STYLE_CLASS = "error";

	/**
	 * Field type
	 */
	private final Class<? extends T> type;

	/**
	 * Internal Field
	 */
	private F internalField;

	/**
	 * Invalid notification mode
	 */
	private InvalidInputNotificationMode invalidFieldNotificationMode = InvalidInputNotificationMode.defaultMode();

	/**
	 * Flag to temporary suspend validation
	 */
	private boolean suspendValidationNotification = false;

	/**
	 * Field attached and composed
	 */
	private boolean composed = false;

	/**
	 * Value change listener for validation when internal field value changes
	 */
	private final com.vaadin.data.Property.ValueChangeListener onChangeValidation = (e) -> {
		if (composed) {
			try {
				setSuspendValidationNotification(false);
				validateValue();
			} catch (@SuppressWarnings("unused") InvalidValueException ive) {
			}
		}
	};

	/**
	 * Constructor
	 * @param type Field concrete type
	 */
	public AbstractCustomField(Class<? extends T> type) {
		this(type, true);
	}

	/**
	 * Constructor
	 * @param type Field concrete type
	 * @param init <code>true</code> to init the internal field. If <code>false</code>, subclasses must ensure the
	 *        {@link #init()} method to called after construction.
	 */
	public AbstractCustomField(Class<? extends T> type, boolean init) {
		super();

		ObjectUtils.argumentNotNull(type, "Field type must be not null");

		this.type = type;

		setWidthUndefined();

		addStyleName("h-field", false);

		if (init) {
			init();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.CustomField#attach()
	 */
	@Override
	public void attach() {
		super.attach();
		composed = true;
	}

	/**
	 * Initialize and configure the internal wrapped field
	 */
	protected void init() {
		// build internal field
		this.internalField = buildInternalField(getType());

		// use this field as data source for internal field
		if (this.internalField instanceof AbstractComponent) {
			((AbstractComponent) this.internalField).setImmediate(true);
		}
		this.internalField.setBuffered(false);
		this.internalField.setPropertyDataSource(this);

		// setup invalid notification mode
		ValidationUtils.setupInvalidFieldNotificationMode(this);

		if (getInvalidFieldNotificationMode() == InvalidInputNotificationMode.USER_INPUT_AND_EXPLICIT_VALIDATION) {
			this.internalField.addValueChangeListener(onChangeValidation);
		}
	}

	/**
	 * Build concrete internal Field.
	 * <p>
	 * If internal Field type is not consistent with the custom field type, it is the responsibility of subclasses to
	 * provide a suitable Converter for the internal Field.
	 * </p>
	 * @param type Concrete field type
	 * @return Internal field (must be not null)
	 */
	protected abstract F buildInternalField(Class<? extends T> type);

	/**
	 * Gets the wrapped field
	 * @return the internal field
	 */
	protected F getInternalField() {
		return internalField;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<? extends T> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		final Field<?> content = getInternalField();
		content.setWidth(100, Unit.PERCENTAGE);
		if (getHeight() > -1) {
			content.setHeight(100, Unit.PERCENTAGE);
		}
		return content;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#addStyleName(java.lang.String)
	 */
	@Override
	public void addStyleName(String style) {
		super.addStyleName(style);
		// add to internal field too
		getInternalField().addStyleName(style);
	}

	/**
	 * Adds one or more style names to this component.
	 * @param styleName Style name to add
	 * @param reflectToInternalField <code>true</code> to add given <code>styleName</code> to internal field too
	 */
	protected void addStyleName(String styleName, boolean reflectToInternalField) {
		super.addStyleName(styleName);
		if (reflectToInternalField) {
			getInternalField().addStyleName(styleName);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#removeStyleName(java.lang.String)
	 */
	@Override
	public void removeStyleName(String style) {
		super.removeStyleName(style);
		getContent().removeStyleName(style);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		// reflect to internal field
		getInternalField().setReadOnly(readOnly);
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
	 * @see com.vaadin.ui.AbstractField#validateValue()
	 */
	@Override
	public void validate() throws InvalidValueException {
		getInternalField().removeStyleName(DEFAULT_INVALID_FIELD_STYLE_CLASS);

		ValidationUtils.preValidate(this);

		try {
			super.validate();
		} catch (InvalidValueException ive) {
			if (isValidationVisible()) {
				getInternalField().addStyleName(DEFAULT_INVALID_FIELD_STYLE_CLASS);
			}
			throw ive;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ValidatableField#addValidator(com.holonplatform.core.validator.Validator)
	 */
	@Override
	public void addValidator(Validator<T> validator) {
		addValidator(ValidationUtils.asVaadinValidator(validator));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#removeValidator(com.holonplatform.core.validator.
	 * Validator)
	 */
	@Override
	public void removeValidator(Validator<T> validator) {
		ValidationUtils.removeValidator(this, validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableField#getInvalidFieldNotificationMode()
	 */
	@Override
	public InvalidInputNotificationMode getInvalidFieldNotificationMode() {
		return invalidFieldNotificationMode;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ValidatableField#setInvalidFieldNotificationMode(com.holonplatform.vaadin.
	 * components.ValidatableField.InvalidFieldNotificationMode)
	 */
	@Override
	public void setInvalidFieldNotificationMode(InvalidInputNotificationMode invalidFieldNotificationMode) {
		ObjectUtils.argumentNotNull(invalidFieldNotificationMode, "InvalidFieldNotificationMode must be not null");
		this.invalidFieldNotificationMode = invalidFieldNotificationMode;

		if (invalidFieldNotificationMode != InvalidInputNotificationMode.ALWAYS) {
			getInternalField().removeStyleName(DEFAULT_INVALID_FIELD_STYLE_CLASS);
		}
		ValidationUtils.setupInvalidFieldNotificationMode(this);

		this.internalField.removeValueChangeListener(onChangeValidation);
		if (getInvalidFieldNotificationMode() == InvalidInputNotificationMode.USER_INPUT_AND_EXPLICIT_VALIDATION) {
			this.internalField.addValueChangeListener(onChangeValidation);
		}
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
			setInvalidFieldNotificationMode(InvalidInputNotificationMode.NEVER);
		} else {
			setInvalidFieldNotificationMode(InvalidInputNotificationMode.defaultMode());
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
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T newFieldValue) throws com.vaadin.data.Property.ReadOnlyException, ConversionException {
		ValidationUtils.preValueSet(this);
		getInternalField().removeStyleName(DEFAULT_INVALID_FIELD_STYLE_CLASS);
		super.setValue(newFieldValue);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#addValueChangeListener(com.holonplatform.vaadin.components.Input.
	 * ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			com.holonplatform.vaadin.components.Input.ValueChangeListener<T> listener) {
		return ValueChangeNotifierRegistration.adapt(this, this, listener);
	}

}
