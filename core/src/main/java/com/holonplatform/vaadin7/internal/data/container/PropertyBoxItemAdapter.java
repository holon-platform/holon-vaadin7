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
package com.holonplatform.vaadin7.internal.data.container;

import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin7.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin7.data.container.ItemAdapter;
import com.holonplatform.vaadin7.data.container.PropertyBoxItem;
import com.vaadin.data.Item;

/**
 * {@link ItemAdapter} to handle {@link PropertyBox} item data using {@link PropertyBoxItem}s.
 *
 * @since 5.0.0
 */
public class PropertyBoxItemAdapter implements ItemAdapter<PropertyBox> {

	private static final long serialVersionUID = -572185846252965732L;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemAdapter#adapt(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration, java.lang.Object)
	 */
	@Override
	public Item adapt(Configuration<?> configuration, PropertyBox item) {
		return (item != null) ? PropertyBoxItem.create(item) : null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemAdapter#restore(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration, com.vaadin.data.Item)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PropertyBox restore(Configuration<?> configuration, Item item) {
		if (item != null) {
			// check is a PropertyBoxItem
			if (PropertyBoxItem.class.isAssignableFrom(item.getClass())) {
				return ((PropertyBoxItem) item).getPropertyBox();
			}
			// build from properties
			List<Property> ps = new LinkedList<>();
			configuration.getProperties().forEach(p -> {
				if (p.getClass().isAssignableFrom(Property.class)) {
					ps.add((Property) p);
				}
			});
			if (ps.isEmpty()) {
				throw new UnsupportedOperationException("Failed to restore PropertyBox from item [" + item
						+ "]: it is not a PropertyBoxItem and container properties are not of ["
						+ Property.class.getName() + "] type");
			}
			PropertyBox.Builder box = PropertyBox.builder(ps).invalidAllowed(true);
			for (Property property : ps) {
				com.vaadin.data.Property<?> itemProperty = item.getItemProperty(property);
				if (itemProperty != null) {
					box.set(property, itemProperty.getValue());
				}
			}
			return box.build();
		}
		return null;
	}

}
