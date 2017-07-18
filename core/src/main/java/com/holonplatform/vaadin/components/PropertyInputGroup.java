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
package com.holonplatform.vaadin.components;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.Validatable;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.Validator.ValidatorSupport;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.Property.PropertyNotFoundException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.internal.components.DefaultPropertyInputGroup;

/**
 * A {@link InputGroup} to manage a group of {@link Input}s bound to a {@link Property} set.
 * <p>
 * Supports overall {@link Validator}s registration to validate all the {@link Input} values, allowing cross input
 * validation, using a {@link PropertyBox} to represent the inputs value set.
 * </p>
 * <p>
 * Fully supports {@link Input} values loading and flushing using {@link PropertyBox} instances.
 * </p>
 * <p>
 * By default, property {@link Input} components are obtained from the {@link PropertyRenderer}s registered in the
 * context {@link PropertyRendererRegistry}, if available. Custom {@link PropertyRenderer} registration is supported to
 * provide a custom behaviour for specific properties.
 * </p>
 * <p>
 * Default property values are supported using a {@link DefaultValueProvider}. The property default values are loaded
 * when {@link #clear()} or {@link #setValue(PropertyBox)} methods are invoked.
 * </p>
 * <p>
 * Convenience methods {@link #setEnabled(boolean)} and {@link #setReadOnly(boolean)} can be used to change the enabled
 * / read-only state for all the property bound {@link Input}s.
 * </p>
 * 
 * @since 5.0.0
 */
