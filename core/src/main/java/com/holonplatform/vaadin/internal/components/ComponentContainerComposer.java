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

import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.PropertyForm.Composer;
import com.holonplatform.vaadin.components.PropertyInputContainer;
import com.vaadin.ui.ComponentContainer;

/**
 * Default {@link Composer} using a {@link ComponentContainer} as composition layout and adding the {@link Input}
 * componens to the layout in the order they are returned from the {@link PropertyInputContainer}.
 * <p>
 * Provides a construction parameter to automatically set all inputs to full width.
 * </p>
 * 
 * @since 5.0.0
 */
public class ComponentContainerComposer implements Composer<ComponentContainer> {

	private final boolean fullWidthInputs;

	/**
	 * Constructor
	 * @param fullWidthInputs <code>true</code> to set the width for all composed {@link Input}s as <code>100%</code>.
	 */
	public ComponentContainerComposer(boolean fullWidthInputs) {
		super();
		this.fullWidthInputs = fullWidthInputs;
	}

	/**
	 * Gets whether to set the width for all composed {@link Input}s as <code>100%</code>.
	 * @return the fullWidthInputs <code>true</code> if the {@link Input}'s width must be setted to <code>100%</code>
	 */
	protected boolean isFullWidthInputs() {
		return fullWidthInputs;
	}

	@Override
	public void compose(ComponentContainer content, PropertyInputContainer propertyInputs) {
		// remove all components
		content.removeAllComponents();
		// add components
		propertyInputs.getInputs().forEach(input -> {
			if (isFullWidthInputs()) {
				input.getComponent().setWidth("100%");
			}
			content.addComponent(input.getComponent());
		});
	}

}
