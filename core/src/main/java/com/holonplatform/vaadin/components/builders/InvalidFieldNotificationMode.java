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
package com.holonplatform.vaadin.components.builders;

import com.vaadin.ui.Field;

/**
 * Enumeration for input validation error notification behaviours.
 * 
 * @since 5.0.0
 */
public enum InvalidFieldNotificationMode {

	/**
	 * Never notify validation errors
	 */
	NEVER,

	/**
	 * Always notify validation errors (any value change, including {@link Field#setValue(Object)}, value changes
	 * originated from user input, and explict {@link Field#validate()} actions).
	 */
	ALWAYS,

	/**
	 * Notify validation errors for value changes originated from user input and explict {@link Field#validate()}
	 * actions.
	 */
	USER_INPUT_AND_EXPLICIT_VALIDATION,

	/**
	 * Notify validation errors only for and explict {@link Field#validate()} actions.
	 */
	ONLY_EXPLICIT_VALIDATION;

	/**
	 * Gets the default validation error notification mode
	 * @return Default validation error notification mode
	 */
	public static InvalidFieldNotificationMode defaultMode() {
		return USER_INPUT_AND_EXPLICIT_VALIDATION;
	}

}
