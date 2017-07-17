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
import com.holonplatform.vaadin.components.builders.InvalidFieldNotificationMode;
import com.vaadin.ui.Field;

/**
 * {@link Field} supporting direct holon {@link Validator}s registration and {@link InvalidFieldNotificationMode}
 * behaviour.
 * 
 * @param <T> Fiel value type
 *
 * @since 5.0.0
 */
public interface ValidatableField<T> extends Field<T> {

	/**
	 * Adds a Field value {@link Validator}
	 * @param validator The validator to add
	 */
	void addValidator(Validator<T> validator);

	/**
	 * Removes a Field value {@link Validator}
	 * @param validator The validator to remove
	 */
	void removeValidator(Validator<T> validator);

	/**
	 * Gets current invalid Field error notification mode
	 * @return the invalid Field error notification mode
	 */
	InvalidFieldNotificationMode getInvalidFieldNotificationMode();

	/**
	 * Sets the invalid Field error notification mode
	 * @param invalidFieldNotificationMode the InvalidFieldNotificationMode to set
	 */
	void setInvalidFieldNotificationMode(InvalidFieldNotificationMode invalidFieldNotificationMode);

	/**
	 * Change validation visibility state
	 * @param visible <code>true</code> to enable validation visibility
	 */
	void changeValidationVisibility(boolean visible);

	/**
	 * Check if invalid field notification is temporary suspended
	 * @return <code>true</code> if invalid field notification is temporary suspended
	 */
	boolean isSuspendValidationNotification();

	/**
	 * Sets whether invalid field notification is temporary suspended or not
	 * @param suspendValidationNotification <code>true</code> to suspend invalid field notification
	 */
	void setSuspendValidationNotification(boolean suspendValidationNotification);

}
