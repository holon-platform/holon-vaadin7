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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.Validatable;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.data.Validator.InvalidValueException;

/**
 * Utility class to handle validation using {@link Validator} and {@link Validatable} and to convert and adapt
 * validation architecture to Vaadin validation model.
 * 
 * @since 5.0.0
 */
public final class ValidationUtils implements Serializable {

	private static final long serialVersionUID = -5381766757295971122L;

	/*
	 * Empty private constructor: this class is intended only to provide constants ad utility methods.
	 */
	private ValidationUtils() {
	}

	/**
	 * Convert a Vaadin {@link com.vaadin.data.Validator} into a {@link Validator}.
	 * @param <T> Value type
	 * @param validator Validator to convert (not null)
	 * @return Converted validator
	 */
	public static <T> Validator<T> asValidator(final com.vaadin.data.Validator validator) {
		return new VaadinValidatorWrapper<>(validator);
	}

	/**
	 * Translate a {@link ValidationException} into a Vaadin {@link InvalidValueException}.
	 * @param exception Exception to translate (not null)
	 * @return Translated InvalidValueException
	 */
	@SuppressWarnings("serial")
	public static InvalidValueException translateValidationException(ValidationException exception) {
		ObjectUtils.argumentNotNull(exception, "ValidationException must not be null");

		final String localizedMessage;
		if (exception.getMessage() != null) {
			localizedMessage = LocalizationContext.translate(exception, true);
		} else {
			localizedMessage = exception.getMessage();
		}
		InvalidValueException[] causes = new InvalidValueException[0];
		if (exception.getCauses() != null) {
			causes = new InvalidValueException[exception.getCauses().length];
			for (int i = 0; i < exception.getCauses().length; i++) {
				causes[i] = translateValidationException(exception.getCauses()[i]);
			}
		}
		return new InvalidValueException(exception.getMessage(), causes) {

			@Override
			public String getLocalizedMessage() {
				return localizedMessage;
			}

		};
	}

	/**
	 * Translate a Vaadin {@link InvalidValueException} into a {@link ValidationException}.
	 * @param exception Exception to translate (not null)
	 * @return Translated ValidationException
	 */
	public static ValidationException translateValidationException(InvalidValueException exception) {
		ObjectUtils.argumentNotNull(exception, "ValidationException must not be null");

		ValidationException ve = new ValidationException(exception.getLocalizedMessage());
		if (exception.getCauses() != null && exception.getCauses().length > 0) {
			List<ValidationException> cs = new LinkedList<>();
			for (InvalidValueException cause : exception.getCauses()) {
				cs.add(translateValidationException(cause));
			}
			ve.setCauses(cs.toArray(new ValidationException[0]));
		}
		return ve;
	}

}
