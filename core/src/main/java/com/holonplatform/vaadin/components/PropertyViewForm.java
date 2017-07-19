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
package com.holonplatform.vaadin.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.internal.components.ComponentContainerViewComposer;
import com.holonplatform.vaadin.internal.components.DefaultPropertyViewForm;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * A {@link PropertyViewGroup} component to display the property {@link ViewComponent}s on a layout, using the
 * {@link ComposableComponent} composition strategy.
 * <p>
 * A {@link Composer} is used to draw the form UI, composing the {@link ViewComponent} components obtained from a
 * {@link PropertyViewSource} into the component setted as content.
 * </p>
 * 
 * @since 5.0.0
 */
public interface PropertyViewForm extends ComposableComponent, PropertyViewGroup {

	// Builders

	/**
	 * Create a {@link Composer} which uses a {@link ComponentContainer} as composition layout and adds the
	 * {@link ViewComponent}s components to layout in the order they are returned from the {@link PropertyViewForm}.
	 * @return A new {@link ComponentContainer} composer
	 */
	static Composer<ComponentContainer, PropertyViewSource> componentContainerComposer() {
		return componentContainerComposer(false);
	}

	/**
	 * Create a {@link Composer} which uses a {@link ComponentContainer} as composition layout and adds the
	 * {@link ViewComponent}s components to layout in the order they are returned from the {@link PropertyViewForm}.
	 * @param fullWidthViews <code>true</code> to set the width of all composed {@link ViewComponent}s to
	 *        <code>100%</code> before adding them to the layout
	 * @return A new {@link ComponentContainer} composer
	 */
	static Composer<ComponentContainer, PropertyViewSource> componentContainerComposer(boolean fullWidthViews) {
		return new ComponentContainerViewComposer(fullWidthViews);
	}

	/**
	 * Get a builder to create a {@link PropertyViewForm}.
	 * @param <C> Form content component type
	 * @param content Form content, where {@link ViewComponent}s will be composed by the form {@link Composer} (not
	 *        null)
	 * @return {@link PropertyViewForm} builder
	 */
	static <C extends Component> PropertyViewFormBuilder<C> builder(C content) {
		ObjectUtils.argumentNotNull(content, "Form content must be not null");
		return new DefaultPropertyViewForm.DefaultBuilder<>(content);
	}

	/**
	 * {@link PropertyViewForm} builder.
	 * @param <C> Form content component type
	 */
	public interface PropertyViewFormBuilder<C extends Component>
			extends ComposableComponent.Builder<PropertyViewSource, C, PropertyViewFormBuilder<C>>,
			PropertyViewGroup.Builder<PropertyViewForm, PropertyViewFormBuilder<C>>,
			ComponentBuilder<PropertyViewForm, PropertyViewFormBuilder<C>> {

		/**
		 * Set the caption for the view component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the view component caption (not null)
		 * @param caption Localizable view component caption
		 * @return this
		 */
		PropertyViewFormBuilder<C> propertyCaption(Property<?> property, Localizable caption);

		/**
		 * Set the caption for the view component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the view component caption (not null)
		 * @param caption View component caption
		 * @return this
		 */
		PropertyViewFormBuilder<C> propertyCaption(Property<?> property, String caption);

		/**
		 * Set the caption for the view component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the view component caption (not null)
		 * @param defaultCaption Default caption if no translation is available for given <code>messageCode</code> for
		 *        current Locale, or no {@link LocalizationContext} is available at all
		 * @param messageCode Caption translation message key
		 * @param arguments Optional translation arguments
		 * @return this
		 */
		PropertyViewFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption, String messageCode,
				Object... arguments);

		/**
		 * Set the caption for the view component bound to given property as hidden.
		 * @param property Property for which to hide the view component caption (not null)
		 * @return this
		 */
		PropertyViewFormBuilder<C> hidePropertyCaption(Property<?> property);

	}

}
