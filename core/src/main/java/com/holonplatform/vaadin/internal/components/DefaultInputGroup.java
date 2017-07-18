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

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.InputGroup;
import com.holonplatform.vaadin.components.ValidationErrorHandler;
import com.vaadin.ui.Field;

/**
 * Default {@link InputGroup} implementation.
 *
 * @since 5.0.0
 */
public class DefaultInputGroup implements InputGroup {

	private static final long serialVersionUID = -7331679742519867668L;

	@SuppressWarnings("rawtypes")
	private final List<Input> inputs = new LinkedList<>();

	/**
	 * Validation behaviour
	 */
	private boolean stopValidationAtFirstFailure = false;

	/**
	 * Default {@link ValidationErrorHandler}
	 */
	private ValidationErrorHandler defaultValidationErrorHandler;

	/**
	 * Constructor
	 */
	public DefaultInputGroup() {
		super();
	}

	/**
	 * Adds an {@link Input} to the group.
	 * @param input the {@link Input} to add (not null)
	 */
	public void add(Input<?> input) {
		ObjectUtils.argumentNotNull(input, "Input to add must be not null");
		inputs.add(input);
	}

	/**
	 * Get whether to stop validation at first validation failure.
	 * @return whether to stop validation at first validation failure
	 */
	public boolean isStopValidationAtFirstFailure() {
		return stopValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop validation at first validation failure.
	 * @param stopValidationAtFirstFailure <code>true</code> to stop validation at first validation failure
	 */
	public void setStopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
		this.stopValidationAtFirstFailure = stopValidationAtFirstFailure;
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
	 * @see com.holonplatform.vaadin.components.InputGroup#clear()
	 */
	@Override
	public void clear() {
		inputs.forEach(input -> input.clear());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.InputGroup#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		inputs.forEach(input -> input.setReadOnly(readOnly));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.InputGroup#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		inputs.forEach(input -> input.getComponent().setEnabled(enabled));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.FieldsValidator#validate()
	 */
	@Override
	public void validateValue() throws ValidationException {
		final LinkedList<ValidationException> validationExceptions = new LinkedList<>();
		inputs.forEach(f -> {
			try {
				f.validateValue();
			} catch (ValidationException ve) {
				if (isStopValidationAtFirstFailure()) {
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableValue#isValid(com.holonplatform.vaadin.components.
	 * ValidationErrorHandler)
	 */
	@Override
	public boolean isValid(ValidationErrorHandler handler) {
		try {
			validateValue();
		} catch (ValidationException e) {
			ValidationErrorHandler h = (handler != null) ? handler : getDefaultValidationErrorHandler().orElse(null);
			if (h != null) {
				h.handleValidationError(e);
			}
			return false;
		}
		return true;
	}

	// Builder

	/**
	 * Default {@link Builder} implementation.
	 */
	public static class DefaultBuilder implements Builder {

		private final DefaultInputGroup instance = new DefaultInputGroup();

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.InputGroup.Builder#with(com.holonplatform.vaadin.components.Input[])
		 */
		@Override
		public Builder with(Input<?>... inputs) {
			if (inputs != null) {
				for (Input<?> input : inputs) {
					instance.add(input);
				}
			}
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.InputGroup.Builder#with(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Builder with(Iterable<Input> inputs) {
			ObjectUtils.argumentNotNull(inputs, "Inputs must be not null");
			inputs.forEach(input -> instance.add(input));
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.InputGroup.Builder#with(com.vaadin.ui.Field[])
		 */
		@Override
		public Builder with(Field<?>... fields) {
			if (fields != null) {
				for (Field<?> field : fields) {
					instance.add(Input.from(field));
				}
			}
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.InputGroup.Builder#withFields(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Builder withFields(Iterable<Field> fields) {
			ObjectUtils.argumentNotNull(fields, "Fields must be not null");
			for (Field<?> field : fields) {
				instance.add(Input.from(field));
			}
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.InputGroup.Builder#stopValidationAtFirstFailure(boolean)
		 */
		@Override
		public Builder stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
			instance.setStopValidationAtFirstFailure(stopValidationAtFirstFailure);
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
		public InputGroup build() {
			return instance;
		}

	}

}
