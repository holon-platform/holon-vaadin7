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
package com.holonplatform.vaadin.components.builders;

import java.util.Set;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.components.ItemSetComponent.ItemCaptionGenerator;
import com.holonplatform.vaadin.components.ItemSetComponent.ItemIconGenerator;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Field;

/**
 * Base builder to create selection {@link Field}s.
 * 
 * @param <T> Field type
 * @param <C> Field component type
 * @param <S> Selection type
 * @param <ITEM> Selection items type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface BaseSelectFieldBuilder<T, C extends Field<T>, S, ITEM, B extends BaseSelectFieldBuilder<T, C, S, ITEM, B>>
		extends ValidatableFieldBuilder<T, C, B>, BaseItemDataSourceComponentBuilder<C, B> {

	/**
	 * Select UI rendering mode
	 */
	public enum RenderingMode {

		/**
		 * Renders as <em>native</em> select, i.e. using client browser native rendering.
		 * <p>
		 * This rendering mode is not suitable for multi-select mode. In this case, {@link #SELECT} will be used as
		 * fallback.
		 * </p>
		 */
		NATIVE_SELECT,

		/**
		 * Renders as a select field (A ComboBox for single selection mode or a ListSelect for multiple selection mode)
		 */
		SELECT,

		/**
		 * Renders as an options group (A checkbox group for single selection mode or a radio buttons group for multiple
		 * selection mode)
		 */
		OPTIONS

	}

	/**
	 * Allow or disallow empty selection by the user. If the select is in single-select mode, you can make an item
	 * represent the empty selection by calling <code>nullSelectionItemId()</code>. This way you can for instance set an
	 * icon and caption for the null selection item.
	 * @param emptySelectionAllowed whether or not to allow empty selection
	 * @return this
	 */
	B emptySelectionAllowed(boolean emptySelectionAllowed);

	/**
	 * Sets the item id that represents null value of this select.
	 * <p>
	 * Data interface does not support nulls as item ids. Selecting the item identified by this id is the same as
	 * selecting no items at all. This setting only affects the single select mode.
	 * </p>
	 * @param nullSelectionItemId the Null value item id to set.
	 * @return this
	 */
	B nullSelectionItemId(S nullSelectionItemId);

	/**
	 * Set the item caption generator to use to display item captions.
	 * @param itemCaptionGenerator The generator to set
	 * @return this
	 */
	B itemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator);

	/**
	 * Set the item icon generator to use to display item icons.
	 * @param itemIconGenerator The generator to set
	 * @return this
	 */
	B itemIconGenerator(ItemIconGenerator<ITEM> itemIconGenerator);

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption (not null)
	 * @return this
	 */
	B itemCaption(ITEM item, Localizable caption);

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption
	 * @return this
	 */
	default B itemCaption(ITEM item, String caption) {
		return itemCaption(item, Localizable.builder().message(caption).build());
	}

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption
	 * @param messageCode Item caption translation code
	 * @return this
	 */
	default B itemCaption(ITEM item, String caption, String messageCode) {
		return itemCaption(item, Localizable.builder().message(caption).messageCode(messageCode).build());
	}

	/**
	 * Sets the icon for an item.
	 * @param item the item to set the icon for
	 * @param icon Item icon
	 * @return this
	 */
	B itemIcon(ITEM item, Resource icon);

	/**
	 * Single select component configurator.
	 * @param <T> Field type
	 * @param <ITEM> Item type
	 * @param <B> Concrete builder type
	 */
	public interface SingleSelectConfigurator<T, ITEM, B extends SingleSelectConfigurator<T, ITEM, B>>
			extends BaseSelectFieldBuilder<T, SingleSelect<T>, T, ITEM, B> {

		/**
		 * Disables the possibility to input text into the field, so the field area of the component is just used to
		 * show what is selected. If the concrete select component does not support user input, this method has no
		 * effect.
		 * @return this
		 */
		B disableTextInput();

		/**
		 * Sets whether to scroll the selected item visible (directly open the page on which it is) when opening the
		 * combo box popup or not. Only applies to select components with a suggestions popup. This requires finding the
		 * index of the item, which can be expensive in many large lazy loading containers.
		 * @param scrollToSelectedItem true to find the page with the selected item when opening the selection popup
		 * @return this
		 */
		B scrollToSelectedItem(boolean scrollToSelectedItem);

		/**
		 * Sets the suggestion pop-up's width as a CSS string. By using relative units (e.g. "50%") it's possible to set
		 * the popup's width relative to the selection component itself.
		 * <p>
		 * Only applies to select field with backing components supporting a suggestion popup.
		 * </p>
		 * @param width the suggestion pop-up width
		 * @return this
		 */
		B suggestionPopupWidth(String width);

		/**
		 * Sets the option filtering mode.
		 * <p>
		 * Only applies to select field with backing components supporting a suggestion popup.
		 * </p>
		 * @param filteringMode the filtering mode to use
		 * @return this
		 */
		B filteringMode(FilteringMode filteringMode);

	}

	/**
	 * Multi select component configurator.
	 * @param <T> Field type
	 * @param <ITEM> Item type
	 * @param <B> Concrete builder type
	 */
	public interface MultiSelectConfigurator<T, ITEM, B extends MultiSelectConfigurator<T, ITEM, B>>
			extends BaseSelectFieldBuilder<Set<T>, MultiSelect<T>, T, ITEM, B> {

	}

}
