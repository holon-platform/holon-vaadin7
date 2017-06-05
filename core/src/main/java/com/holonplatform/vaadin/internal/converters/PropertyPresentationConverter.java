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

import java.util.Locale;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.vaadin.data.util.converter.Converter;

/**
 * A {@link Converter} which uses {@link Property#present(Object)} method to convert a {@link Property} value to a
 * {@link String} presentation type.
 * <p>
 * Backward to-model conversion is not supported.
 * </p>
 * 
 * @param <T> Property type
 * 
 * @since 5.0.0
 */
public class PropertyPresentationConverter<T> implements Converter<String, T> {

	private static final long serialVersionUID = 7645087071499012088L;

	/**
	 * Property
	 */
	private final Property<T> property;

	/**
	 * Construct a new PropertyPresentationConverter
	 * @param property Property to convert (not null)
	 */
	public PropertyPresentationConverter(Property<T> property) {
		super();

		ObjectUtils.argumentNotNull(property, "Property must be not null");

		this.property = property;
	}

	/**
	 * Gets the Property
	 * @return the property
	 */
	protected Property<T> getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class,
	 * java.util.Locale)
	 */
	@Override
	public String convertToPresentation(T value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return getProperty().present(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getModelType() {
		return (Class<T>) getProperty().getType();
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
		throw new ConversionException("Conversion to model is not supported");
	}

}
