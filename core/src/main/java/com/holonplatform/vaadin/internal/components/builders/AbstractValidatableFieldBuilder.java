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

import com.holonplatform.core.Validator;
import com.holonplatform.vaadin.components.ValidatableField;
import com.holonplatform.vaadin.components.ValidatableField.InvalidFieldNotificationMode;
import com.holonplatform.vaadin.components.builders.ValidatableFieldBuilder;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

/**
 * Base {@link ValidatableFieldBuilder} class.
 * 
 * @param <T> Field type
 * @param <C> Internal field type
 * 
 * @since 5.0.0
 */
public abstract class AbstractValidatableFieldBuilder<T, C extends Field<T>, I extends AbstractField<T> & ValidatableField<T>, B extends ValidatableFieldBuilder<T, C, B>>
		extends AbstractFieldBuilder<T, C, I, B> implements ValidatableFieldBuilder<T, C, B> {

	/**
	 * Constructor
	 * @param instance Internal Field instance
	 */
	public AbstractValidatableFieldBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ValidatableFieldBuilder#withValidator(com.holonplatform.core.
	 * validator.Validator)
	 */
	@Override
	public B withValidator(Validator<T> validator) {
		getInstance().addValidator(validator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ValidatableFieldBuilder#invalidFieldNotificationMode(com.
	 * holonframework.vaadin.components.ValidatableField.InvalidFieldNotificationMode)
	 */
	@Override
	public B invalidFieldNotificationMode(InvalidFieldNotificationMode invalidFieldNotificationMode) {
		getInstance().setInvalidFieldNotificationMode(invalidFieldNotificationMode);
		return builder();
	}

}
