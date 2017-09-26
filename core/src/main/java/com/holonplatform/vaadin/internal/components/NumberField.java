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
package com.holonplatform.vaadin.internal.components;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder;
import com.holonplatform.vaadin.internal.converters.StringToNumberConverter;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

/**
 * A {@link Field} for {@link Number} value type input.
 * 
 * <p>
 * A standard {@link TextField} is used as UI widget for user input, so no client-side character validation is
 * performed.
 * </p>
 * 
 * <p>
 * This field provides common {@link TextField} properties getters and setters, for example {@link #setMaxLength(int)}
 * or {@link #setInputPrompt(String)}.
 * </p>
 * 
 * <p>
 * A {@link StringToNumberConverter} is used for String to numeric value conversions and backward.
 * </p>
 * 
 * @param <T> Number type
 * 
 * @since 5.0.0
 */
public class NumberField<T extends Number> extends AbstractCustomField<T, NumericTextField> {

	private static final long serialVersionUID = -6791184576381210657L;

	/**
	 * Default constructor
	 * @param <N> Number type
	 * @param numberClass Number field type (not null)
	 */
	public <N extends T> NumberField(Class<N> numberClass) {
		super(TypeUtils.box(numberClass));

		addStyleName("h-numberfield", false);
	}

	/**
	 * Constructor with caption
	 * @param <N> Number type
	 * @param numberClass Number field type (not null)
	 * @param caption The field caption
	 */
	public <N extends T> NumberField(Class<N> numberClass, String caption) {
		this(numberClass);
		setCaption(caption);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected NumericTextField buildInternalField(Class<? extends T> type) {
		NumericTextField field = new NumericTextField();
		field.setConverter(new StringToNumberConverter<>(type));
		return field;
	}

	/**
	 * Sets a fixed NumberFormat to use for value conversions from user input field and back.
	 * @param numberFormat the NumberFormat to set
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		StringToNumberConverter<T> converter = new StringToNumberConverter<>(getType());
		converter.setNumberFormat(numberFormat);
		getInternalField().setConverter(converter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		setupLocaleSymbolsInputValidation();
		return super.initContent();
	}

	/**
	 * Setup user input validation allowed symbols according to current Locale and number type
	 */
	protected void setupLocaleSymbolsInputValidation() {

		Locale locale = LocalizationContext.getCurrent().filter(l -> l.isLocalized()).flatMap(l -> l.getLocale())
				.orElse(getLocale());
		if (locale == null) {
			// use default
			locale = Locale.getDefault();
		}

		// check grouping
		boolean useGrouping = true;
		if (getInternalField().getConverter() instanceof StringToNumberConverter) {
			NumberFormat nf = ((StringToNumberConverter<?>) getInternalField().getConverter()).getNumberFormat(locale);
			if (nf != null) {
				useGrouping = nf.isGroupingUsed();
			}
		}

		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);

		char[] symbols = null;
		if (useGrouping) {
			if (TypeUtils.isDecimalNumber(getType())) {
				symbols = new char[] { dfs.getGroupingSeparator(), dfs.getDecimalSeparator() };
			} else {
				symbols = new char[] { dfs.getGroupingSeparator() };
			}
		} else {
			if (TypeUtils.isDecimalNumber(getType())) {
				symbols = new char[] { dfs.getDecimalSeparator() };
			}
		}

		getInternalField().setAllowedSymbols(symbols);
	}

	/**
	 * Gets whether to allow negative numbers input
	 * @return <code>true</code> to allow negative numbers input
	 */
	public boolean isAllowNegative() {
		return getInternalField().isAllowNegative();
	}

	/**
	 * Sets whether to allow negative numbers input
	 * @param allowNegative <code>true</code> to allow negative numbers input
	 */
	public void setAllowNegative(boolean allowNegative) {
		getInternalField().setAllowNegative(allowNegative);
	}

	/**
	 * Gets whether to set html5 input type property as "number"
	 * @return <code>true</code> to set html5 input type property as "number"
	 */
	public boolean isHtml5NumberInputType() {
		return getInternalField().isHtml5NumberInputType();
	}

	/**
	 * Sets whether to set html5 input type property as "number"
	 * @param html5NumberInputType <code>true</code> to set html5 input type property as "number"
	 */
	public void setHtml5NumberInputType(boolean html5NumberInputType) {
		getInternalField().setHtml5NumberInputType(html5NumberInputType);
	}

	/**
	 * Sets the null-string representation.
	 * <p>
	 * The null-valued strings are represented on the user interface by replacing the null value with this string. If
	 * the null representation is set null (not 'null' string), painting null value throws exception.
	 * </p>
	 * <p>
	 * The default value is string 'null'
	 * </p>
	 * @param nullRepresentation Textual representation for null strings
	 */
	public void setNullRepresentation(String nullRepresentation) {
		getInternalField().setNullRepresentation(nullRepresentation);
	}

