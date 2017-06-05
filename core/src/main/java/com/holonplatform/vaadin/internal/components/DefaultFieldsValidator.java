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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Validator.ValidationErrorHandler;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.FieldsValidator;
import com.holonplatform.vaadin.components.FieldsValidator.Builder;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Field;

/**
 * Default {@link FieldsValidator} implementation.
 *
 * @since 5.0.0
 */
public class DefaultFieldsValidator implements FieldsValidator {

	private static final long serialVersionUID = -7331679742519867668L;

	@SuppressWarnings("rawtypes")
	private final List<Field> fields = new LinkedList<>();

	/**
	 * Field validation behaviour
	 */
	private boolean stopFieldValidationAtFirstFailure = false;

	/**
	 * Default {@link ValidationErrorHandler}
	 */
	private ValidationErrorHandler defaultValidationErrorHandler;

	/**
	 * Constructor
	 */
	public DefaultFieldsValidator() {
		super();
	}

	/**
	 * Add a {@link Field} to validate.
	 * @param field Field to add
	 */
	public void addField(Field<?> field) {
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		fields.add(field);
	}

	/**
	 * Get whether to stop field validation at first validation failure.
	 * @return whether to stop field validation at first validation failure
	 */
	public boolean isStopFieldValidationAtFirstFailure() {
		return stopFieldValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop field validation at first validation failure.
	 * @param stopFieldValidationAtFirstFailure <code>true</code> to stop field validation at first validation failure
	 */
	public void setStopFieldValidationAtFirstFailure(boolean stopFieldValidationAtFirstFailure) {
		this.stopFieldValidationAtFirstFailure = stopFieldValidationAtFirstFailure;
	}

	/**
	 * Get the default {@link ValidationErrorHandler} to use.
	 * @return the default ValidationErrorHandler
	 */
	public Optional<ValidationErrorHandler> getDefaultValidationErrorHandler() {
		return Optional.ofNullable(defaultValidationErrorHandler);
	}

	/**
	 * Set the default {@link ValidationErrorHandler} to use.
	 * @param defaultValidationErrorHandler the default ValidationErrorHandler to set
	 */
	public void setDefaultValidationErrorHandler(ValidationErrorHandler defaultValidationErrorHandler) {
		this.defaultValidationErrorHandler = defaultValidationErrorHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.FieldsValidator#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		if (getDefaultValidationErrorHandler().isPresent()) {
			validate(getDefaultValidationErrorHandler().get());
		} else {
			doValidate();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.FieldsValidator#validate(com.holonplatform.core.Validator.
	 * ValidationErrorHandler)
	 */
	@Override
	public boolean validate(ValidationErrorHandler handler) {
		ObjectUtils.argumentNotNull(handler, "ValidationErrorHandler must be not null");
		try {
			doValidate();
		} catch (ValidationException e) {
			handler.handleValidationError(e);
			return false;
		}
		return true;
	}

	/**
	 * Validate all fields
	 * @throws ValidationException If at least one field is not valid
	 */
	protected void doValidate() throws ValidationException {
		LinkedList<ValidationException> validationExceptions = new LinkedList<>();
		fields.forEach(f -> {
			ValidationException ve = validateField(f);
			if (ve != null) {
				if (isStopFieldValidationAtFirstFailure()) {
					throw ve;
				}
				validationExceptions.add(ve);
			}
		});
		if (!validationExceptions.isEmpty()) {
			if (validationExceptions.size() == 1) {
				throw validationExceptions.getFirst();
			} else {
				throw new ValidationException(validationExceptions.toArray(new ValidationException[0]));
			}
		}
	}

	/**
	 * Validate given Field and return a translated {@link ValidationException} if validation fails.
	 * @param field Field to validate
	 * @return Validation exception
	 */
	protected ValidationException validateField(@SuppressWarnings("rawtypes") Field field) {
		try {
			field.validate();
		} catch (InvalidValueException e) {
			return ValidationUtils.translateValidationException(e);
		}
		return null;
	}

	// Builder

	/**
	 * Default {@link Builder} implementation.
	 */
	public static class DefaultBuilder implements Builder {

		private final DefaultFieldsValidator instance = new DefaultFieldsValidator();

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.FieldsValidator.Builder#add(com.vaadin.ui.Field)
		 */
		@Override
		public Builder add(Field<?> field) {
			instance.addField(field);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.FieldsValidator.Builder#add(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Builder add(Iterable<Field> fields) {
			ObjectUtils.argumentNotNull(fields, "Fields must be not null");
			fields.forEach(f -> instance.addField(f));
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.FieldsValidator.Builder#stopFieldValidationAtFirstFailure(boolean)
		 */
		@Override
		public Builder stopFieldValidationAtFirstFailure(boolean stopFieldValidationAtFirstFailure) {
			instance.setStopFieldValidationAtFirstFailure(stopFieldValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.FieldsValidator.Builder#defaultValidationErrorHandler(com.holonplatform
		 * .core.Validator.ValidationErrorHandler)
		 */
		@Override
		public Builder defaultValidationErrorHandler(ValidationErrorHandler validationErrorHandler) {
			instance.setDefaultValidationErrorHandler(validationErrorHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.FieldsValidator.Builder#build()
		 */
		@Override
		public FieldsValidator build() {
			return instance;
		}

	}

}
