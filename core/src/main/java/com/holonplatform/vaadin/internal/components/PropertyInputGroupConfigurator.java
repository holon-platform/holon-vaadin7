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
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.PropertyBinding.PostProcessor;
import com.holonplatform.vaadin.components.PropertyInputGroup;
import com.holonplatform.vaadin.components.ValidationErrorHandler;

/**
 * Configurator interface to setup and build a {@link PropertyInputGroup}.
 */
public interface PropertyInputGroupConfigurator extends PropertyInputGroup {

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
	 * Set the {@link PropertyRenderer} to use with given property to obtain the property {@link Input} component.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param renderer Renderer
	 */
	<T, F extends T> void setPropertyRenderer(Property<T> property, PropertyRenderer<Input<F>, T> renderer);

	/**
	 * Set whether to stop validation at first validation failure.
	 * @param stopValidationAtFirstFailure <code>true</code> to stop validation at first validation failure
	 */
	void setStopValidationAtFirstFailure(boolean stopValidationAtFirstFailure);

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
	 * Set whether to ignore missing property inputs
	 * @param ignoreMissingInputs <code>true</code> to ignore missing property inputs
	 */
	void setIgnoreMissingInputs(boolean ignoreMissingInputs);

	/**
	 * Add an {@link Input} {@link PostProcessor}.
	 * @param postProcessor the post-processor to add
	 */
	void addInputPostProcessor(PostProcessor<Input<?>> postProcessor);

	/**
	 * Build and bind {@link Input}s to the properties of the property set.
	 */
	void build();

}
