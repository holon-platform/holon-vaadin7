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
package com.holonplatform.vaadin.internal.components;

import com.holonplatform.core.Validator;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.PropertyFieldGroup;
import com.holonplatform.vaadin.components.ValidationErrorHandler;
import com.vaadin.ui.Field;

/**
 * Configurator interface to seup and build a {@link PropertyFieldGroup}
 */
public interface PropertyFieldGroupConfigurator extends PropertyFieldGroup {

	/**
	 * Add a property to the property set
	 * @param property Property to add
	 */
	@SuppressWarnings("rawtypes")
	void addProperty(Property property);

	/**
	 * Set a property as read-only
	 * @param property Property
	 */
	@SuppressWarnings("rawtypes")
	void setPropertyReadOnly(Property property);

	/**
	 * Set a property as hidden
	 * @param property Property
	 */
	@SuppressWarnings("rawtypes")
	void setPropertyHidden(Property property);

	/**
	 * Set a property as required
	 * @param property Property
	 */
	@SuppressWarnings("rawtypes")
	void setPropertyRequired(Property property);

	/**
	 * Add a property validator
	 * @param <T> Property type
	 * @param property Property
	 * @param validator Validator to add
	 */
	<T> void addPropertyValidator(Property<T> property, Validator<T> validator);

	/**
	 * Set the {@link FieldPropertyRenderer} to use with given property
	 * @param <T> Property type
	 * @param property Property
	 * @param renderer Renderer
	 */
	<T> void setPropertyRenderer(Property<T> property, FieldPropertyRenderer<T> renderer);

	/**
	 * Set whether to stop field validation at first validation failure.
	 * @param stopFieldValidationAtFirstFailure <code>true</code> to stop field validation at first validation failure
	 */
	void setStopFieldValidationAtFirstFailure(boolean stopFieldValidationAtFirstFailure);

	/**
	 * Set whether to stop overall validation at first validation failure.
	 * @param stopOverallValidationAtFirstFailure <code>true</code> to stop overall validation at first validation
	 *        failure
	 */
	void setStopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure);

	/**
	 * Set whether to ignore validation
	 * @param ignoreValidation <code>true</code> to ignore validation
	 */
	void setIgnoreValidation(boolean ignoreValidation);

	/**
	 * Set the default {@link ValidationErrorHandler} to use.
	 * @param defaultValidationErrorHandler the default ValidationErrorHandler to set
	 */
	void setDefaultValidationErrorHandler(ValidationErrorHandler defaultValidationErrorHandler);

	/**
	 * Set whether to ignore missing property fields
	 * @param ignoreMissingField <code>true</code> to ignore missing property fields
	 */
	void setIgnoreMissingField(boolean ignoreMissingField);

	/**
	 * Add a FieldConfigurator
	 * @param fieldConfigurator the FieldConfigurator to add
	 */
	void addFieldConfigurator(FieldConfigurator fieldConfigurator);

	/**
	 * Build and bind {@link Field}s to the properties of the property set.
	 */
	void build();

}
