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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.builders.ComponentBuilder;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Base class for {@link ComponentBuilder}s.
 * 
 * @param <C> Component type
 * @param <I> Internal component type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public abstract class AbstractComponentBuilder<C extends Component, I extends AbstractComponent, B extends ComponentBuilder<C, B>>
		extends AbstractComponentConfigurator<I, B> implements ComponentBuilder<C, B> {

	/**
	 * Deferred messages localization
	 */
	protected boolean deferLocalization = false;

	protected Localizable caption;
	protected Localizable description;

	/**
	 * Constructor
	 * @param instance Instance to build and return (not null)
	 */
	public AbstractComponentBuilder(I instance) {
		super(instance);
	}

	/**
	 * Build concrete instance in expected type
	 * @param instance Building instance
	 * @return Instance in expected type
	 */
	protected abstract C build(I instance);

	/**
	 * Performs messages localization
	 * @param instance Building instance
	 */
	protected void localize(I instance) {
		if (caption != null) {
			instance.setCaption(LocalizationContext.translate(caption, true));
		}
		if (description != null) {
			instance.setDescription(LocalizationContext.translate(description, true));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#deferLocalization()
	 */
	@Override
	public B deferLocalization() {
		this.deferLocalization = true;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ComponentBuilder#caption(com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B caption(Localizable caption) {
		this.caption = caption;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#description(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public B description(Localizable description) {
		this.description = description;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#build()
	 */
	@Override
	public C build() {
		if (deferLocalization) {
			instance.addAttachListener((e) -> localize(instance));
		} else {
			localize(instance);
		}
		return build(instance);
	}

}
