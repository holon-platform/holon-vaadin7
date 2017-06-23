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

import com.holonplatform.vaadin.components.PropertyFieldContainer;
import com.holonplatform.vaadin.components.PropertyForm.Composer;
import com.vaadin.ui.ComponentContainer;

/**
 * Default {@link Composer} using a {@link ComponentContainer} as composition layout and adding the fields to layout in
 * the order they are returned from the {@link PropertyFieldContainer}.
 * <p>
 * Provides a construction parameter to automatically set all fields to full width.
 * </p>
 * 
 * @since 5.0.0
 */
public class ComponentContainerComposer implements Composer<ComponentContainer> {

	private final boolean fullWidthFields;

	/**
	 * Constructor
	 * @param fullWidthFields <code>true</code> to set full width for all composed fields
	 */
	public ComponentContainerComposer(boolean fullWidthFields) {
		super();
		this.fullWidthFields = fullWidthFields;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyForm.Composer#compose(com.vaadin.ui.Component,
	 * com.holonplatform.vaadin.components.PropertyFieldContainer)
	 */
	@Override
	public void compose(ComponentContainer content, PropertyFieldContainer propertyFields) {
		// remove all components
		content.removeAllComponents();
		// add components
		propertyFields.getFields().forEach(f -> {
			if (fullWidthFields) {
				f.setWidth("100%");
			}
			content.addComponent(f);
		});
	}

}