public interface PropertyInputGroup
		extends InputGroup, PropertyInputContainer, ValidatorSupport<PropertyBox>, Validatable<PropertyBox> {

	/**
	 * Adds a {@link Validator} to the given <code>property</code>.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param validator Validator to add (not null)
	 */
	<T> void addValidator(Property<T> property, Validator<T> validator);

	/**
	 * Removes a {@link Validator} from the given <code>property</code>.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param validator Validator to remove (not null)
	 */
	<T> void removeValidator(Property<T> property, Validator<T> validator);

	/**
	 * Set a {@link DefaultValueProvider} to obtain the given property default value.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param defaultValueProvider DefaultValueProvider (not null)
	 */
	<T> void setDefaultValueProvider(Property<T> property, DefaultValueProvider<T> defaultValueProvider);

	/**
	 * Remove the {@link DefaultValueProvider} bound to given property, if any.
	 * @param <T> Property type
	 * @param property Property (not null)
	 */
	<T> void removeDefaultValueProvider(Property<T> property);

	/**
	 * Get the all the property {@link Input}s values using a {@link PropertyBox}.
	 * <p>
	 * The {@link PropertyInputGroup} property set is used as the {@link PropertyBox} property set.
	 * </p>
	 * @param validate <code>true</code> to check the validity of the bound {@link Input}s and of this
	 *        {@link PropertyInputGroup} before returing the value
	 * @return A {@link PropertyBox} containing the values of the properties of this {@link PropertyInputGroup}
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and a input value is not valid
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyInputGroup} validators)
	 */
	PropertyBox getValue(boolean validate);

	/**
	 * Get the all the property {@link Input}s values using a {@link PropertyBox}, validating the bound inputs and this
	 * {@link PropertyInputGroup} before returing the value.
	 * <p>
	 * The {@link PropertyInputGroup} property set is used as the {@link PropertyBox} property set.
	 * </p>
	 * @return A {@link PropertyBox} containing the values of the properties of this {@link PropertyInputGroup}
	 * @throws ValidationException If an input value is not valid, providing the validation error message.
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyInputGroup} validators)
	 */
	default PropertyBox getValue() {
		return getValue(true);
	}

	/**
	 * Flush all the property {@link Input}s values to given {@link PropertyBox}.
	 * @param propertyBox PropertyBox into which to write the values (not null)
	 * @param validate <code>true</code> to check the validity of the bound inputs and of this
	 *        {@link PropertyInputGroup} before writing the values
	 * @throws PropertyNotFoundException If a property to flush is not found in given {@link PropertyBox}
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and an input value is not valid
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyInputGroup} validators)
	 */
	void flush(PropertyBox propertyBox, boolean validate);

	/**
	 * Flush all the property {@link Input}s values to given {@link PropertyBox}, validating the bound {@link Input}s
	 * and this {@link PropertyInputGroup} before writing the values.
	 * @param propertyBox PropertyBox into which to write the values (not null)
	 * @throws PropertyNotFoundException If a property to flush is not found in given {@link PropertyBox}
	 * @throws ValidationException If an input value is not valid, providing the validation error message.
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyInputGroup} validators)
	 */
	default void flush(PropertyBox propertyBox) {
		flush(propertyBox, true);
	}

	/**
	 * Load the values contained in given {@link PropertyBox} to the bound {@link Input}s of this
	 * {@link PropertyInputGroup}.
	 * <p>
	 * Only the properties of this {@link PropertyInputGroup} property set are used to read the values from the
	 * {@link PropertyBox}.
	 * </p>
	 * @param propertyBox PropertyBox from which to read the values (not null)
	 * @param validate <code>true</code> to check the validity of the bound inputs and of this
	 *        {@link PropertyInputGroup} after setting the values
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and an input value is not valid
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyInputGroup} validators)
	 */
	void setValue(PropertyBox propertyBox, boolean validate);

	/**
	 * Load the values contained in given {@link PropertyBox} to the bound {@link Input}s of this
	 * {@link PropertyInputGroup}, validating the bound inputs and this {@link PropertyInputGroup} after setting the
	 * values.
	 * <p>
	 * No validation is performed.
	 * </p>
	 * @param propertyBox PropertyBox from which to read the values. If <code>null</code>, all {@link Input}s are
	 *        cleared.
	 * @see #setValue(PropertyBox, boolean)
	 */
	default void setValue(PropertyBox propertyBox) {
		setValue(propertyBox, false);
	}

	/**
	 * Get a {@link Builder} to create and setup a {@link PropertyInputGroup}.
	 * @return {@link PropertyInputGroup} builder
	 */
	static PropertyInputGroupBuilder builder() {
		return new DefaultPropertyInputGroup.DefaultBuilder();
	}

	// -------

	/**
	 * Interface to provide the default value for a {@link Property}.
	 * @param <T> Property type
	 */
	@FunctionalInterface
	public interface DefaultValueProvider<T> {

		/**
		 * Get the property default value
		 * @param property Property (never null)
		 * @return Default value
		 */
		T getDefaultValue(Property<T> property);

	}

	/**
	 * Callback interface to process an {@link Input} bound to a {@link Property} before the actual binding is setted
	 * up, for example to perform additional input configuration operations.
	 */
	@FunctionalInterface
	public interface InputPostProcessor {

		/**
		 * Process the given {@link Input} before binding it to its property.
		 * @param property Property to which the {@link Input} will be bound
		 * @param input {@link Input} component
		 */
		void process(Property<?> property, Input<?> input);

	}

	// Builder

	/**
	 * {@link PropertyInputGroup} builder.
	 */
	public interface PropertyInputGroupBuilder extends Builder<PropertyInputGroup, PropertyInputGroupBuilder> {

	}

	/**
	 * Base {@link PropertyInputGroup} builder.
	 * @param <G> Actual {@link PropertyInputGroup} type
	 * @param <B> Concrete builder type
	 */
	public interface Builder<G extends PropertyInputGroup, B extends Builder<G, B>> {

		/**
		 * Add given properties to the {@link PropertyInputGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add
		 * @return this
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		<P extends Property> B properties(P... properties);

		/**
		 * Add given properties to the {@link PropertyInputGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		<P extends Property> B properties(Iterable<P> properties);

		/**
		 * Set the given property as read-only. If a property is read-only, the {@link Input} bound to the property will
		 * be setted as read-only too, and its value will never be written to a {@link PropertyBox} nor validated.
		 * @param <T> Property type
		 * @param property Property to set as read-only (not null)
		 * @return this
		 */
		<T> B readOnly(Property<T> property);

		/**
		 * Set the given property as required. If a property is required, the {@link Input} bound to the property will
		 * be setted as required, and its validation will fail when empty.
		 * @param <T> Property type
		 * @param property Property to set as required (not null)
		 * @return this
		 */
		<T> B required(Property<T> property);

		/**
		 * Set the given property as hidden. If a property is hidden, the {@link Input} bound to the property will never
		 * be generated, but its value will be written to a {@link PropertyBox} using
		 * {@link PropertyInputGroup#getValue()} or {@link PropertyInputGroup#flush(PropertyBox)}.
		 * @param <T> Property type
		 * @param property Property to set as hidden (not null)
		 * @return this
		 */
		<T> B hidden(Property<T> property);

		/**
		 * Set the default value provider for given <code>property</code>.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param defaultValueProvider DefaultValueProvider (not null)
		 * @return this
		 */
		<T> B defaultValue(Property<T> property, DefaultValueProvider<T> defaultValueProvider);

		/**
		 * Adds a {@link Validator} to the {@link Input} bound to given <code>property</code>.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		<T> B withValidator(Property<T> property, Validator<T> validator);

		/**
		 * Adds a {@link Validator} to the {@link PropertyInputGroup}, using a {@link PropertyBox} to provide the
		 * property values to validate.
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		B withValidator(Validator<PropertyBox> validator);

		/**
		 * Set the specific {@link PropertyRenderer} to use to render the {@link Input} to bind to given
		 * <code>property</code>.
		 * @param <T> Property type
		 * @param <F> Rendered input type
		 * @param property Property (not null)
		 * @param renderer Property renderer (not null)
		 * @return this
		 */
		<T, F extends T> B bind(Property<T> property, PropertyRenderer<Input<F>, T> renderer);

		/**
		 * Convenience method to set a specific {@link PropertyRenderer} to use to render the {@link Input} to bind to
		 * given <code>property</code> using the {@link InputPropertyRenderer} functional interface.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param renderer Property renderer (not null)
		 * @return this
		 */
		default <T> B bind(Property<T> property, InputPropertyRenderer<T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "Renderer must be not null");
			return bind(property, (PropertyRenderer<Input<T>, T>) renderer);
		}

		/**
		 * Bind the given <code>property</code> to given <code>input</code> instance. If the property was already bound
		 * to a {@link Input}, the old input will be replaced by the new input.
		 * <p>
		 * This method also adds property validators to given {@link Input} when applicable.
		 * </p>
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param input Input to bind (not null)
		 * @return this
		 */
		default <T> B bind(Property<T> property, Input<T> input) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(input, "Input must be not null");
			return bind(property, p -> input);
		}

		/**
		 * Set whether to stop validation at first validation failure. If <code>true</code>, only the first
		 * {@link ValidationException} is thrown at validation, otherwise a {@link ValidationException} containing all
		 * the occurred validation exception is thrown.
		 * @param stopValidationAtFirstFailure <code>true</code> to stop validation at first validation failure
		 * @return this
		 */
		B stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure);

		/**
		 * Set whether to stop overall validation at first validation failure. If <code>true</code>, only the first
		 * {@link OverallValidationException} is thrown at validation, otherwise a {@link OverallValidationException}
		 * containing all the occurred validation exception is thrown.
		 * <p>
		 * The overall validation is the one which is performed using validators added with
		 * {@link #withValidator(Validator)} method.
		 * </p>
		 * @param stopOverallValidationAtFirstFailure <code>true</code> to stop overall validation at first validation
		 *        failure
		 * @return this
		 */
		B stopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure);

		/**
		 * Set whether to ignore any {@link Validator} bound to property {@link Input}s or directly to
		 * {@link PropertyInputGroup}. Default is <code>false</code>.
		 * @param ignoreValidation <code>true</code> to ignore any {@link Validator} bound to property inputs or
		 *        directly to {@link PropertyInputGroup}.
		 * @return this
		 */
		B ignoreValidation(boolean ignoreValidation);

		/**
		 * Set a default {@link ValidationErrorHandler} to use to handle {@link ValidationException} when validation is
		 * performed.
		 * <p>
		 * If a default {@link ValidationErrorHandler} is configured, the {@link PropertyInputGroup#validate()} will
		 * never throw any {@link ValidationException}, delegating validation error management to the default handler.
		 * </p>
		 * @param validationErrorHandler The handler to set
		 * @return this
		 */
		B defaultValidationErrorHandler(ValidationErrorHandler validationErrorHandler);

		/**
		 * Set whether to ignore properties without a bound {@link Input}. Default is <code>false</code>, and an
		 * exception is thrown if a property of the {@link PropertyInputGroup} cannot be bound to any input component.
		 * @param ignoreMissingInputs Whether to ignore when the {@link Input} for a property is missing
		 * @return this
		 */
		B ignoreMissingInputs(boolean ignoreMissingInputs);

		/**
		 * Add a {@link InputPostProcessor} to allow further {@link Input} configuration before the input is actually
		 * bound to a property.
		 * @param postProcessor the {@link InputPostProcessor} to add (not null)
		 * @return this
		 */
		B withInputPostProcessor(InputPostProcessor postProcessor);

		/**
		 * Build the {@link PropertyInputGroup}.
		 * @return a new {@link PropertyInputGroup} instance
		 */
		G build();

	}

	/**
	 * {@link ValidationException} extension to discern inputs validation and overall container validation exceptions.
	 */
	@SuppressWarnings("serial")
	public class OverallValidationException extends ValidationException {

		/**
		 * {@inheritDoc}
		 */
		public OverallValidationException(String message) {
			super(message);
		}

		/**
		 * {@inheritDoc}
		 */
		public OverallValidationException(String message, String messageCode, Object... messageArguments) {
			super(message, messageCode, messageArguments);
		}

		/**
		 * {@inheritDoc}
		 */
		public OverallValidationException(ValidationException... causes) {
			super(causes);
		}

	}

}
