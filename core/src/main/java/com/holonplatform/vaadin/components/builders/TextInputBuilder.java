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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.Input;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;

/**
 * Base builder to create {@link Input} instances backed by a text input widget.
 * 
 * @param <T> Value type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface TextInputBuilder<T, C extends Input<T>, B extends TextInputBuilder<T, C, B>>
		extends ValidatableInputBuilder<T, C, B> {

	/**
	 * Set the maximum number of characters in the field
	 * @param maxLength Maximum number of characters in the field, -1 is considered unlimited
	 * @return this
	 */
	B maxLength(int maxLength);

	/**
	 * Sets the number of columns in the editor. If the number of columns is set 0, the actual number of displayed
	 * columns is determined implicitly by the adapter.
	 * @param columns the number of columns to set
	 * @return this
	 */
	B columns(int columns);

	/**
	 * Sets the input prompt - a textual prompt that is displayed when the field would otherwise be empty, to prompt the
	 * user for input.
	 * @param inputPrompt the input prompt to set, <code>null</code> for none
	 * @return this
	 */
	B inputPrompt(String inputPrompt);

	/**
	 * Sets the input prompt - a textual prompt that is displayed when the field would otherwise be empty, to prompt the
	 * user for input - using a localizable <code>messageCode</code>.
	 * <p>
	 * For input prompt localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built or when component is displayed if {@link #deferLocalization()} is
	 * <code>true</code>.
	 * </p>
	 * @param defaultInputPrompt Default message if no translation is available for given <code>messageCode</code> for
	 *        current Locale.
	 * @param messageCode Input prompt translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	B inputPrompt(String defaultInputPrompt, String messageCode, Object... arguments);

	/**
	 * Sets the input prompt - a textual prompt that is displayed when the field would otherwise be empty, to prompt the
	 * user for input - using a {@link Localizable} message.
	 * <p>
	 * For input prompt localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built or when component is displayed if {@link #deferLocalization()} is
	 * <code>true</code>.
	 * </p>
	 * @param inputPrompt Localizable input prompt
	 * @return this
	 */
	B inputPrompt(Localizable inputPrompt);

	/**
	 * Sets the mode how the TextField triggers {@link TextChangeEvent}s.
	 * @param inputEventMode the new mode
	 * @see TextChangeEventMode
	 * @return this
	 */
	B textChangeEventMode(TextChangeEventMode inputEventMode);

	/**
	 * The text change timeout modifies how often text change events are communicated to the application when text
	 * change event mode is {@link TextChangeEventMode#LAZY} or {@link TextChangeEventMode#TIMEOUT}.
	 * @param timeout the timeout in milliseconds
	 * @return this
	 */
	B textChangeTimeout(int timeout);

	/**
	 * Add a listener for text change events
	 * @param listener Listener to add
	 * @return this
	 */
	B withTextChangeListener(TextChangeListener listener);

	/**
	 * Add a listener for focus gained events
	 * @param listener Listener to add
	 * @return this
	 */
	B withFocusListener(FocusListener listener);

	/**
	 * Add a listener for focus lost events
	 * @param listener Listener to add
	 * @return this
	 */
	B withBlurListener(BlurListener listener);

}
