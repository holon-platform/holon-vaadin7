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
package com.holonplatform.vaadin7.internal;

import javax.annotation.Priority;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin7.components.Input;
import com.vaadin.ui.Field;

/**
 * Default {@link PropertyRenderer} to create {@link Input} type {@link Property} representations.
 * 
 * @param <T> Property type
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public class DefaultInputPropertyRenderer<T> implements PropertyRenderer<Input, T> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
	 */
	@Override
	public Class<? extends Input> getRenderType() {
		return Input.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
	 */
	@Override
	public Input render(Property<? extends T> property) {

		ObjectUtils.argumentNotNull(property, "Property must be not null");
		
		@SuppressWarnings("unchecked")
		final Property<T> p = (Property<T>) property;

		// try to render as Field and convert to Input
		return PropertyRendererRegistry.get().getRenderer(Field.class, p).map(r -> r.render(p))
				.map(field -> asInput(field)).orElse(null);
	}

	@SuppressWarnings("unchecked")
	private static Input asInput(Field field) {
		if (Input.class.isAssignableFrom(field.getClass())) {
			return (Input) field;
		}
		return Input.from(field);
	}

}