	/**
	 * Gets the null-string representation.
	 * <p>
	 * The null-valued strings are represented on the user interface by replacing the null value with this string. If
	 * the null representation is set null (not 'null' string), painting null value throws exception.
	 * </p>
	 * <p>
	 * The default value is string 'null'.
	 * </p>
	 * @return the String Textual representation for null strings
	 */
	public String getNullRepresentation() {
		return getInternalField().getNullRepresentation();
	}

	/**
	 * Is setting nulls with null-string representation allowed.
	 * <p>
	 * If this property is true, writing null-representation string to text field always sets the field value to real
	 * null. If this property is false, null setting is not made, but the null values are maintained. Maintenance of
	 * null-values is made by only converting the textfield contents to real null, if the text field matches the
	 * null-string representation and the current value of the field is null.
	 * </p>
	 * <p>
	 * By default this setting is false
	 * </p>
	 * @return boolean Should the null-string represenation be always converted to null-values
	 * @see #getNullRepresentation()
	 */
	public boolean isNullSettingAllowed() {
		return getInternalField().isNullSettingAllowed();
	}

	/**
	 * Sets the null conversion mode.
	 * <p>
	 * If this property is true, writing null-representation string to text field always sets the field value to real
	 * null. If this property is false, null setting is not made, but the null values are maintained. Maintenance of
	 * null-values is made by only converting the textfield contents to real null, if the text field matches the
	 * null-string representation and the current value of the field is null.
	 * </p>
	 * <p>
	 * By default this setting is false.
	 * </p>
	 * @param nullSettingAllowed Should the null-string representation always be converted to null-values
	 * @see #getNullRepresentation()
	 */
	public void setNullSettingAllowed(boolean nullSettingAllowed) {
		getInternalField().setNullSettingAllowed(nullSettingAllowed);
	}

	/**
	 * Returns the maximum number of characters in the field. Value -1 is considered unlimited.
	 * @return the maxLength Maximum number of characters in the field
	 */
	public int getMaxLength() {
		return getInternalField().getMaxLength();
	}

	/**
	 * Set the maximum number of characters in the field
	 * @param maxLength Maximum number of characters in the field, -1 is considered unlimited
	 */
	public void setMaxLength(int maxLength) {
		getInternalField().setMaxLength(maxLength);
	}

	/**
	 * Gets the number of columns in the editor. If the number of columns is set 0, the actual number of displayed
	 * columns is determined implicitly by the adapter.
	 * @return the number of columns in the editor
	 */
	public int getColumns() {
		return getInternalField().getColumns();
	}

	/**
	 * Sets the number of columns in the editor. If the number of columns is set 0, the actual number of displayed
	 * columns is determined implicitly by the adapter.
	 * @param columns the number of columns to set
	 */
	public void setColumns(int columns) {
		getInternalField().setColumns(columns);
	}

	/**
	 * Gets the current input prompt
	 * @return the current input prompt, or <code>null</code> if not enabled
	 */
	public String getInputPrompt() {
		return getInternalField().getInputPrompt();
	}

	/**
	 * Sets the input prompt - a textual prompt that is displayed when the field would otherwise be empty, to prompt the
	 * user for input.
	 * @param inputPrompt the input prompt to set, <code>null</code> for none
	 */
	public void setInputPrompt(String inputPrompt) {
		getInternalField().setInputPrompt(inputPrompt);
	}

	/**
	 * Sets the mode how the TextField triggers {@link TextChangeEvent}s.
	 * @param inputEventMode the new mode
	 * @see TextChangeEventMode
	 */
	public void setTextChangeEventMode(TextChangeEventMode inputEventMode) {
		getInternalField().setTextChangeEventMode(inputEventMode);
	}

	/**
	 * Gets the current {@link TextChangeEventMode}
	 * @return the mode used to trigger {@link TextChangeEvent}s.
	 */
	public TextChangeEventMode getTextChangeEventMode() {
		return getInternalField().getTextChangeEventMode();
	}

	/**
	 * The text change timeout modifies how often text change events are communicated to the application when
	 * {@link #getTextChangeEventMode()} is {@link TextChangeEventMode#LAZY} or {@link TextChangeEventMode#TIMEOUT}.
	 * @param timeout the timeout in milliseconds
	 */
	public void setTextChangeTimeout(int timeout) {
		getInternalField().setTextChangeTimeout(timeout);
	}

	/**
	 * Gets the timeout used to fire {@link TextChangeEvent}s when the {@link #getTextChangeEventMode()} is
	 * {@link TextChangeEventMode#LAZY} or {@link TextChangeEventMode#TIMEOUT}.
	 * @return the timeout value in milliseconds
	 */
	public int getTextChangeTimeout() {
		return getInternalField().getTextChangeTimeout();
	}

	/**
	 * Add a listener for text change events
	 * @param listener Listener to add
	 */
	public void addTextChangeListener(TextChangeListener listener) {
		getInternalField().addTextChangeListener(listener);
	}

	/**
	 * Removes a listener for text change events
	 * @param listener Listener to remove
	 */
	public void removeTextChangeListener(TextChangeListener listener) {
		getInternalField().removeTextChangeListener(listener);
	}

