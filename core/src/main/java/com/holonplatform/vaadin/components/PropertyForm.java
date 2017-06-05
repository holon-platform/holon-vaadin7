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
package com.holonplatform.vaadin.components;

import java.util.function.Consumer;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.internal.components.ComponentContainerComposer;
import com.holonplatform.vaadin.internal.components.DefaultPropertyForm;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Field;

/**
 * A {@link PropertyFieldGroup} component to display the property {@link Field}s on a layout.
 * <p>
 * Uses a {@link Composer} to draw the form content, composing the fields on the component setted as form layout.
 * </p>
 * 
 * @since 5.0.0
 */
public interface PropertyForm extends Component, PropertyFieldGroup {

	/**
	 * Get the form content component
	 * @return Form content component
	 */
	Component getContent();

	/**
	 * Compose the property {@link Field}s on the form content component using the previously configured
	 * {@link Composer}.
	 * @throws IllegalStateException If the form content component or {@link Composer} are not configured (null)
	 */
	void compose();

	/**
	 * Delegate to compose the form {@link Field}s on the form layout content.
	 * @param <C> Form content component type
	 */
	public interface Composer<C extends Component> {

		/**
		 * Compose the form {@link Field}s on the form layout content.
		 * @param content Form content
		 * @param propertyFields Property {@link Field}s provider
		 */
		void compose(C content, PropertyFieldContainer propertyFields);

	}

	// Builders

	/**
	 * Create a {@link Composer} which uses a {@link ComponentContainer} as composition layout and adds the fields to
	 * layout in the order they are returned from the {@link PropertyForm}.
	 * @return A new {@link ComponentContainer} composer
	 */
	static Composer<ComponentContainer> componentContainerComposer() {
		return componentContainerComposer(false);
	}

	/**
	 * Create a {@link Composer} which uses a {@link ComponentContainer} as composition layout and adds the fields to
	 * layout in the order they are returned from the {@link PropertyForm}.
	 * @param fullWidthFields <code>true</code> to set full width for all composed fields before adding them to layout
	 * @return A new {@link ComponentContainer} composer
	 */
	static Composer<ComponentContainer> componentContainerComposer(boolean fullWidthFields) {
		return new ComponentContainerComposer(fullWidthFields);
	}

	/**
	 * Get a builder to create a {@link PropertyForm}.
	 * @param <C> Form content component type
	 * @param content Form content, where fields will be composed by the form {@link Composer} (not null)
	 * @return {@link PropertyForm} builder
	 */
	static <C extends Component> PropertyFormBuilder<C> builder(C content) {
		ObjectUtils.argumentNotNull(content, "Form content must be not null");
		return new DefaultPropertyForm.DefaultBuilder<>(content);
	}

	/**
	 * {@link PropertyForm} builder.
	 * @param <C> Form content component type
	 */
	public interface PropertyFormBuilder<C extends Component> extends Builder<PropertyForm, PropertyFormBuilder<C>>,
			ComponentBuilder<PropertyForm, PropertyFormBuilder<C>> {

		/**
		 * Set a form content initializer to setup form content component. This initiliazer is called every time form
		 * fields compisition is triggered.
		 * @param initializer Form content initializer (not null)
		 * @return this
		 */
		PropertyFormBuilder<C> initializer(Consumer<C> initializer);

		/**
		 * Set the form {@link Composer} to be used to compose form fields on the form layout component.
		 * @param composer The composer to set (not null)
		 * @return this
		 */
		PropertyFormBuilder<C> composer(Composer<? super C> composer);

		/**
		 * Set whether to compose the form field on the form layout component when {@link PropertyForm} is attached to a
		 * parent component, only if the form was not already composed using {@link PropertyForm#compose()}.
		 * @param composeOnAttach <code>true</code> to to compose the form field on the form layout component when
		 *        {@link PropertyForm} is attached to a parent component. If <code>false</code>, the
		 *        {@link PropertyForm#compose()} must be invoked to compose the form fields.
		 * @return this
		 */
		PropertyFormBuilder<C> composeOnAttach(boolean composeOnAttach);

		/**
		 * Set the caption for the {@link Field}s bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the field caption (not null)
		 * @param caption Localizable field caption
		 * @return this
		 */
		PropertyFormBuilder<C> propertyCaption(Property<?> property, Localizable caption);

		/**
		 * Set the caption for the {@link Field}s bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the field caption (not null)
		 * @param caption Field caption
		 * @return this
		 */
		PropertyFormBuilder<C> propertyCaption(Property<?> property, String caption);

		/**
		 * Set the caption for the {@link Field}s bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the field caption (not null)
		 * @param defaultCaption Default caption if no translation is available for given <code>messageCode</code> for
		 *        current Locale, or no {@link LocalizationContext} is available at all
		 * @param messageCode Caption translation message key
		 * @param arguments Optional translation arguments
		 * @return this
		 */
		PropertyFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption, String messageCode,
				Object... arguments);

		/**
		 * Set the caption for the {@link Field}s bound to given property as hidden.
		 * @param property Property for which to hide the field caption (not null)
		 * @return this
		 */
		PropertyFormBuilder<C> hidePropertyCaption(Property<?> property);

	}

}
