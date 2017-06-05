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

import com.vaadin.ui.Field;

/**
 * Builder to create {@link String} type {@link Field} instances backed by a text input widget.
 * 
 * @since 5.0.0
 */
public interface StringFieldBuilder extends TextInputFieldBuilder<String, Field<String>, StringFieldBuilder> {

	/**
	 * If field is rendered as a text area in UI, set the number of rows of the text area
	 * @param rows the number of rows of the text area
	 * @return this
	 */
	StringFieldBuilder rows(int rows);

	/**
	 * Sets the null-string representation.
	 * <p>
	 * The null-valued strings are represented on the user interface by replacing the null value with this string. If
	 * the null representation is set null (not 'null' string), painting null value throws exception.
	 * </p>
	 * <p>
	 * The default value is string 'null'
	 * </p>
	 * @param nullRepresentation Textual representation for null strings
	 * @return this
	 */
	StringFieldBuilder nullRepresentation(String nullRepresentation);

	/**
	 * Sets the null conversion mode.
	 * <p>
	 * If this property is true, writing null-representation string to text field always sets the field value to real
	 * null. If this property is false, null setting is not made, but the null values are maintained. Maintenance of
	 * null-values is made by only converting the textfield contents to real null, if the text field matches the
	 * null-string representation and the current value of the field is null.
	 * </p>
	 * <p>
	 * By default this setting is false.
	 * </p>
	 * @param nullSettingAllowed Should the null-string representation always be converted to null-values
	 * @return this
	 */
	StringFieldBuilder nullSettingAllowed(boolean nullSettingAllowed);

	/**
	 * Enable or disable treating empty String values as <code>null</code> values.
	 * <p>
	 * By default this behaviour is enabled.
	 * </p>
	 * @param enable True to treat empty String values as <code>null</code> values
	 * @return this
	 */
	StringFieldBuilder emptyValuesAsNull(boolean enable);

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
	StringFieldBuilder blankValuesAsNull(boolean enable);

}
