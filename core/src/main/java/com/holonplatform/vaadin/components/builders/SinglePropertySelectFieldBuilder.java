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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.SingleSelect;
import com.vaadin.ui.Field;

/**
 * Builder to create a single selection {@link Field} with {@link Property} data source support.
 * 
 * @param <T> Field type
 * 
 * @since 5.0.0
 */
public interface SinglePropertySelectFieldBuilder<T>
		extends SelectFieldBuilder.SingleSelectConfigurator<T, PropertyBox, SinglePropertySelectFieldBuilder<T>>,
		PropertySelectFieldBuilder<T, SingleSelect<T>, T, SinglePropertySelectFieldBuilder<T>> {

}
