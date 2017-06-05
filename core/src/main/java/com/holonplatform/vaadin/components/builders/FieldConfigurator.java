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

import java.util.Locale;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Field;

/**
 * Interface to configure a {@link Field}.
 * 
 * @param <T> Field type
 * @param <B> Concrete configurator type
 * 
 * @since 5.0.0
 */
public interface FieldConfigurator<T, B extends FieldConfigurator<T, B>> extends ComponentConfigurator<B> {

	/**
	 * Sets the <i>tabulator index</i> of the component. The tab index property is used to specify the order in which
	 * the fields are focused when the user presses the Tab key. Components with a defined tab index are focused
	 * sequentially first, and then the components with no tab index.
	 * <p>
	 * If the tab index is not set (is set to zero), the default tab order is used. The order is somewhat
	 * browser-dependent, but generally follows the HTML structure of the page.
	 * </p>
	 * <p>
	 * A negative value means that the component is completely removed from the tabulation order and can not be reached
	 * by pressing the Tab key at all.
	 * </p>
	 * @param tabIndex The tab order of this component. Indexes usually start from 1. Zero means that default tab order
	 *        should be used. A negative value means that the field should not be included in the tabbing sequence.
	 * @return this
	 */
	B tabIndex(int tabIndex);

	/**
	 * Sets the field required. Required fields must filled by the user.
	 * @return this
	 */
	B required();

	/**
	 * Sets the error message to be displayed if a required field is empty.
	 * @param requiredError Error message
	 * @return this
	 */
	default B requiredError(String requiredError) {
		return requiredError(Localizable.builder().message(requiredError).build());
	}

	/**
	 * Sets the error message to be displayed if a required field is empty, using a localizable
	 * <code>messageCode</code>.
	 * <p>
	 * For error localization, a {@link LocalizationContext} must be available and localized as {@link Context} resource
	 * when component is built.
	 * </p>
	 * @param defaultRequiredError Default error message if no translation is available for given
	 *        <code>messageCode</code> for current Locale.
	 * @param messageCode Required error message translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default B requiredError(String defaultRequiredError, String messageCode, Object... arguments) {
		return requiredError(Localizable.builder().message(defaultRequiredError).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/**
	 * Sets the error message to be displayed if a required field is empty, using a {@link Localizable} message.
	 * <p>
	 * For error localization, a {@link LocalizationContext} must be available and localized as {@link Context} resource
	 * when component is built.
	 * </p>
	 * @param requiredError Required error message
	 * @return this
	 */
	B requiredError(Localizable requiredError);

	/**
	 * Sets an initial value for the field.
	 * @param value The value to set
	 * @return this
	 */
	B withValue(T value);

	/**
	 * Sets the value of the field using a value of the data source type. The value given is converted to the field type
	 * and then assigned to the field. This will update the property data source in the same way as when
	 * {@link Field#setValue(Object)} is called.
	 * @param value The value to set
	 * @return this
	 */
	B withConvertedValue(Object value);

	/**
	 * Sets a converter for the field retrieving it from the converter factory defined for the application.
	 * @param datamodelType The type of the data model that we want to be able to convert from
	 * @return this
	 */
	B converter(Class<?> datamodelType);

	/**
	 * Sets the converter used to convert the field value to property data source type. The converter must have a
	 * presentation type that matches the field type.
	 * @param converter The converter to use
	 * @return this
	 */
	B converter(Converter<T, ?> converter);

	/**
	 * Sets the error that is shown if the field value cannot be converted to the data source type. If {0} is present in
	 * the message, it will be replaced by the simple name of the data source type. If {1} is present in the message, it
	 * will be replaced by the ConversionException message.
	 * @param conversionError Message to be shown when conversion of the value fails
	 * @return this
	 */
	default B conversionError(String conversionError) {
		return conversionError(Localizable.builder().message(conversionError).build());
	}

	/**
	 * Sets the error that is shown if the field value cannot be converted to the data source type, using a localizable
	 * <code>messageCode</code>.
	 * <p>
	 * For description localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param defaultConversionError Default message if no translation is available for given <code>messageCode</code>
	 *        for current Locale.
	 * @param messageCode Conversion error translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default B conversionError(String defaultConversionError, String messageCode, Object... arguments) {
		return conversionError(Localizable.builder().message(defaultConversionError).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/**
	 * Sets the error that is shown if the field value cannot be converted to the data source type, using a
	 * {@link Localizable} message.
	 * <p>
	 * For description localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param conversionError Conversion error message
	 * @return this
	 */
	B conversionError(Localizable conversionError);

	/**
	 * Disallow invalid value to be accepted by th field.
	 * @return this
	 */
	B invalidNotAllowed();

	/**
	 * Allow invalid data to be committed to datasource.
	 * @return this
	 */
	B invalidCommitted();

	/**
	 * Sets the field in buffered mode.
	 * <p>
	 * When in buffered mode, an internal buffer will be used to store changes until {@link Buffered#commit()} is
	 * called. Calling {@link Buffered#discard()} will revert the internal buffer to the value of the data source.
	 * <p>
	 * @return this
	 */
	B buffered();

	/**
	 * Sets the specified Property as the data source for the field. All uncommitted changes are replaced with a value
	 * from the new data source.
	 * <p>
	 * If the datasource has any validators, the same validators are added to the field. Because the default behavior of
	 * the field is to allow invalid values, but not to allow committing them, this only adds visual error messages to
	 * fields and do not allow committing them as long as the value is invalid. After the value is valid, the error
	 * message is not shown and the commit can be done normally.
	 * </p>
	 * @param dataSource The data source Property
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	B dataSource(Property dataSource);

	/**
	 * Adds a value {@link Validator} to the field.
	 * <p>
	 * The validator is activated every time the object's value needs to be verified. This usually happens when the
	 * object's value changes.
	 * </p>
	 * @param validator The Validator to add
	 * @return this
	 */
	B withValidator(Validator validator);

	/**
	 * Adds a value {@link com.holonplatform.core.Validator} to the field.
	 * <p>
	 * The validator is activated every time the object's value needs to be verified. This usually happens when the
	 * object's value changes.
	 * </p>
	 * @param validator The Validator to add
	 * @return this
	 */
	B withValidator(com.holonplatform.core.Validator<T> validator);

	/**
	 * Disable automatic, visible validation. Validation methods still work, but one must show the validation in their
	 * own code.
	 * @return this
	 */
	B hideValidation();

	/**
	 * Add a {@link ValueChangeListener} to the field.
	 * @param listener The ValueChangeListener to add
	 * @return this
	 */
	B withValueChangeListener(ValueChangeListener listener);

	/**
	 * Adds a {@link ReadOnlyStatusChangeListener} to the field
	 * @param listener The ReadOnlyStatusChangeListener to add
	 * @return this
	 */
	B withReadOnlyStatusChangeListener(ReadOnlyStatusChangeListener listener);

	/**
	 * Sets the locale of this component.
	 * @param locale The Locale to set
	 * @return this
	 */
	B locale(Locale locale);

	/**
	 * Base field configurator.
	 * @param <T> Field value type
	 */
	public interface BaseFieldConfigurator<T> extends FieldConfigurator<T, BaseFieldConfigurator<T>> {

	}

}
