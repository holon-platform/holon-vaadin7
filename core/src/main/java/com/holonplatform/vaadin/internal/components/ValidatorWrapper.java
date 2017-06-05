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

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * A wrapper class to encapsulate a {@link Validator} into a Vaadin standard {@link com.vaadin.data.Validator}.
 * 
 * @param <T> Validator type
 * 
 * @since 5.0.0
 */
public class ValidatorWrapper<T> implements com.vaadin.data.Validator {

	private static final long serialVersionUID = 1064622406100865519L;

	/**
	 * Wrapped validator
	 */
	private final Validator<T> validator;

	/**
	 * Constructor
	 * @param validator Concrete validator (not null)
	 */
	public ValidatorWrapper(Validator<T> validator) {
		super();
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		this.validator = validator;
	}

	/**
	 * The wrapped {@link Validator}
	 * @return the wrapped validator
	 */
	public Validator<T> getValidator() {
		return validator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Validator#validate(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void validate(Object value) throws InvalidValueException {
		try {
			validator.validate((T) value);
		} catch (ValidationException ve) {
			throw ValidationUtils.translateValidationException(ve);
		} catch (Exception e) {
			throw new InvalidValueException("Invalid value type: " + ExceptionUtils.getRootCauseMessage(e));
		}
	}

}
