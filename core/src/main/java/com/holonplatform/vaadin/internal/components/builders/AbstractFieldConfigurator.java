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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.Locale;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.builders.FieldConfigurator;
import com.holonplatform.vaadin.internal.components.ValidationUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractField;

/**
 * Base {@link FieldConfigurator} implementation.
 * 
 * @param <T> Field type
 * @param <C> Internal field type
 * @param <B> Concrete configurator type
 * 
 * @since 5.0.0
 */
public abstract class AbstractFieldConfigurator<T, C extends AbstractField<T>, B extends FieldConfigurator<T, B>>
		extends AbstractComponentConfigurator<C, B> implements FieldConfigurator<T, B> {

	/**
	 * Constructor
	 * @param instance Instance to configure (not null)
	 */
	public AbstractFieldConfigurator(C instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#tabIndex(int)
	 */
	@Override
	public B tabIndex(int tabIndex) {
		getInstance().setTabIndex(tabIndex);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#required()
	 */
	@Override
	public B required() {
		getInstance().setRequired(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#requiredError(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public B requiredError(Localizable requiredError) {
		getInstance().setRequiredError(localizeMessage(requiredError));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#withValue(java.lang.Object)
	 */
	@Override
	public B withValue(T value) {
		getInstance().setValue(value);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#withConvertedValue(java.lang.Object)
	 */
	@Override
	public B withConvertedValue(Object value) {
		getInstance().setConvertedValue(value);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#converter(java.lang.Class)
	 */
	@Override
	public B converter(Class<?> datamodelType) {
		getInstance().setConverter(datamodelType);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.FieldBuilder#converter(com.vaadin.data.util.converter.Converter)
	 */
	@Override
	public B converter(Converter<T, ?> converter) {
		getInstance().setConverter(converter);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#conversionError(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public B conversionError(Localizable conversionError) {
		getInstance().setConversionError(localizeMessage(conversionError));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#invalidNotAllowed()
	 */
	@Override
	public B invalidNotAllowed() {
		getInstance().setInvalidAllowed(false);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#invalidCommitted()
	 */
	@Override
	public B invalidCommitted() {
		getInstance().setInvalidCommitted(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#buffered()
	 */
	@Override
	public B buffered() {
		getInstance().setBuffered(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#dataSource(com.vaadin.data.Property)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public B dataSource(Property dataSource) {
		getInstance().setPropertyDataSource(dataSource);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#withValidator(com.vaadin.data.Validator)
	 */
	@Override
	public B withValidator(Validator validator) {
		getInstance().addValidator(validator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#withValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public B withValidator(com.holonplatform.core.Validator<T> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getInstance().addValidator(ValidationUtils.asVaadinValidator(validator));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#hideValidation()
	 */
	@Override
	public B hideValidation() {
		getInstance().setValidationVisible(false);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#withValueChangeListener(com.vaadin.data.Property.
	 * ValueChangeListener)
	 */
	@Override
	public B withValueChangeListener(ValueChangeListener listener) {
		getInstance().addValueChangeListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#withReadOnlyStatusChangeListener(com.vaadin.data.
	 * Property.ReadOnlyStatusChangeListener)
	 */
	@Override
	public B withReadOnlyStatusChangeListener(ReadOnlyStatusChangeListener listener) {
		getInstance().addReadOnlyStatusChangeListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#locale(java.util.Locale)
	 */
	@Override
	public B locale(Locale locale) {
		getInstance().setLocale(locale);
		return builder();
	}

}
