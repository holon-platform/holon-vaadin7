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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.Registration;
import com.holonplatform.vaadin.components.Input;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Field;

/**
 * Base class to build a custom {@link Field} wrapping another Field, maybe with a different value type, that acts as
 * presenter and inout handler in UI.
 * 
 * <p>
 * Any style name added to this Field using {@link #addStyleName(String)} is reflected to internal field, to ensure
 * visual consistency using common field styles.
 * </p>
 * 
 * @param <T> Field type
 * @param <F> Internal field type
 * 
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractCustomField<T, F extends Field> extends CustomField<T>
		implements Input<T>, RequiredIndicatorSupport {

	private static final long serialVersionUID = -5952052029882768908L;

	/**
	 * Default style class name for invalid fields
	 */
	static final String DEFAULT_INVALID_FIELD_STYLE_CLASS = "error";

	/**
	 * Field type
	 */
	private final Class<? extends T> type;

	/**
	 * Internal Field
	 */
	private F internalField;

	/**
	 * Required indicator
	 */
	private boolean requiredIndicatorOnly = false;

	/**
	 * Constructor
	 * @param type Field concrete type
	 */
	public AbstractCustomField(Class<? extends T> type) {
		this(type, true);
	}

	/**
	 * Constructor
	 * @param type Field concrete type
	 * @param init <code>true</code> to init the internal field. If <code>false</code>, subclasses must ensure the
	 *        {@link #init()} method to called after construction.
	 */
	public AbstractCustomField(Class<? extends T> type, boolean init) {
		super();

		ObjectUtils.argumentNotNull(type, "Field type must be not null");

		this.type = type;

		setWidthUndefined();

		addStyleName("h-field", false);

		if (init) {
			init();
		}

	}

	/**
	 * Initialize and configure the internal wrapped field
	 */
	protected void init() {
		// build internal field
		this.internalField = buildInternalField(getType());

		// use this field as data source for internal field
		if (this.internalField instanceof AbstractComponent) {
			((AbstractComponent) this.internalField).setImmediate(true);
		}
		this.internalField.setBuffered(false);
		this.internalField.setPropertyDataSource(this);
	}

	/**
	 * Build concrete internal Field.
	 * <p>
	 * If internal Field type is not consistent with the custom field type, it is the responsibility of subclasses to
	 * provide a suitable Converter for the internal Field.
	 * </p>
	 * @param type Concrete field type
	 * @return Internal field (must be not null)
	 */
	protected abstract F buildInternalField(Class<? extends T> type);

	/**
	 * Gets the wrapped field
	 * @return the internal field
	 */
	protected F getInternalField() {
		return internalField;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<? extends T> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		final Field<?> content = getInternalField();
		if (getWidth() > -1) {
			content.setWidth(100, Unit.PERCENTAGE);
		}
		if (getHeight() > -1) {
			content.setHeight(100, Unit.PERCENTAGE);
		}
		return content;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#addStyleName(java.lang.String)
	 */
	@Override
	public void addStyleName(String style) {
		super.addStyleName(style);
		// add to internal field too
		getInternalField().addStyleName(style);
	}

	/**
	 * Adds one or more style names to this component.
	 * @param styleName Style name to add
	 * @param reflectToInternalField <code>true</code> to add given <code>styleName</code> to internal field too
	 */
	protected void addStyleName(String styleName, boolean reflectToInternalField) {
		super.addStyleName(styleName);
		if (reflectToInternalField) {
			getInternalField().addStyleName(styleName);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#removeStyleName(java.lang.String)
	 */
	@Override
	public void removeStyleName(String style) {
		super.removeStyleName(style);
		getContent().removeStyleName(style);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		// reflect to internal field
		getInternalField().setReadOnly(readOnly);
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
	 * @see com.vaadin.ui.AbstractComponent#setComponentError(com.vaadin.server.ErrorMessage)
	 */
	@Override
	public void setComponentError(ErrorMessage componentError) {
		super.setComponentError(componentError);
		if (componentError != null) {
			addStyleName(DEFAULT_INVALID_FIELD_STYLE_CLASS);
		} else {
			removeStyleName(DEFAULT_INVALID_FIELD_STYLE_CLASS);
		}
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
			com.holonplatform.vaadin.components.Input.ValueChangeListener<T> listener) {
		return ValueChangeNotifierRegistration.adapt(this, this, listener);
	}

}
