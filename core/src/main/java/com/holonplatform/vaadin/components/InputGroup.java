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

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.vaadin.internal.components.DefaultInputGroup;
import com.vaadin.ui.Field;

/**
 * Collect a set of {@link Input}s to perform operations on the inputs group, like validation and value reset.
 * <p>
 * The {@link #validateValue()} method will validate the current value of all the inputs of the group.
 * </p>
 *
 * @since 5.0.0
 */
public interface InputGroup extends ValidatableValue, Serializable {

	/**
	 * Clear (reset the value) all the group inputs.
	 */
	void clear();

	/**
	 * Set the read-only mode for all the group inputs.
	 * @param readOnly <code>true</code> to set all inputs as read-only, <code>false</code> to unset
	 */
	void setReadOnly(boolean readOnly);

	// Builder

	/**
	 * Get a builder to create {@link InputGroup} instances.
	 * @return {@link InputGroup} builder
	 */
	static Builder builder() {
		return new DefaultInputGroup.DefaultBuilder();
	}

	/**
	 * {@link InputGroup} builder.
	 */
	public interface Builder {

		/**
		 * Add one or more input to the group.
		 * @param inputs Inputs to add
		 * @return this
		 */
		Builder with(Input<?>... inputs);

		/**
		 * Add inputs to the group.
		 * @param inputs Input {@link Iterable} to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		Builder with(Iterable<Input> inputs);

		/**
		 * Add one or more inputs to the group.
		 * @param fields Input {@link Field}s to add
		 * @return this
		 */
		Builder with(Field<?>... fields);

		/**
		 * Add inputs to the group.
		 * @param fields Input {@link Field}s {@link Iterable} to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		Builder withFields(Iterable<Field> fields);

		/**
		 * Set whether to stop inputs validation at first validation failure. If <code>true</code>, only the first
		 * {@link ValidationException} is thrown at validation, otherwise a {@link ValidationException} containing all
		 * the occurred validation exception is thrown.
		 * @param stopValidationAtFirstFailure <code>true</code> to stop inputs validation at first validation failure
		 * @return this
		 */
		Builder stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure);

		/**
		 * Set a default {@link ValidationErrorHandler} to use to handle {@link ValidationException} when validation is
		 * performed.
		 * <p>
		 * If a default {@link ValidationErrorHandler} is configured, the {@link InputGroup#validate()} will never throw
		 * any {@link ValidationException}, delegating validation error management to the default handler.
		 * </p>
		 * @param validationErrorHandler The handler to set
		 * @return this
		 */
		Builder defaultValidationErrorHandler(ValidationErrorHandler validationErrorHandler);

		/**
		 * Build the {@link InputGroup} instance.
		 * @return a new {@link InputGroup}
		 */
		InputGroup build();

	}

}
