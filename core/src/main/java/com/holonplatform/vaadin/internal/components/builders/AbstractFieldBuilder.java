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
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.FieldBuilder;
import com.holonplatform.vaadin.internal.components.ValidationUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

/**
 * Base {@link FieldBuilder} class
 * 
 * @param <T> Field type
 * @param <C> Internal field type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public abstract class AbstractFieldBuilder<T, C extends Input<T>, I extends AbstractField<T>, B extends FieldBuilder<T, C, B>>
		extends AbstractLocalizableComponentConfigurator<I, B> implements FieldBuilder<T, C, B> {

	/**
	 * Default validation error message for required fields.
	 */
	static final Localizable DEFAULT_REQUIRED_ERROR = Localizable.builder().message("Value is required")
			.messageCode(com.holonplatform.core.Validator.DEFAULT_MESSAGE_CODE_PREFIX + "required").build();

	protected Localizable requiredError;
	protected Localizable conversionError;

	public AbstractFieldBuilder(I instance) {
		super(instance);
	}

	/**
	 * Build concrete instance in expected type
	 * @param instance Building instance
	 * @return Instance in expected type
	 */
	protected abstract C build(I instance);

	/**
	 * Build the concrete instance as a {@link Field}
	 * @param instance Building instance
	 * @return {@link Field} instance
	 */
	protected abstract Field<T> buildAsField(I instance);

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#localize(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected void localize(I instance) {
		super.localize(instance);

		if (requiredError != null) {
			instance.setRequiredError(LocalizationContext.translate(requiredError, true));
		}
		if (conversionError != null) {
			instance.setConversionError(LocalizationContext.translate(conversionError, true));
		}

		// Set default required error if none setted
		if (instance.getRequiredError() == null || instance.getRequiredError().trim().equals("")) {
			instance.setRequiredError(LocalizationContext.translate(DEFAULT_REQUIRED_ERROR, true));
		}
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
		this.requiredError = requiredError;
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
		this.conversionError = conversionError;
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#deferLocalization()
	 */
	@Override
	public B deferLocalization() {
		this.deferLocalization = true;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#build()
	 */
	@Override
	public C build() {
		return build(setupLocalization(instance));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#asField()
	 */
	@Override
	public Field<T> asField() {
		return buildAsField(setupLocalization(instance));
	}

}
