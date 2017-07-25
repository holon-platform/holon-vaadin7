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

import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.Registration;
import com.holonplatform.vaadin.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

/**
 * A {@link Boolean} type {@link Field} using a {@link CheckBox} as UI widget, supporting {@link ValidatableField}
 * features.
 *
 * @since 5.0.0
 */
public class BooleanField extends CheckBox implements Input<Boolean>, RequiredIndicatorSupport {

	private static final long serialVersionUID = 8256370094165483325L;

	/**
	 * Required indicator
	 */
	private boolean requiredIndicatorOnly = false;

	/**
	 * Treat <code>null</code> values as {@link Boolean#FALSE} values
	 */
	private boolean nullValueAsFalse = false;

	/**
	 * Creates a new <code>BooleanField</code> without caption.
	 */
	public BooleanField() {
		super();
		init();
	}

	/**
	 * Creates a new <code>BooleanField</code> with given caption and initial value.
	 * @param caption Field caption
	 * @param initialState Initial value
	 */
	public BooleanField(String caption, boolean initialState) {
		super(caption, initialState);
		init();
	}

	/**
	 * Creates a new <code>BooleanField</code> that is connected to a boolean property.
	 * @param caption Field caption
	 * @param dataSource the Property to be edited with this editor
	 */
	public BooleanField(String caption, Property<?> dataSource) {
		super(caption, dataSource);
		init();
	}

	/**
	 * Creates a new <code>BooleanField</code> with given caption.
	 * @param caption Field caption
	 */
	public BooleanField(String caption) {
		super(caption);
		init();
	}

	/**
	 * Init field
	 */
	protected void init() {
		addStyleName("h-field");
		addStyleName("h-booleanfield");
	}

	/**
	 * Gets whether to treat <code>null</code> values as {@link Boolean#FALSE} values
	 * @return <code>true</code> to treat <code>null</code> values as {@link Boolean#FALSE} values
	 */
	public boolean isNullValueAsFalse() {
		return nullValueAsFalse;
	}

	/**
	 * Sets whether to treat <code>null</code> values as {@link Boolean#FALSE} values
	 * @param nullValueAsFalse <code>true</code> to treat <code>null</code> values as {@link Boolean#FALSE} values
	 */
	public void setNullValueAsFalse(boolean nullValueAsFalse) {
		this.nullValueAsFalse = nullValueAsFalse;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.CheckBox#setInternalValue(java.lang.Boolean)
	 */
	@Override
	protected void setInternalValue(Boolean newValue) {
		super.setInternalValue(sanitizeValue(newValue));
	}

	/**
	 * Checkup Boolean value to apply null-as-false behaviour, if enabled.
	 * @param value Value to check
	 * @return Sanitized value
	 */
	protected Boolean sanitizeValue(Boolean value) {
		if (value == null && isNullValueAsFalse()) {
			return Boolean.FALSE;
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		super.setRequired(required);
		this.requiredIndicatorOnly = false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.RequiredIndicatorSupport#setRequiredIndicatorVisible(boolean)
	 */
	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		super.setRequired(requiredIndicatorVisible);
		this.requiredIndicatorOnly = requiredIndicatorVisible;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.RequiredIndicatorSupport#isRequiredIndicatorVisible()
	 */
	@Override
	public boolean isRequiredIndicatorVisible() {
		return isRequired() || requiredIndicatorOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#validateValue()
	 */
	@Override
	public void validate() throws InvalidValueException {
		if (requiredIndicatorOnly) {
			super.validate(getValue());
		} else {
			super.validate();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#addValueChangeListener(com.holonplatform.vaadin.components.Input.
	 * ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			com.holonplatform.vaadin.components.Input.ValueChangeListener<Boolean> listener) {
		return ValueChangeNotifierRegistration.adapt(this, this, listener);
	}

	// Builder

	/**
	 * Builder to create {@link BooleanField} instances
	 */
	public static class Builder extends AbstractFieldBuilder<Boolean, Input<Boolean>, BooleanField, BooleanInputBuilder>
			implements BooleanInputBuilder {

		/**
		 * Constructor
		 */
		public Builder() {
			super(new BooleanField());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BooleanFieldBuilder#nullValueAsFalse(boolean)
		 */
		@Override
		public BooleanInputBuilder nullValueAsFalse(boolean nullValueAsFalse) {
			getInstance().setNullValueAsFalse(nullValueAsFalse);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BooleanFieldBuilder#withFocusListener(com.vaadin.event.
		 * FieldEvents.FocusListener)
		 */
		@Override
		public BooleanInputBuilder withFocusListener(FocusListener listener) {
			getInstance().addFocusListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BooleanFieldBuilder#withBlurListener(com.vaadin.event.
		 * FieldEvents.BlurListener)
		 */
		@Override
		public BooleanInputBuilder withBlurListener(BlurListener listener) {
			getInstance().addBlurListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected BooleanInputBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<Boolean> build(BooleanField instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<Boolean> buildAsField(BooleanField instance) {
			return instance;
		}

	}

}
