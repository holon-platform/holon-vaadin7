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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.ValidatableField;
import com.holonplatform.vaadin.components.builders.StringFieldBuilder;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

/**
 * Builder to create {@link String} type {@link Field}s.
 * 
 * @param <I> Internal field type
 * 
 * @since 5.0.0
 */
public abstract class AbstractStringFieldBuilder<I extends AbstractTextField & ValidatableField<String>>
		extends AbstractValidatableFieldBuilder<String, Field<String>, I, StringFieldBuilder> implements StringFieldBuilder {

	protected Localizable inputPrompt;

	public AbstractStringFieldBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#maxLength(int)
	 */
	@Override
	public StringFieldBuilder maxLength(int maxLength) {
		getInstance().setMaxLength(maxLength);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#columns(int)
	 */
	@Override
	public StringFieldBuilder columns(int columns) {
		getInstance().setColumns(columns);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(java.lang.String)
	 */
	@Override
	public StringFieldBuilder inputPrompt(String inputPrompt) {
		getInstance().setInputPrompt(inputPrompt);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(java.lang.String,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public StringFieldBuilder inputPrompt(String defaultInputPrompt, String messageCode, Object... arguments) {
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
	public StringFieldBuilder inputPrompt(Localizable inputPrompt) {
		this.inputPrompt = inputPrompt;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#textChangeEventMode(com.vaadin.ui.
	 * AbstractTextField.TextChangeEventMode)
	 */
	@Override
	public StringFieldBuilder textChangeEventMode(TextChangeEventMode inputEventMode) {
		getInstance().setTextChangeEventMode(inputEventMode);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#textChangeTimeout(int)
	 */
	@Override
	public StringFieldBuilder textChangeTimeout(int timeout) {
		getInstance().setTextChangeTimeout(timeout);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withTextChangeListener(com.vaadin.event.
	 * FieldEvents.TextChangeListener)
	 */
	@Override
	public StringFieldBuilder withTextChangeListener(TextChangeListener listener) {
		getInstance().addTextChangeListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withFocusListener(com.vaadin.event.
	 * FieldEvents.FocusListener)
	 */
	@Override
	public StringFieldBuilder withFocusListener(FocusListener listener) {
		getInstance().addFocusListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withBlurListener(com.vaadin.event.FieldEvents
	 * .BlurListener)
	 */
	@Override
	public StringFieldBuilder withBlurListener(BlurListener listener) {
		getInstance().addBlurListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextFieldBuilder#rows(int)
	 */
	@Override
	public StringFieldBuilder rows(int rows) {
		if (getInstance() instanceof TextArea) {
			((TextArea) getInstance()).setRows(rows);
		}
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextFieldBuilder#nullRepresentation(java.lang.String)
	 */
	@Override
	public StringFieldBuilder nullRepresentation(String nullRepresentation) {
		getInstance().setNullRepresentation(nullRepresentation);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextFieldBuilder#nullSettingAllowed(boolean)
	 */
	@Override
	public StringFieldBuilder nullSettingAllowed(boolean nullSettingAllowed) {
		getInstance().setNullSettingAllowed(nullSettingAllowed);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#localize(com.vaadin.ui.AbstractField)
	 */
	@Override
	protected void localize(I instance) {
		super.localize(instance);

		if (inputPrompt != null) {
			instance.setInputPrompt(LocalizationContext.translate(inputPrompt, true));
		}
	}

}