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

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.ItemListing.PropertyEditorFactory;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Resource;
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
	 * Editor Field factory
	 */
	private PropertyEditorFactory<P> editorFactory;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyEditorFactory<P> getEditorFactory() {
		return editorFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEditorFactory(PropertyEditorFactory<P> editorFactory) {
		this.editorFactory = editorFactory;
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
