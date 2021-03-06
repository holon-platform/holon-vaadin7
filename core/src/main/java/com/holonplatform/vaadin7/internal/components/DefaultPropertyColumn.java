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
package com.holonplatform.vaadin7.internal.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin7.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin7.components.ItemListing.ColumnAlignment;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Resource;
import com.vaadin.ui.Field;
import com.vaadin.ui.renderers.Renderer;

/**
 * Default {@link PropertyColumn} implementation.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 * 
 * @since 5.0.0
 */
public class DefaultPropertyColumn<T, P> implements PropertyColumn<T, P> {

	private static final long serialVersionUID = -4394599534028523259L;

	/**
	 * Caption (header)
	 */
	private Localizable caption;

	/**
	 * Alignment
	 */
	private ColumnAlignment alignment;

	/**
	 * Width in pixels
	 */
	private int width = -1;
	/**
	 * Minimum width in pixels
	 */
	private int minWidth = -1;
	/**
	 * Maximum width in pixels
	 */
	private int maxWidth = -1;

	/**
	 * Expand ratio (Grid)
	 */
	private int gridExpandRatio = -1;
	/**
	 * Expand ratio (Table)
	 */
	private float tableExpandRatio = -1;

	/**
	 * Editable
	 */
	private boolean editable = true;

	/**
	 * Required
	 */
	private boolean required = false;

	private Localizable requiredMessage;

	/**
	 * Editor Field
	 */
	private Field<?> editor;

	/**
	 * Validators
	 */
	private List<Validator> validators = new LinkedList<>();

	/**
	 * Hidden
	 */
	private boolean hidden = false;
	/**
	 * Hidable
	 */
	private boolean hidable = true;

	/**
	 * Hide toggle menu caption
	 */
	private Localizable hidingToggleCaption;

	/**
	 * Resizable
	 */
	private boolean resizable = true;

	/**
	 * Icon
	 */
	private Resource icon;

	/**
	 * Style generator
	 */
	private CellStyleGenerator<T, P> style;

	/**
	 * Converter
	 */
	private Converter<?, ?> converter;
	/**
	 * Renderer
	 */
	private Renderer<?> renderer;

	public DefaultPropertyColumn(P property) {
		super();
		if (property != null) {
			if (Localizable.class.isAssignableFrom(property.getClass())) {
				this.caption = (Localizable) property;
			}
			if ((this.caption == null || this.caption.getMessage() == null)
					&& Path.class.isAssignableFrom(property.getClass())) {
				this.caption = Localizable.builder().message(((Path<?>) property).getName()).build();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.support.PropertyColumn#getCaption()
	 */
	@Override
	public Localizable getCaption() {
		return caption;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.support.PropertyColumn#setCaption(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public void setCaption(Localizable caption) {
		this.caption = caption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ColumnAlignment getAlignment() {
		return alignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAlignment(ColumnAlignment alignment) {
		this.alignment = alignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinWidth() {
		return minWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getGridExpandRatio() {
		return gridExpandRatio;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGridExpandRatio(int gridExpandRatio) {
		this.gridExpandRatio = gridExpandRatio;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getTableExpandRatio() {
		return tableExpandRatio;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTableExpandRatio(float tableExpandRatio) {
		this.tableExpandRatio = tableExpandRatio;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEditable() {
		return editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getRequiredMessage()
	 */
	@Override
	public Localizable getRequiredMessage() {
		return requiredMessage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setRequiredMessage(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public void setRequiredMessage(Localizable message) {
		this.requiredMessage = message;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getEditor()
	 */
	@Override
	public Optional<Field<?>> getEditor() {
		return Optional.ofNullable(editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setEditor(com.vaadin.ui.Field)
	 */
	@Override
	public void setEditor(Field<?> editor) {
		this.editor = editor;
	}

	@Override
	public void addValidator(Validator validator) {
		ObjectUtils.argumentNotNull(validator, "Property validator must be not null");
		this.validators.add(validator);
	}

	@Override
	public List<Validator> getValidators() {
		return validators;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHidable() {
		return hidable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHidable(boolean hidable) {
		this.hidable = hidable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.support.PropertyColumn#getHidingToggleCaption()
	 */
	@Override
	public Localizable getHidingToggleCaption() {
		return hidingToggleCaption;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.support.PropertyColumn#setHidingToggleCaption(com.holonplatform.
	 * core.i18n.Localizable)
	 */
	@Override
	public void setHidingToggleCaption(Localizable hidingToggleCaption) {
		this.hidingToggleCaption = hidingToggleCaption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isResizable() {
		return resizable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Resource getIcon() {
		return icon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIcon(Resource icon) {
		this.icon = icon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellStyleGenerator<T, P> getStyle() {
		return style;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStyle(CellStyleGenerator<T, P> style) {
		this.style = style;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Converter<?, ?> getConverter() {
		return converter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConverter(Converter<?, ?> converter) {
		this.converter = converter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Renderer<?> getRenderer() {
		return renderer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRenderer(Renderer<?> renderer) {
		this.renderer = renderer;
	}

}
