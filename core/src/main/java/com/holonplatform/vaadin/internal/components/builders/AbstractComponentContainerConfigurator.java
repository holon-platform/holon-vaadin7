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

import com.holonplatform.vaadin.components.builders.ComponentContainerConfigurator;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachListener;

/**
 * Base {@link ComponentContainerConfigurator} implementation.
 * 
 * @param <I> Internal component type
 * @param <B> Concrete configurator type
 *
 * @since 5.0.0
 */
public abstract class AbstractComponentContainerConfigurator<I extends AbstractComponentContainer, B extends ComponentContainerConfigurator<B>>
		extends AbstractComponentConfigurator<I, B> implements ComponentContainerConfigurator<B> {

	/**
	 * Constructor
	 * @param instance Instance to configure
	 */
	public AbstractComponentContainerConfigurator(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentContainerBuilder#add(com.vaadin.ui.Component[])
	 */
	@Override
	public B add(Component... components) {
		getInstance().addComponents(components);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ComponentContainerBuilder#withComponentAttachListener(com.vaadin.ui
	 * .HasComponents.ComponentAttachListener)
	 */
	@Override
	public B withComponentAttachListener(ComponentAttachListener listener) {
		getInstance().addComponentAttachListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ComponentContainerBuilder#withComponentDetachListener(com.vaadin.ui
	 * .HasComponents.ComponentDetachListener)
	 */
	@Override
	public B withComponentDetachListener(ComponentDetachListener listener) {
		getInstance().addComponentDetachListener(listener);
		return builder();
	}

}
