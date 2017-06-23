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
package com.holonplatform.vaadin.internal.converters;

import java.text.NumberFormat;
import java.util.Locale;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.vaadin.data.util.converter.Converter;

/**
 * A {@link Converter}s that convert from {@link Number} types to {@link String} and back.
 * <p>
 * The String value is trimmed before conversion. Null or empty String values will be converted into <code>null</code>
 * Number values.
 * </p>
 * <p>
 * The {@link NumberFormat} to use for conversion is retrieved from {@link LocalizationContext}, if available as
 * {@link Context} resource. If a {@link LocalizationContext} is not available, default Number formats for current
 * Locale are used.
 * </p>
 * 
 * @param <T> Number type
 * 
 * @since 5.0.0
 */
public class StringToNumberConverter<T extends Number> implements Converter<String, T> {

	private static final long serialVersionUID = 2952012087662607453L;

	/**
	 * Number type
	 */
	private final Class<? extends T> numberType;

	/**
	 * The number format to use
	 */
	private NumberFormat numberFormat;

	/**
	 * Constructor
	 * @param numberType Number type
	 */
	public StringToNumberConverter(Class<? extends T> numberType) {
		super();
		this.numberType = numberType;
	}

	/**
	 * Sets a fixed NumberFormat to use for value conversions
	 * @param numberFormat the NumberFormat to set
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	/**
	 * Gets the NumberFormat to use to convert values
	 * @param locale Locale to use
	 * @return the numberFormat If a NumberFormat was specified using {@link #setNumberFormat(NumberFormat)}, this one
	 *         is returned. Otherwise, a NumberFormat is obtained using given Locale
	 */
	public NumberFormat getNumberFormat(Locale locale) {
		Locale lcl = (locale != null) ? locale
				: LocalizationContext.getCurrent().filter(l -> l.isLocalized()).flatMap(l -> l.getLocale())
						.orElse(Locale.getDefault());
		return (numberFormat != null) ? numberFormat
				: TypeUtils.isDecimalNumber(numberType) ? NumberFormat.getNumberInstance(lcl)
						: NumberFormat.getIntegerInstance(lcl);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class,
	 * java.util.Locale)
	 */
	@Override
	public String convertToPresentation(T value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			return getNumberFormat(locale).format(value);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getPresentationType()
	 */
	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
	 */
	@Override
	public T convertToModel(String value, Class<? extends T> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null && !value.trim().equals("")) {
			try {
				return ConversionUtils.convertNumberToTargetClass(getNumberFormat(locale).parse(value.trim()),
						numberType);
			} catch (Exception e) {
				throw new ConversionException("Could not convert '" + value + "' to " + getModelType().getName(), e);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getModelType() {
		return (Class<T>) numberType;
	}

}
