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
package com.holonplatform.vaadin7.internal.components.builders;

import com.holonplatform.vaadin7.components.builders.CssLayoutBuilder;
import com.vaadin.ui.CssLayout;

/**
 * Default {@link CssLayoutBuilder} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultCssLayoutBuilder extends AbstractBaseLayoutBuilder<CssLayout, CssLayout, CssLayoutBuilder>
		implements CssLayoutBuilder {

	public DefaultCssLayoutBuilder() {
		super(new CssLayout());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
	 */
	@Override
	protected CssLayoutBuilder builder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected CssLayout build(CssLayout instance) {
		return instance;
	}

}
