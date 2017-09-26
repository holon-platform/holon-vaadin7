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
package com.holonplatform.vaadin.data.container;

import com.holonplatform.core.Path;
import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.internal.data.container.DefaultPropertyBoxItem;
import com.vaadin.data.Item;

/**
 * A container {@link Item} which uses a {@link PropertyBox} instance to hold and provide item property values.
 * 
 * <p>
 * This Item property set is immutable and coincides with the bound PropertyBox property set, so
 * {@link #addItemProperty(Object, com.vaadin.data.Property)} and {@link #removeItemProperty(Object)} operations are not
 * supported, throwing an {@link UnsupportedOperationException}.
 * </p>
 *
 * @since 5.0.0
 */
public interface PropertyBoxItem extends Item {

	/**
	 * Get the {@link PropertyBox} to which the Item is bound
	 * @return Item PropertyBox
	 */
	PropertyBox getPropertyBox();

	// Builders

	/**
	 * Create a PropertyBoxItem using given <code>propertyBox</code>
	 * @param propertyBox {@link PropertyBox} to provide item property set and values (not null)
	 * @return PropertyBoxItem instance
	 */
	static PropertyBoxItem create(PropertyBox propertyBox) {
		return new DefaultPropertyBoxItem(propertyBox);
	}

	/**
	 * Create a PropertyBoxItem bound to an empty PropertyBox using given properties.
	 * @param <P> Actual property type
	 * @param properties Property set
	 * @return PropertyBoxItem instance
	 */
	@SafeVarargs
	static <P extends Property<?>> PropertyBoxItem empty(P... properties) {
		return create(PropertyBox.builder(properties).build());
	}

	/**
	 * Create a PropertyBoxItem bound to an empty PropertyBox using given properties.
	 * @param <P> Actual property type
	 * @param properties Property set
	 * @return PropertyBoxItem instance
	 */
	static <P extends Property<?>> PropertyBoxItem empty(Iterable<P> properties) {
		return create(PropertyBox.builder(properties).build());
	}

	/**
	 * Create a PropertyBoxItem using given <code>properties</code> as property set and reading property values from
	 * given <code>bean</code> instance.
	 * <p>
	 * Only bean properties whose name matches a {@link Path} with same name contained in this PropertyBox property set
	 * will be considered.
	 * </p>
	 * <p>
	 * Bean instance structure must the follow JavaBeans rules providing getter methods to read bean property values.
	 * </p>
	 * @param <P> Actual property type
	 * @param bean Bean instance (not null)
	 * @param properties Property set (not null)
	 * @return PropertyBoxItem instance
	 */
	static <P extends Property<?>> PropertyBoxItem fromBean(Object bean, PropertySet<P> properties) {
		ObjectUtils.argumentNotNull(bean, "Bean instance must be not null");
		ObjectUtils.argumentNotNull(properties, "Property set must be not null");
		return create(BeanIntrospector.get().read(PropertyBox.builder(properties).build(), bean));
	}

}
