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
package com.holonplatform.vaadin7.components.builders;

import com.vaadin.ui.Layout;

/**
 * Builder to create {@link Layout} component containers.
 * 
 * @param <C> Layout type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface LayoutBuilder<C extends Layout, B extends LayoutBuilder<C, B>>
		extends LayoutConfigurator<B>, BaseLayoutBuilder<C, B> {

}
