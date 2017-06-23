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

import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Field;

/**
 * Builder to create {@link Boolean} type {@link Field} instances.
 * 
 * @since 5.0.0
 */
public interface BooleanFieldBuilder extends ValidatableFieldBuilder<Boolean, Field<Boolean>, BooleanFieldBuilder> {

	/**
	 * Sets whether to treat <code>null</code> values as {@link Boolean#FALSE} values
	 * @param nullValueAsFalse <code>true</code> to treat <code>null</code> values as {@link Boolean#FALSE} values
	 * @return this
	 */
	BooleanFieldBuilder nullValueAsFalse(boolean nullValueAsFalse);

	/**
	 * Add a listener for focus gained events
	 * @param listener Listener to add
	 * @return this
	 */
	BooleanFieldBuilder withFocusListener(FocusListener listener);

	/**
	 * Add a listener for focus lost events
	 * @param listener Listener to add
	 * @return this
	 */
	BooleanFieldBuilder withBlurListener(BlurListener listener);

}
