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

import com.holonplatform.vaadin.components.Input;
import com.vaadin.ui.Field;

/**
 * Builder to create {@link String} type {@link Field} instances with hidden input, used to enter secret text
 * information like passwords.
 * 
 * @since 5.0.0
 */
public interface SecretFieldBuilder extends ValidatableFieldBuilder<String, Input<String>, SecretFieldBuilder> {

	/**
	 * Enable or disable treating empty String values as <code>null</code> values.
	 * <p>
	 * By default this behaviour is enabled.
	 * </p>
	 * @param enable True to treat empty String values as <code>null</code> values
	 * @return this
	 */
	SecretFieldBuilder emptyValuesAsNull(boolean enable);

	/**
	 * Enable or disable treating blank String values (with 0 length or whitespaces only Strings) as <code>null</code>
	 * values.
	 * <p>
	 * By default this behaviour is disabled.
	 * </p>
	 * @param enable True to treat blank String values (with 0 length or whitespaces only Strings) as <code>null</code>
	 *        values
	 * @return this
	 */
	SecretFieldBuilder blankValuesAsNull(boolean enable);

}
