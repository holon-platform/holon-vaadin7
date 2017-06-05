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
package com.holonplatform.vaadin.components;

import java.io.Serializable;

import com.holonplatform.core.Validator.ValidationErrorHandler;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.vaadin.internal.components.DefaultFieldsValidator;
import com.vaadin.ui.Field;

/**
 * Interface to validate a set of {@link Field}s.
 *
 * @since 5.0.0
 */
public interface FieldsValidator extends Serializable {

	/**
	 * Validate all the fields.
	 * @throws ValidationException If at least one field is not valid
	 */
	void validate() throws ValidationException;

	/**
	 * Validate all the fields, using given {@link ValidationErrorHandler} to handle any {@link ValidationException}.
	 * <p>
	 * This method does not throw any {@link ValidationException}, delegating error management to given error handler.
	 * </p>
	 * @param handler Validation error handler (not null)
	 * @return <code>true</code> if all fields are valid, <code>false</code> otherwise
	 */
	boolean validate(ValidationErrorHandler handler);

	/**
	 * Check if the fields are valid, swallowing any validation exception.
	 * @return <code>true</code> if all the fields are valid
	 */
	default boolean isValid() {
		try {
			validate();
		} catch (@SuppressWarnings("unused") ValidationException e) {
			return false;
		}
		return true;
	}

	// Builders

	/**
	 * Get a builder to create {@link FieldsValidator} instances.
	 * @return {@link FieldsValidator} builder
	 */
	static Builder builder() {
		return new DefaultFieldsValidator.DefaultBuilder();
	}

	/**
	 * {@link FieldsValidator} builder.
	 */
	public interface Builder {

		/**
		 * Add a {@link Field} to validate.
		 * @param field Field to add (not null)
		 * @return this
		 */
		Builder add(Field<?> field);

		/**
		 * Add {@link Field}s to validate.
		 * @param fields Field {@link Iterable} to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		Builder add(Iterable<Field> fields);

		/**
		 * Set whether to stop fields validation at first validation failure. If <code>true</code>, only the first
		 * {@link ValidationException} is thrown at validation, otherwise a {@link ValidationException} containing all
		 * the occurred validation exception is thrown.
		 * @param stopFieldValidationAtFirstFailure <code>true</code> to stop field validation at first validation
		 *        failure
		 * @return this
		 */
		Builder stopFieldValidationAtFirstFailure(boolean stopFieldValidationAtFirstFailure);

		/**
		 * Set a default {@link ValidationErrorHandler} to use to handle {@link ValidationException} when validation is
		 * performed.
		 * <p>
		 * If a default {@link ValidationErrorHandler} is configured, the {@link FieldsValidator#validate()} will never
		 * throw any {@link ValidationException}, delegating validation error management to the default handler.
		 * </p>
		 * @param validationErrorHandler The handler to set
		 * @return this
		 */
		Builder defaultValidationErrorHandler(ValidationErrorHandler validationErrorHandler);

		/**
		 * Build the {@link FieldsValidator} instance.
		 * @return FieldsValidator
		 */
		FieldsValidator build();

	}

}
