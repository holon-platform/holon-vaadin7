/*
 * Copyright 2016-2017 Axioma srl.
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

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontIcon;
import com.vaadin.ui.Grid;

/**
 * A {@link Grid} converter to present {@link FontIcon} resources as HTML values, using {@link FontIcon#getHtml()}.
 * 
 * <p>
 * Backward to-model conversion is not supported.
 * </p>
 * 
 * @since 5.0.0
 */
public class FontIconPresentationConverter implements Converter<String, FontIcon> {

	private static final long serialVersionUID = 1967667430051792945L;

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class,
	 * java.util.Locale)
	 */
	@Override
	public String convertToPresentation(FontIcon value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			return value.getHtml();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@Override
	public Class<FontIcon> getModelType() {
		return FontIcon.class;
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
	public FontIcon convertToModel(String value, Class<? extends FontIcon> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return null;
	}

}
