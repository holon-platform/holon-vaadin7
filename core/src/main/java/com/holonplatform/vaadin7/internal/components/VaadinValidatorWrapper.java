/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin7.internal.components;

import com.holonplatform.core.Validator;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.data.Validator.InvalidValueException;

/**
 * Vaadin {@link com.vaadin.data.Validator} to Holon {@link Validator} wrapper.
 * 
 * @param <T> Value type
 *
 * @since 5.0.0
 */
public class VaadinValidatorWrapper<T> implements Validator<T> {

	private static final long serialVersionUID = 3664165755542688523L;

	private final com.vaadin.data.Validator validator;

	/**
	 * Constructor
	 * @param validator Vaadin validator (not null)
	 */
	public VaadinValidatorWrapper(com.vaadin.data.Validator validator) {
		super();
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		this.validator = validator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(T value) throws com.holonplatform.core.Validator.ValidationException {
		try {
			validator.validate(value);
		} catch (InvalidValueException e) {
			throw ValidationUtils.translateValidationException(e);
		}
	}

}
