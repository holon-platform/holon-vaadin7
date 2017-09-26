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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.Property.PropertyReadOnlyException;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

/**
 * A {@link Property} bound to a {@link com.holonplatform.core.property.Property} id which value is stored in a
 * {@link PropertyBox}.
 * 
 * @param <T> Property type
 * 
 * @since 5.0.0
 */
public class DefaultPropertyBoxProperty<T> extends AbstractProperty<T> {

	private static final long serialVersionUID = 4772299718230608889L;

	/*
	 * Property Id
	 */
	private final com.holonplatform.core.property.Property<T> propertyId;
	/*
	 * Property box
	 */
	private final PropertyBox propertyBox;

	public DefaultPropertyBoxProperty(com.holonplatform.core.property.Property<T> propertyId, PropertyBox propertyBox) {
		super();

		ObjectUtils.argumentNotNull(propertyId, "Property must be not null");
		ObjectUtils.argumentNotNull(propertyBox, "PropertyBox must be not null");

		this.propertyId = propertyId;
		this.propertyBox = propertyBox;
	}

	/**
	 * {@link com.holonplatform.core.property.Property} id
	 * @return the property id
	 */
	protected com.holonplatform.core.property.Property<T> getPropertyId() {
		return propertyId;
	}

	/**
	 * PropertyBox in which property value is stored
	 * @return the PropertyBox
	 */
	protected PropertyBox getPropertyBox() {
		return propertyBox;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property#getValue()
	 */
	@Override
	public T getValue() {
		return getPropertyBox().getValue(getPropertyId());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T newValue) throws com.vaadin.data.Property.ReadOnlyException {

		// Check read-only
		if (isReadOnly()) {
			throw new Property.ReadOnlyException();
		}

		try {
			getPropertyBox().setValue(getPropertyId(), newValue);
		} catch (@SuppressWarnings("unused") PropertyReadOnlyException e) {
			throw new com.vaadin.data.Property.ReadOnlyException("Property is read-only: " + getPropertyId());
		}

		// fire value change event
		fireValueChange();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property#getType()
	 */
	@Override
	public Class<? extends T> getType() {
		return TypeUtils.box(getPropertyId().getType());
	}

}
