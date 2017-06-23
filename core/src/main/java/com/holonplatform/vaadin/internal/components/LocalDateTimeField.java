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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import com.holonplatform.vaadin.components.builders.TemporalFieldBuilder.TemporalWithTimeFieldBuilder;
import com.holonplatform.vaadin.internal.converters.DateToLocalDateTimeConverter;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.InlineDateField;

/**
 * Temporal Field implementation handling {@link LocalDateTime} value types.
 * 
 * <p>
 * This field is rendered using a {@link DateField} or an {@link InlineDateField} with MINUTE or SECOND resolution.
 * </p>
 * 
 * @since 5.0.0
 */
public class LocalDateTimeField extends AbstractTemporalField<LocalDateTime> {

	private static final long serialVersionUID = -6110526230713711558L;

	/**
	 * Constructor
	 * @param inline <code>true</code> to render the field using and inline calendar input
	 */
	public LocalDateTimeField(boolean inline) {
		this(inline, null);
	}

	/**
	 * Constructor
	 * @param inline <code>true</code> to render the field using and inline calendar input
	 * @param caption Field caption
	 */
	public LocalDateTimeField(boolean inline, String caption) {
		super(LocalDateTime.class, inline);
		setCaption(caption);
		// init
		init();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.AbstractTemporalField#configureDateField(com.vaadin.ui.DateField)
	 */
	@Override
	protected void configureDateField(DateField field) {
		field.setResolution(Resolution.MINUTE);
		field.setConverter(new DateToLocalDateTimeConverter());
	}

	/**
	 * Set whether to show time seconds
	 * @param showSeconds showSeconds <code>true</code> to handle time seconds, false otherwise (handle only hours and
	 *        minutes)
	 */
	public void setShowSeconds(boolean showSeconds) {
		getInternalField().setResolution(showSeconds ? Resolution.SECOND : Resolution.MINUTE);
	}

	// Builder

	public static class Builder extends
			AbstractTemporalFieldBuilder<LocalDateTime, LocalDateTimeField, TemporalWithTimeFieldBuilder<LocalDateTime>>
			implements TemporalWithTimeFieldBuilder<LocalDateTime> {

		/**
		 * Constructor
		 * @param inline True to render the field as an inline calendar
		 */
		public Builder(boolean inline) {
			super(new LocalDateTimeField(inline));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TemporalFieldBuilder#timeZone(java.time.ZoneId)
		 */
		@Override
		public TemporalWithTimeFieldBuilder<LocalDateTime> timeZone(ZoneId zoneId) {
			getInstance().setTimeZone((zoneId == null) ? null : TimeZone.getTimeZone(zoneId));
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.TemporalFieldBuilder.TemporalWithTimeFieldBuilder#showSeconds()
		 */
		@Override
		public TemporalWithTimeFieldBuilder<LocalDateTime> showSeconds() {
			getInstance().setShowSeconds(true);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected TemporalWithTimeFieldBuilder<LocalDateTime> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
		 * AbstractComponent)
		 */
		@Override
		protected Field<LocalDateTime> build(LocalDateTimeField instance) {
			return instance;
		}

	}

}
