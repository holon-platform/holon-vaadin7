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
package com.holonplatform.vaadin.internal.components;

import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.TemporalFieldBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractValidatableFieldBuilder;
import com.holonplatform.vaadin.internal.converters.TimeZonedConverter;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

/**
 * An {@link AbstractCustomField} to manage {@link Temporal} type values, using a {@link DateField} for user input.
 * 
 * @param <T> Concrete temporal type
 * 
 * @since 5.0.0
 */
public abstract class AbstractTemporalField<T extends Temporal> extends AbstractCustomField<T, DateField> {

	private static final long serialVersionUID = -3838080526102530873L;

	/**
	 * Inline mode
	 */
	private final boolean inline;

	/**
	 * Optional date/time range start
	 */
	private T rangeStart;

	/**
	 * Optional date/time range end
	 */
	private T rangeEnd;

	/**
	 * Constructor
	 * @param type Field type
	 * @param inline True to render the field as an inline calendar
	 */
	public AbstractTemporalField(Class<? extends T> type, boolean inline) {
		super(type, false);
		this.inline = inline;

		addStyleName("h-temporalfield", false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected DateField buildInternalField(Class<? extends T> type) {
		DateField field = isInline() ? new InlineDateField() : new DateField();
		configureDateField(field);
		return field;
	}

	/**
	 * Configure internal {@link DateField}
	 * @param field Iternal DateField to configure
	 */
	protected abstract void configureDateField(DateField field);

	/**
	 * Returns whether the internal DateField is rendered as an inline calendar
	 * @return <code>true</code> if the internal DateField is rendered as an inline calendar
	 */
	public boolean isInline() {
		return inline;
	}

	/**
	 * Sets date format to use for internal {@link DateField} component date display.
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		getInternalField().setDateFormat(dateFormat);
	}

	/**
	 * Sets the current error message if the range validation fails.
	 * @param dateOutOfRangeMessage Localizable message which is shown when value (the date) is set outside allowed
	 *        range
	 */
	public void setDateOutOfRangeMessage(String dateOutOfRangeMessage) {
		getInternalField().setDateOutOfRangeMessage(dateOutOfRangeMessage);
	}

	/**
	 * Sets the default error message used if the DateField cannot parse the text input by user to a Date field.
	 * @param parsingErrorMessage Parse error message
	 */
	public void setParseErrorMessage(String parsingErrorMessage) {
		getInternalField().setParseErrorMessage(parsingErrorMessage);
	}

	/**
	 * Specifies whether or not date/time interpretation in component is to be lenient.
	 * @see Calendar#setLenient(boolean)
	 * @param lenient true if the lenient mode is to be turned on; false if it is to be turned off.
	 */
	public void setLenient(boolean lenient) {
		getInternalField().setLenient(lenient);
	}

	/**
	 * Returns whether date/time interpretation is to be lenient.
	 * @see Calendar#setLenient(boolean)
	 * @return true if the interpretation mode of this calendar is lenient; false otherwise.
	 */
	public boolean isLenient() {
		return getInternalField().isLenient();
	}

	/**
	 * Checks whether ISO 8601 week numbers are shown in the date selector.
	 * @return true if week numbers are shown, false otherwise.
	 */
	public boolean isShowISOWeekNumbers() {
		return getInternalField().isShowISOWeekNumbers();
	}

	/**
	 * Sets the visibility of ISO 8601 week numbers in the date selector. ISO 8601 defines that a week always starts
	 * with a Monday so the week numbers are only shown if this is the case.
	 * @param showWeekNumbers true if week numbers should be shown, false otherwise.
	 */
	public void setShowISOWeekNumbers(boolean showWeekNumbers) {
		getInternalField().setShowISOWeekNumbers(showWeekNumbers);
	}

	/**
	 * Sets the time zone used by this date field. The time zone is used to convert the absolute time in a Date object
	 * to a logical time displayed in the selector and to convert the select time back to a Date object. If no time zone
	 * has been set, the current default time zone returned by {@code TimeZone.getDefault()} is used.
	 * @param timeZone the time zone to use for time calculations.
	 */
	@SuppressWarnings("rawtypes")
	public void setTimeZone(TimeZone timeZone) {
		getInternalField().setTimeZone(timeZone);

		// update time zone in converter, if supported
		Converter<Date, Object> converter = getInternalField().getConverter();
		if (converter instanceof TimeZonedConverter && timeZone != null) {
			((TimeZonedConverter) converter).setTimeZone(timeZone);
		}
	}

	/**
	 * Gets the time zone used by this field. The time zone is used to convert the absolute time in a Date object to a
	 * logical time displayed in the selector and to convert the select time back to a Date object. If {@code null} is
	 * returned, the current default time zone returned by {@code TimeZone.getDefault()} is used.
	 * @return the current time zone
	 */
	public TimeZone getTimeZone() {
		return getInternalField().getTimeZone();
	}

	/**
	 * Sets the start range for this component. If the value is set before this date/time, the component will not
	 * validate. If <code>start</code> is set to <code>null</code>, any value before {@link #getRangeEnd()} will be
	 * accepted by the range.
	 * @param start the allowed range's start date/time (inclusive)
	 */
	public void setRangeStart(T start) {
		this.rangeStart = start;
		if (start == null) {
			getInternalField().setRangeStart(null);
		} else {
			Converter<Date, Object> converter = getInternalField().getConverter();
			if (converter == null) {
				throw new IllegalStateException("Missing value converter for internal DateField");
			}
			getInternalField().setRangeStart(converter.convertToPresentation(start, Date.class, getLocale()));
		}
	}

	/**
	 * Sets the end range for this component. If the value is set after this date/time, the component will not validate.
	 * If <code>end</code> is set to <code>null</code>, any value after {@link #getRangeStart()} will be accepted by the
	 * range.
	 *
	 * @param end the allowed range's end date (inclusive)
	 */
	public void setRangeEnd(T end) {
		this.rangeEnd = end;
		if (end == null) {
			getInternalField().setRangeEnd(null);
		} else {
			Converter<Date, Object> converter = getInternalField().getConverter();
			if (converter == null) {
				throw new IllegalStateException("Missing value converter for internal DateField");
			}
			getInternalField().setRangeEnd(converter.convertToPresentation(end, Date.class, getLocale()));
		}
	}

	/**
	 * Gets the start range for this component. If the value is set before this date/time, the component will not
	 * validate.
	 * @return the allowed range's start date/time, or <code>null</code> if not setted
	 */
	public T getRangeStart() {
		return rangeStart;
	}

	/**
	 * Gets the end range for this component. If the value is set after this date/time, the component will not validate.
	 * @return the allowed range's end date/time, or <code>null</code> if not setted
	 */
	public T getRangeEnd() {
		return rangeEnd;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.event.FieldEvents.FocusNotifier#addFocusListener(com.vaadin.event.FieldEvents.FocusListener)
	 */
	public void addFocusListener(FocusListener listener) {
		getInternalField().addFocusListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.event.FieldEvents.FocusNotifier#removeFocusListener(com.vaadin.event.FieldEvents.FocusListener)
	 */
	public void removeFocusListener(FocusListener listener) {
		getInternalField().removeFocusListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.event.FieldEvents.BlurNotifier#addBlurListener(com.vaadin.event.FieldEvents.BlurListener)
	 */
	public void addBlurListener(BlurListener listener) {
		getInternalField().addBlurListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.event.FieldEvents.BlurNotifier#removeBlurListener(com.vaadin.event.FieldEvents.BlurListener)
	 */
	public void removeBlurListener(BlurListener listener) {
		getInternalField().removeBlurListener(listener);
	}

	/**
	 * Base builder
	 */
	public abstract static class AbstractTemporalFieldBuilder<T extends Temporal, C extends AbstractTemporalField<T>, B extends TemporalFieldBuilder<T, B>>
			extends AbstractValidatableFieldBuilder<T, Input<T>, C, B> implements TemporalFieldBuilder<T, B> {

		protected Localizable parseErrorMessage;
		protected Localizable outOfRangeMessage;

		/**
		 * Constructor
		 * @param instance Instance to build
		 */
		public AbstractTemporalFieldBuilder(C instance) {
			super(instance);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#localize(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected void localize(C instance) {
			super.localize(instance);

			if (parseErrorMessage != null) {
				instance.setParseErrorMessage(LocalizationContext.translate(parseErrorMessage, true));
			}
			if (outOfRangeMessage != null) {
				instance.setDateOutOfRangeMessage(LocalizationContext.translate(outOfRangeMessage, true));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#dateFormat(java.lang.String)
		 */
		@Override
		public B dateFormat(String dateFormat) {
			getInstance().setDateFormat(dateFormat);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#parseErrorMessage(java.lang.String)
		 */
		@Override
		public B parseErrorMessage(String parsingErrorMessage) {
			getInstance().setParseErrorMessage(parsingErrorMessage);
			this.parseErrorMessage = null;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#parseErrorMessage(java.lang.String,
		 * java.lang.String, java.lang.Object[])
		 */
		@Override
		public B parseErrorMessage(String defaultParseErrorMessage, String messageCode, Object... arguments) {
			this.parseErrorMessage = Localizable.builder().message(defaultParseErrorMessage).messageCode(messageCode)
					.messageArguments(arguments).build();
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#parseErrorMessage(com.holonplatform.core.
		 * i18n.Localizable)
		 */
		@Override
		public B parseErrorMessage(Localizable parseErrorMessage) {
			this.parseErrorMessage = parseErrorMessage;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#lenient(boolean)
		 */
		@Override
		public B lenient(boolean lenient) {
			getInstance().setLenient(lenient);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#showISOWeekNumbers(boolean)
		 */
		@Override
		public B showISOWeekNumbers(boolean showWeekNumbers) {
			getInstance().setShowISOWeekNumbers(showWeekNumbers);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#timeZone(java.util.TimeZone)
		 */
		@Override
		public B timeZone(TimeZone timeZone) {
			getInstance().setTimeZone(timeZone);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#rangeStart(java.lang.Object)
		 */
		@Override
		public B rangeStart(T start) {
			getInstance().setRangeStart(start);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#rangeEnd(java.lang.Object)
		 */
		@Override
		public B rangeEnd(T end) {
			getInstance().setRangeEnd(end);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#dateOutOfRangeMessage(java.lang.String)
		 */
		@Override
		public B dateOutOfRangeMessage(String dateOutOfRangeMessage) {
			getInstance().setDateOutOfRangeMessage(dateOutOfRangeMessage);
			this.outOfRangeMessage = null;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#dateOutOfRangeMessage(java.lang.String,
		 * java.lang.String, java.lang.Object[])
		 */
		@Override
		public B dateOutOfRangeMessage(String defaultDateOutOfRangeMessage, String messageCode, Object... arguments) {
			this.outOfRangeMessage = Localizable.builder().message(defaultDateOutOfRangeMessage)
					.messageCode(messageCode).messageArguments(arguments).build();
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#dateOutOfRangeMessage(com.holonplatform.
		 * core.i18n.Localizable)
		 */
		@Override
		public B dateOutOfRangeMessage(Localizable dateOutOfRangeMessage) {
			this.outOfRangeMessage = dateOutOfRangeMessage;
			return builder();
		}

	}

}
