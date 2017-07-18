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

import com.holonplatform.vaadin.components.Input;

/**
 * Builder for input components whith {@link InvalidFieldNotificationMode} support.
 *
 * @param <T> Field type
 * @param <C> Field component type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface ValidatableFieldBuilder<T, C extends Input<T>, B extends ValidatableFieldBuilder<T, C, B>>
		extends FieldBuilder<T, C, B> {

	/**
	 * Sets the invalid Field error notification mode
	 * @param invalidFieldNotificationMode the InvalidFieldNotificationMode to set
	 * @return this
	 */
	B invalidFieldNotificationMode(InvalidFieldNotificationMode invalidFieldNotificationMode);

}
