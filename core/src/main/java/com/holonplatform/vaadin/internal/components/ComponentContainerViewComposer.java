/*
 * Copyright 2000-2017 Holon TDCN.
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

import com.holonplatform.vaadin.components.ComposableComponent.Composer;
import com.holonplatform.vaadin.components.PropertyViewSource;
import com.holonplatform.vaadin.components.ViewComponent;
import com.vaadin.ui.ComponentContainer;

/**
 * Default {@link Composer} using a {@link ComponentContainer} as composition layout and adding the
 * {@link ViewComponent} componens to the layout in the order they are returned from the {@link PropertyViewSource}.
 * <p>
 * Provides a construction parameter to automatically set all inputs to full width.
 * </p>
 * 
 * @since 5.0.0
 */
public class ComponentContainerViewComposer implements Composer<ComponentContainer, PropertyViewSource> {

	private final boolean fullWidthViews;

	/**
	 * Constructor
	 * @param fullWidthViews <code>true</code> to set the width for all composed {@link ViewComponent}s as
	 *        <code>100%</code>.
	 */
	public ComponentContainerViewComposer(boolean fullWidthViews) {
		super();
		this.fullWidthViews = fullWidthViews;
	}

	/**
	 * Gets whether to set the width for all composed {@link ViewComponent}s as <code>100%</code>.
	 * @return <code>true</code> if the {@link ViewComponent}'s width must be setted to <code>100%</code>
	 */
	protected boolean isFullWidthViews() {
		return fullWidthViews;
	}

	@Override
	public void compose(ComponentContainer content, PropertyViewSource source) {
		// remove all components
		content.removeAllComponents();
		// add components
		source.getViewComponents().forEach(vc -> {
			if (isFullWidthViews()) {
				vc.setWidth("100%");
			}
			content.addComponent(vc);
		});
	}

}
