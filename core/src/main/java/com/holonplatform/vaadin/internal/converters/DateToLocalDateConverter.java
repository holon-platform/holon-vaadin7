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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * {@link Converter} to convert {@link LocalDate}s into {@link Date}s and back.
 * 
 * @since 5.0.0
 */
public class DateToLocalDateConverter implements TimeZonedConverter<Date, LocalDate> {

	private static final long serialVersionUID = -8306255810004887029L;

	/*
	 * Time zone to use for conversion
	 */
	private ZoneId timeZone;

	/**
	 * Construct a converter using default Time Zone
	 * @see ZoneId#systemDefault()
	 */
	public DateToLocalDateConverter() {
		this(ZoneId.systemDefault());
	}

	/**
	 * Construct a converter using given Time Zone
	 * @param timeZone Time zone to use for conversion
	 */
	public DateToLocalDateConverter(ZoneId timeZone) {
		super();
		this.timeZone = (timeZone != null) ? timeZone : ZoneId.systemDefault();
	}

	/**
	 * Gets the Time zone to use for conversion
	 * @return the timeZone Time zone to use for conversion
	 */
	@Override
	public ZoneId getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets the Time zone to use for conversion
	 * @param timeZone the Time zone to set
	 */
	@Override
	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
	 */
	@Override
	public LocalDate convertToModel(Date value, Class<? extends LocalDate> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			return Instant.ofEpochMilli(value.getTime()).atZone(getTimeZone()).toLocalDate();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@Override
	public Class<LocalDate> getModelType() {
		return LocalDate.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class,
	 * java.util.Locale)
	 */
	@Override
	public Date convertToPresentation(LocalDate value, Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			return Date.from(value.atStartOfDay(getTimeZone()).toInstant());
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