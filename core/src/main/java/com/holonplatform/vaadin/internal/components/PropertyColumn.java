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

import java.io.Serializable;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.ItemListing.PropertyEditorFactory;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Resource;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Table;
import com.vaadin.ui.renderers.Renderer;

/**
 * Interface to collect and provide {@link ItemListing} column properties and settings.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 * 
 * @since 5.0.0
 */
public interface PropertyColumn<T, P> extends Serializable {

	/**
	 * Gets column caption (header)
	 * @return Column caption (header)
	 */
	Localizable getCaption();

	/**
	 * Sets column caption (header)
	 * @param caption Column caption (header)
	 */
	void setCaption(Localizable caption);

	/**
	 * Gets column content alignment mode
	 * @return Column content alignment mode
	 */
	ColumnAlignment getAlignment();

	/**
	 * Sets column content alignment mode
	 * @param alignment Column content alignment mode
	 */
	void setAlignment(ColumnAlignment alignment);

	/**
	 * Gets column width in pixels
	 * @return Column width in pixels
	 */
	int getWidth();

	/**
	 * Sets column width in pixels
	 * @param width Column width in pixels
	 */
	void setWidth(int width);

	/**
	 * Gets minimum column width in pixels
	 * @return Minimum column width in pixels
	 */
	int getMinWidth();

	/**
	 * Sets minimum column width in pixels
	 * @param minWidth Minimum column width in pixels
	 */
	void setMinWidth(int minWidth);

	/**
	 * Gets maximum column width in pixels
	 * @return Maximum column width in pixels
	 */
	int getMaxWidth();

	/**
	 * Sets maximum column width in pixels
	 * @param maxWidth Maximum column width in pixels
	 */
	void setMaxWidth(int maxWidth);

	/**
	 * Gets grid column expand ratio
	 * @return Grid column expand ratio
	 * @see Grid.Column#setExpandRatio(int)
	 */
	int getGridExpandRatio();

	/**
	 * Sets grid column expand ratio
	 * @param gridExpandRatio Grid column expand ratio
	 * @see Grid.Column#setExpandRatio(int)
	 */
	void setGridExpandRatio(int gridExpandRatio);

	/**
	 * Gets table column expand ratio
	 * @return Table column expand ratio
	 * @see Table#setColumnExpandRatio(Object, float)
	 */
	float getTableExpandRatio();

	/**
	 * Sets table column expand ratio
	 * @param tableExpandRatio Column expand ratio
	 * @see Table#setColumnExpandRatio(Object, float)
	 */
	void setTableExpandRatio(float tableExpandRatio);

	/**
	 * Gets whether the column is editable
	 * @return <code>true</code> if the column is editable, <code>false</code> otherwise (read-only)
	 */
	boolean isEditable();

	/**
	 * Sets whether the column is editable
	 * @param editable <code>true</code> if the column is editable, <code>false</code> otherwise
	 */
	void setEditable(boolean editable);

	/**
	 * Gets the {@link PropertyEditorFactory} to use to provide the editor {@link Field} for the property to which the
	 * column is bound
	 * @return The {@link PropertyEditorFactory} to use to provide the editor {@link Field} for the property to which the
	 *         column is bound
	 */
	PropertyEditorFactory<P> getEditorFactory();

	/**
	 * Sets the {@link PropertyEditorFactory} to use to provide the editor {@link Field} for the property to which the
	 * column is bound
	 * @param editorFactory The {@link PropertyEditorFactory} to use to provide the editor {@link Field} for the property
	 *        to which the column is bound
	 */
	void setEditorFactory(PropertyEditorFactory<P> editorFactory);

	/**
	 * Gets whether the column is initially hidden when rendered in table
	 * @return <code>true</code> if the column is initially hidden when rendered in table
	 */
	boolean isHidden();

	/**
	 * Sets whether the column is initially hidden when rendered in table
	 * @param hidden <code>true</code> if the column must be initially hidden when rendered in table
	 */
	void setHidden(boolean hidden);

	/**
	 * Gets whether the column visibility can be toggled by user
	 * @return <code>true</code> if the column visibility can be toggled by user
	 */
	boolean isHidable();

	/**
	 * Sets whether the column visibility can be controlled by user
	 * @param hidable <code>true</code> if the column visibility can be controlled by user
	 */
	void setHidable(boolean hidable);

	/**
	 * Gets the caption to use for the table menu with which the user can control column visibility
	 * @return The caption to use for the table menu with which the user can control column visibility
	 */
	Localizable getHidingToggleCaption();

	/**
	 * Sets the caption to use for the table menu with which the user can control column visibility
	 * @param hidingToggleCaption The caption to use for the table menu with which the user can control column
	 *        visibility
	 */
	void setHidingToggleCaption(Localizable hidingToggleCaption);

	/**
	 * Gets whether the column can be resized by user
	 * @return <code>true</code> if the column can be resized by user
	 */
	boolean isResizable();

	/**
	 * Sets whether the column can be resized by user
	 * @param resizable <code>true</code> if the column can be resized by user, <code>false</code> otherwise
	 */
	void setResizable(boolean resizable);

	/**
	 * Gets the column header icon
	 * @return The column header icon
	 */
	Resource getIcon();

	/**
	 * Sets the column header icon
	 * @param icon The column header icon to set
	 */
	void setIcon(Resource icon);

	/**
	 * Gets an optional {@link CellStyleGenerator} to generate column's cells style
	 * @return {@link CellStyleGenerator} to generate column's cells style
	 */
	CellStyleGenerator<T, P> getStyle();

	/**
	 * Sets a {@link CellStyleGenerator} to generate column's cells style
	 * @param cellStyleGenerator The {@link CellStyleGenerator} to generate column's cells style
	 */
	void setStyle(CellStyleGenerator<T, P> cellStyleGenerator);

	/**
	 * Gets the {@link Converter} to use to display column value
	 * @return The {@link Converter} to use to display column value
	 */
	Converter<?, ?> getConverter();

	/**
	 * Sets the {@link Converter} to use to display column value
	 * @param converter The {@link Converter} to use to display column value
	 */
	void setConverter(Converter<?, ?> converter);

	/**
	 * Gets the {@link Renderer} to use to display column value
	 * @return The {@link Renderer} to use to display column value
	 */
	Renderer<?> getRenderer();

	/**
	 * Sets the {@link Renderer} to use to display column value
	 * @param renderer The {@link Renderer} to use to display column value
	 */
	void setRenderer(Renderer<?> renderer);

}