	/**
	 * Sets the cursor position in the field. As a side effect the field will become focused.
	 * @param pos the position for the cursor
	 */
	public void setCursorPosition(int pos) {
		getInternalField().setCursorPosition(pos);
	}

	/**
	 * Returns the last known cursor position of the field.
	 * @return the cursor position
	 */
	public int getCursorPosition() {
		return getInternalField().getCursorPosition();
	}

	/**
	 * Sets the range of text to be selected. As a side effect the field will become focused.
	 * @param pos the position of the first character to be selected
	 * @param length the number of characters to be selected
	 */
	public void setSelectionRange(int pos, int length) {
		getInternalField().setSelectionRange(pos, length);
	}

	/**
	 * Selects all text in the field.
	 */
	public void selectAll() {
		getInternalField().selectAll();
	}

	/**
	 * Adds a listener for focus gained event
	 * @param listener Listener to add
	 */
	public void addFocusListener(FocusListener listener) {
		getInternalField().addFocusListener(listener);
	}

	/**
	 * Removes a listener for focus gained event
	 * @param listener Listener to remove
	 */
	public void removeFocusListener(FocusListener listener) {
		getInternalField().removeFocusListener(listener);
	}

	/**
	 * Adds a listener for focus lost event
	 * @param listener Listener to add
	 */
	public void addBlurListener(BlurListener listener) {
		getInternalField().addBlurListener(listener);
	}

	/**
	 * Removes a listener for focus lost event
	 * @param listener Listener to remove
	 */
	public void removeBlurListener(BlurListener listener) {
		getInternalField().removeBlurListener(listener);
	}

	// Builder

	public static class Builder<T extends Number> extends
			AbstractFieldBuilder<T, Input<T>, NumberField<T>, NumberInputBuilder<T>> implements NumberInputBuilder<T> {

		protected Localizable inputPrompt;

		public <N extends T> Builder(Class<N> numberClass) {
			super(new NumberField<>(numberClass));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#maxLength(int)
		 */
		@Override
		public NumberInputBuilder<T> maxLength(int maxLength) {
			getInstance().setMaxLength(maxLength);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(java.lang.String)
		 */
		@Override
		public NumberInputBuilder<T> inputPrompt(String inputPrompt) {
			getInstance().setInputPrompt(inputPrompt);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(java.lang.String,
		 * java.lang.String, java.lang.Object[])
		 */
		@Override
		public NumberInputBuilder<T> inputPrompt(String defaultInputPrompt, String messageCode, Object... arguments) {
			this.inputPrompt = Localizable.builder().message(defaultInputPrompt).messageCode(messageCode)
					.messageArguments(arguments).build();
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public NumberInputBuilder<T> inputPrompt(Localizable inputPrompt) {
			this.inputPrompt = inputPrompt;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#textChangeEventMode(com.vaadin.ui.
		 * AbstractTextField.TextChangeEventMode)
		 */
		@Override
		public NumberInputBuilder<T> textChangeEventMode(TextChangeEventMode inputEventMode) {
			getInstance().setTextChangeEventMode(inputEventMode);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#textChangeTimeout(int)
		 */
		@Override
		public NumberInputBuilder<T> textChangeTimeout(int timeout) {
			getInstance().setTextChangeTimeout(timeout);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.NumberFieldBuilder#numberFormat(java.text.NumberFormat)
		 */
		@Override
		public NumberInputBuilder<T> numberFormat(NumberFormat numberFormat) {
			getInstance().setNumberFormat(numberFormat);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.NumberFieldBuilder#allowNegative(boolean)
		 */
		@Override
		public NumberInputBuilder<T> allowNegative(boolean allowNegative) {
			getInstance().setAllowNegative(allowNegative);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.NumberFieldBuilder#html5NumberInputType(boolean)
		 */
		@Override
		public NumberInputBuilder<T> html5NumberInputType(boolean html5NumberInputType) {
			getInstance().setHtml5NumberInputType(html5NumberInputType);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withTextChangeListener(com.vaadin.event.
		 * FieldEvents.TextChangeListener)
		 */
		@Override
		public NumberInputBuilder<T> withTextChangeListener(TextChangeListener listener) {
			getInstance().addTextChangeListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withFocusListener(com.vaadin.event.
		 * FieldEvents.FocusListener)
		 */
		@Override
		public NumberInputBuilder<T> withFocusListener(FocusListener listener) {
			getInstance().addFocusListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withBlurListener(com.vaadin.event.
		 * FieldEvents.BlurListener)
		 */
		@Override
		public NumberInputBuilder<T> withBlurListener(BlurListener listener) {
			getInstance().addBlurListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected NumberInputBuilder<T> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#localize(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected void localize(NumberField<T> instance) {
			super.localize(instance);

			if (inputPrompt != null) {
				instance.setInputPrompt(LocalizationContext.translate(inputPrompt, true));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<T> build(NumberField<T> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<T> buildAsField(NumberField<T> instance) {
			return instance;
		}

	}

}
