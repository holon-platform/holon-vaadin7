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
package com.holonplatform.vaadin.internal.data.container;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;

import com.holonplatform.vaadin.internal.data.ItemStore;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;

/**
 * {@link ValueChangeListener} used by {@link ItemStore} to track item properties modifications
 * 
 * @since 5.0.0
 */
public class ItemPropertyValueChangeListener implements ValueChangeListener {

	private static final long serialVersionUID = 2323107238967436191L;

	private final WeakReference<Item> item;
	private final ItemStore<Item> itemStore;

	private Collection<WeakReference<Property<?>>> trackedItemProperties;

	/**
	 * Constructor
	 * @param item Item
	 * @param itemStore Item store
	 */
	public ItemPropertyValueChangeListener(Item item, ItemStore<Item> itemStore) {
		super();
		this.item = new WeakReference<>(item);
		this.itemStore = itemStore;

		if (item != null) {
			// add value change listener to track property modifications
			Collection<?> itemPropertyIds = item.getItemPropertyIds();
			if (itemPropertyIds != null) {
				trackedItemProperties = new HashSet<>(itemPropertyIds.size());
				for (Object itemPropertyId : itemPropertyIds) {
					Property<?> itemProperty = item.getItemProperty(itemPropertyId);
					if (itemProperty instanceof ValueChangeNotifier) {
						((ValueChangeNotifier) itemProperty).addValueChangeListener(this);
						trackedItemProperties.add(new WeakReference<>(itemProperty));
					}
				}
			}
		}
	}

	/**
	 * Detach this listener from item properties
	 */
	public void detach() {
		if (trackedItemProperties != null) {
			for (WeakReference<Property<?>> itemProperty : trackedItemProperties) {
				Property<?> property = itemProperty.get();
				if (property != null) {
					((ValueChangeNotifier) property).removeValueChangeListener(this);
				}
			}
		}
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Item itm = item.get();
		if (itm != null) {
			itemStore.setItemModified(itm);
		}
	}

}
