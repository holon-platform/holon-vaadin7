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
package com.holonplatform.vaadin.internal.data.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.data.container.PropertyBoxItem;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * {@link Item} implementation using a {@link PropertyBox} to hold and provide item property values.
 * <p>
 * Item's property set is immutable, so adding and removing properties is not supported.
 * </p>
 * 
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public class DefaultPropertyBoxItem implements PropertyBoxItem {

	private static final long serialVersionUID = -8007665091516305226L;

	/**
	 * PropertyBox providing data for this item
	 */
	private final PropertyBox propertyBox;

	/**
	 * Item properties
	 */
	private final Map<com.holonplatform.core.property.Property, Property> itemProperties;

	/**
	 * Construct a new Item bound to a {@link PropertyBox}
	 * @param propertyBox PropertyBox providing data for this item (not null)
	 */
	public DefaultPropertyBoxItem(PropertyBox propertyBox) {
		super();
		ObjectUtils.argumentNotNull(propertyBox, "PropertyBox must be not null");
		this.propertyBox = propertyBox;
		this.itemProperties = new HashMap<>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.PropertyBoxItem#getPropertyBox()
	 */
	@Override
	public PropertyBox getPropertyBox() {
		return propertyBox;
	}

	/**
	 * Map of item properties
	 * @return item properties
	 */
	protected Map<com.holonplatform.core.property.Property, Property> getItemProperties() {
		return itemProperties;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Item#getItemProperty(java.lang.Object)
	 */
	@Override
	public Property<?> getItemProperty(Object id) {
		if (id instanceof com.holonplatform.core.property.Property) {
			com.holonplatform.core.property.Property<?> p = (com.holonplatform.core.property.Property<?>) id;
			if (getPropertyBox().contains(p)) {
				return getOrCreateProperty(p);
			}
		}
		return null;
	}

	/**
	 * Get Item {@link Property} from properties map or create it if not present.
	 * <p>
	 * If property type is a primitive type, it is boxed into the corresponding object wrapper type to avoid issues with
	 * Vaadin converters.
	 * </p>
	 * @param id Property id
	 * @return Item property
	 */
	@SuppressWarnings("unchecked")
	protected Property getOrCreateProperty(com.holonplatform.core.property.Property id) {
		if (getItemProperties().containsKey(id)) {
			return getItemProperties().get(id);
		}
		// Create Item property
		final Property property = new DefaultPropertyBoxProperty<>(id, getPropertyBox());
		// Check read-only
		if (id.isReadOnly()) {
			property.setReadOnly(true);
		}
		// Store reference
		getItemProperties().put(id, property);
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Item#getItemPropertyIds()
	 */
	@Override
	public Collection<?> getItemPropertyIds() {
		return ConversionUtils.iterableAsList(getPropertyBox());
	}

	/**
	 * Not supported.
	 * <p>
	 * This Item property set is immutable and coincides with the bound PropertyBox property set.
	 * </p>
	 */
	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 * <p>
	 * This Item property set is immutable and coincides with the bound PropertyBox property set.
	 * </p>
	 */
	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
