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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.Validatable;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.builders.InvalidFieldNotificationMode;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.ClientConnector;

/**
 * Utility class to handle validation using {@link Validator} and {@link Validatable} and to convert and adapt
 * validation architecture to Vaadin validation model.
 * 
 * @since 5.0.0
 */
public final class ValidationUtils implements Serializable {

	private static final long serialVersionUID = -5381766757295971122L;

	/**
	 * <p>
	 * For internal use.
	 * </p>
	 */
	@FunctionalInterface
	public interface ClientSynchAction {

		/**
		 * Called before the shared state and RPC invocations are sent to the client
		 * @param initial Connector creation phase
		 */
		void beforeClientResponse(boolean initial);

	}

	/*
	 * Empty private constructor: this class is intended only to provide constants ad utility methods.
	 */
	private ValidationUtils() {
	}

	/**
	 * Convert and register all <code>fromValidatable</code> {@link Validator}s into <code>toValidatable</code> Vaadin
	 * {@link com.vaadin.data.Validatable}.
	 * @param <T> Validatable type
	 * @param fromValidatable Source validatable (not null)
	 * @param toValidatable Destination validatable (not null)
	 */
	public static <T> void registerValidators(Validatable<T> fromValidatable,
			com.vaadin.data.Validatable toValidatable) {
		ObjectUtils.argumentNotNull(fromValidatable, "Source Validatable must not be null");
		ObjectUtils.argumentNotNull(toValidatable, "Destination Validatable must not be null");
		for (Validator<T> validator : fromValidatable.getValidators()) {
			toValidatable.addValidator(asVaadinValidator(validator));
		}
	}

	/**
	 * Convert a {@link Validator} into a Vaadin {@link com.vaadin.data.Validator}.
	 * @param <T> Validator type
	 * @param validator Validator to convert (not null)
	 * @return Converted validator
	 */
	public static <T> com.vaadin.data.Validator asVaadinValidator(final Validator<T> validator) {
		return new ValidatorWrapper<>(validator);
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

	/**
	 * Unregister given <code>validator</code> from given Validatable
	 * @param validatable Validators container
	 * @param validator Validator to remove
	 * @return <code>true</code> if validator was found and removed
	 */
	@SuppressWarnings("rawtypes")
	public static boolean removeValidator(com.vaadin.data.Validatable validatable, Validator validator) {
		if (validator != null) {
			Collection<com.vaadin.data.Validator> validators = validatable.getValidators();
			if (validators != null) {
				com.vaadin.data.Validator toRemove = null;
				for (com.vaadin.data.Validator v : validators) {
					if (v instanceof ValidatorWrapper && ((ValidatorWrapper) v).getValidator().equals(validator)) {
						toRemove = v;
						break;
					}
				}
				if (toRemove != null) {
					validatable.removeValidator(toRemove);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Setup ValidatableField when {@link InvalidFieldNotificationMode} changes
	 * @param field Field
	 */
	public static void setupInvalidFieldNotificationMode(final ValidatableField<?> field) {
		if (InvalidFieldNotificationMode.ALWAYS != field.getInvalidFieldNotificationMode()) {
			field.changeValidationVisibility(false);
		} else {
			field.changeValidationVisibility(true);
		}
	}

	/**
	 * {@link ValidatableField} pre-validation operations
	 * @param field Field
	 */
	public static void preValidate(final ValidatableField<?> field) {
		if (!field.isSuspendValidationNotification()
				&& InvalidFieldNotificationMode.NEVER != field.getInvalidFieldNotificationMode()) {
			field.changeValidationVisibility(true);
		}
	}

	/**
	 * {@link ValidatableField} pre value setting operations
	 * @param field Field
	 */
	public static void preValueSet(final ValidatableField<?> field) {
		if (field.getInvalidFieldNotificationMode() != InvalidFieldNotificationMode.ALWAYS) {
			field.changeValidationVisibility(false);
			field.setSuspendValidationNotification(true);
		}
	}

	/**
	 * Operation wrapper of {@link ClientConnector#beforeClientResponse(boolean)} for {@link ValidatableField}s
	 * @param field Field
	 * @param initial Initial state
	 * @param action Concrete action executor
	 */
	public static void beforeClientResponse(final ValidatableField<?> field, final boolean initial,
			ClientSynchAction action) {
		boolean resynch = false;
		if (!initial && (InvalidFieldNotificationMode.ALWAYS == field.getInvalidFieldNotificationMode()
				|| InvalidFieldNotificationMode.USER_INPUT_AND_EXPLICIT_VALIDATION == field
						.getInvalidFieldNotificationMode())
				&& !field.isSuspendValidationNotification()) {
			field.changeValidationVisibility(true);
			resynch = true;
		}
		if (field.isSuspendValidationNotification()) {
			field.setSuspendValidationNotification(false);
		}
		// cocrete action
		action.beforeClientResponse(initial);
		// force repaint to display validation status
		if (resynch) {
			field.markAsDirty();
		}
	}

}
