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

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Abstract {@link Converter} to convert {@link LocalTime}s into {@link Date}s and back.
 * 
 * @since 5.0.0
 */
public class DateToLocalTimeConverter implements Converter<Date, LocalTime> {

	private static final long serialVersionUID = 3668975750482437599L;

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
	 */
	@Override
	public LocalTime convertToModel(Date value, Class<? extends LocalTime> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			final Calendar calendar = Calendar.getInstance(locale);
			calendar.setTime(value);
			return LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@Override
	public Class<LocalTime> getModelType() {
		return LocalTime.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class,
	 * java.util.Locale)
	 */
	@Override
	public Date convertToPresentation(LocalTime value, Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			final Calendar calendar = Calendar.getInstance(locale);
			calendar.set(Calendar.YEAR, 0);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.HOUR_OF_DAY, value.getHour());
			calendar.set(Calendar.MINUTE, value.getMinute());
			calendar.set(Calendar.SECOND, value.getSecond());
			return calendar.getTime();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getPresentationType()
	 */
	@Override
	public Class<Date> getPresentationType() {
		return Date.class;
	}

}
