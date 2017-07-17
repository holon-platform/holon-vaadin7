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

import java.io.Serializable;

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
import com.holonplatform.vaadin.internal.components.DefaultPropertyFieldGroup;
import com.vaadin.ui.Field;

/**
 * Provides functionalities to build and manage a group of {@link Field}s bound to a {@link Property} set.
 * <p>
 * Supports overall {@link Validator}s registration to validate all the fields values, allowing cross field validation,
 * using a {@link PropertyBox} to represent the fields value set.
 * </p>
 * <p>
 * Fully supports field values loading and flushing using {@link PropertyBox} instances.
 * </p>
 * <p>
 * By default, property {@link Field} components are obtained from the {@link PropertyRenderer}s registered in the
 * context {@link PropertyRendererRegistry}, if available. Custom {@link PropertyRenderer} registration is supported to
 * provide a custom behaviour for specific properties.
 * </p>
 * <p>
 * Default property values are supported using a {@link DefaultValueProvider}. The property default values are loaded
 * when {@link #clear()} or {@link #setValue(PropertyBox)} methods are invoked.
 * </p>
 * <p>
 * Convenience methods {@link #setEnabled(boolean)} and {@link #setReadOnly(boolean)} can be used to change the enabled
 * / read-only state for all the property bound fields.
 * </p>
 * 
 * @since 5.0.0
 */
public interface PropertyFieldGroup
		extends Serializable, PropertyFieldContainer, ValidatorSupport<PropertyBox>, Validatable<PropertyBox> {

	/**
	 * Adds a {@link Validator} to the Field bound to given <code>property</code>.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param validator Validator to add (not null)
	 */
	<T> void addValidator(Property<T> property, Validator<T> validator);

	/**
	 * Removes a {@link Validator} from the Field bound to given <code>property</code>.
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
	 * Clear all the field values.
	 */
	void clear();

	/**
	 * Validate the {@link PropertyFieldGroup} invoking all bound Field validators using current Field values and all
	 * the global {@link PropertyFieldGroup} validators.
	 * @throws ValidationException If a field value is not valid, providing the validation error message.
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyFieldGroup} validators)
	 */
	void validate() throws ValidationException;

	/**
	 * Validate all the fields, using given {@link ValidationErrorHandler} to handle any {@link ValidationException}.
	 * <p>
	 * This method does not throw any {@link ValidationException}, delegating error management to given error handler.
	 * </p>
	 * @param handler Validation error handler (not null)
	 * @return <code>true</code> if all fields are valid, <code>false</code> otherwise
	 */
	boolean validate(ValidationErrorHandler handler);

	/**
	 * Checks the validity of the bound fields and of this {@link PropertyFieldGroup}.
	 * @return <code>true</code> if all bound fields and this {@link PropertyFieldGroup} are valid, <code>false</code>
	 *         otherwise.
	 */
	default boolean isValid() {
		try {
			validate();
		} catch (@SuppressWarnings("unused") ValidationException e) {
			return false;
		}
		return true;
	}

	/**
	 * Get the all the property fields values using a {@link PropertyBox}.
	 * <p>
	 * The {@link PropertyFieldGroup} property set is used as the {@link PropertyBox} property set.
	 * </p>
	 * @param validate <code>true</code> to check the validity of the bound fields and of this
	 *        {@link PropertyFieldGroup} before returing the value
	 * @return A {@link PropertyBox} containing the values of the properties of this {@link PropertyFieldGroup}
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and a field value is not valid
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyFieldGroup} validators)
	 */
	PropertyBox getValue(boolean validate);

	/**
	 * Get the all the property fields values using a {@link PropertyBox}, validating the bound fields and this
	 * {@link PropertyFieldGroup} before returing the value
	 * <p>
	 * The {@link PropertyFieldGroup} property set is used as the {@link PropertyBox} property set.
	 * </p>
	 * @return A {@link PropertyBox} containing the values of the properties of this {@link PropertyFieldGroup}
	 * @throws ValidationException If a field value is not valid, providing the validation error message.
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyFieldGroup} validators)
	 */
	default PropertyBox getValue() {
		return getValue(true);
	}

	/**
	 * Flush all the property fields values to given {@link PropertyBox}.
	 * @param propertyBox PropertyBox into which to write the values (not null)
	 * @param validate <code>true</code> to check the validity of the bound fields and of this
	 *        {@link PropertyFieldGroup} before writing the values
	 * @throws PropertyNotFoundException If a property to flush is not found in given {@link PropertyBox}
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and a field value is not valid
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyFieldGroup} validators)
	 */
	void flush(PropertyBox propertyBox, boolean validate);

	/**
	 * Flush all the property fields values to given {@link PropertyBox}, validating the bound fields and this
	 * {@link PropertyFieldGroup} before writing the values.
	 * @param propertyBox PropertyBox into which to write the values (not null)
	 * @throws PropertyNotFoundException If a property to flush is not found in given {@link PropertyBox}
	 * @throws ValidationException If a field value is not valid, providing the validation error message.
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyFieldGroup} validators)
	 */
	default void flush(PropertyBox propertyBox) {
		flush(propertyBox, true);
	}

	/**
	 * Load the values contained in given {@link PropertyBox} to the bound fields of this {@link PropertyFieldGroup}.
	 * <p>
	 * Only the properties of this {@link PropertyFieldGroup} property set are used to read the values from the
	 * {@link PropertyBox}.
	 * </p>
	 * @param propertyBox PropertyBox from which to read the values (not null)
	 * @param validate <code>true</code> to check the validity of the bound fields and of this
	 *        {@link PropertyFieldGroup} after setting the values
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and a field value is not valid
	 * @throws OverallValidationException If overall validation failed (using {@link PropertyFieldGroup} validators)
	 */
	void setValue(PropertyBox propertyBox, boolean validate);

	/**
	 * Load the values contained in given {@link PropertyBox} to the bound fields of this {@link PropertyFieldGroup},
	 * validating the bound fields and this {@link PropertyFieldGroup} after setting the values.
	 * <p>
	 * No validation is performed.
	 * </p>
	 * @param propertyBox PropertyBox from which to read the values. If <code>null</code>, all fields are cleared.
	 * @see #setValue(PropertyBox, boolean)
	 */
	default void setValue(PropertyBox propertyBox) {
		setValue(propertyBox, false);
	}

	/**
	 * Returns the enabled status for the fields.
	 * <p>
	 * Note that this will not accurately represent the enabled status of all fields if you change the enabled status of
	 * the fields through some other method than {@link #setEnabled(boolean)}.
	 * </p>
	 * @return <code>true</code> if the fields are enabled, <code>false</code> otherwise
	 */
	boolean isEnabled();

	/**
	 * Updates the enabled state of all bound fields.
	 * @param enabled <code>true</code> to enable all bound fields, <code>false</code> to disable them
	 */
	void setEnabled(boolean enabled);

	/**
	 * Returns the read-only status for the fields.
	 * <p>
	 * Note that this will not accurately represent the read only status of all fields if you change the read only
	 * status of the fields through some other method than {@link #setReadOnly(boolean)}.
	 * </p>
	 * @return <code>true</code> if the fields are set to read-only, <code>false</code> otherwise
	 */
	boolean isReadOnly();

	/**
	 * Updates the read-only state of all bound fields.
	 * @param readOnly <code>true</code> to set all bound fields as read-only, <code>false</code> otherwise
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * Get a {@link Builder} to create and setup a {@link PropertyFieldGroup}.
	 * @return {@link PropertyFieldGroup} builder
	 */
	static PropertyFieldGroupBuilder builder() {
		return new DefaultPropertyFieldGroup.DefaultBuilder();
	}

	// -------

	/**
	 * Represents a binding between a {@link Property} and its {@link Field} in a {@link PropertyFieldGroup}.
	 * @param <T> Property type
	 */
	public interface Binding<T> {

		/**
		 * Get the property
		 * @return The property
		 */
		Property<T> getProperty();

		/**
		 * Get the field to which the property is bound
		 * @return The field
		 */
		Field<T> getField();

	}

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
	 * Interface to allow further {@link Field} configuration after generation and before the field is actually bound to
	 * a property.
	 */
	@FunctionalInterface
	public interface FieldConfigurator {

		/**
		 * Configure the given {@link Field} before binding it to its property.
		 * @param property Property to which the field is bound
		 * @param field Field to configure
		 */
		void configureField(Property<?> property, Field<?> field);

	}

	/**
	 * A {@link PropertyRenderer} with a fixed {@link Field} rendering type.
	 * @param <T> Property type
	 */
	@SuppressWarnings("rawtypes")
	@FunctionalInterface
	public interface FieldPropertyRenderer<T> extends PropertyRenderer<Field, T> {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
		 */
		@Override
		default Class<? extends Field> getRenderType() {
			return Field.class;
		}

	}

	// Builder

	/**
	 * {@link PropertyFieldGroup} builder.
	 */
	public interface PropertyFieldGroupBuilder extends Builder<PropertyFieldGroup, PropertyFieldGroupBuilder> {

	}

	/**
	 * Base {@link PropertyFieldGroup} builder.
	 * @param <G> Actual {@link PropertyFieldGroup} type
	 * @param <B> Concrete builder type
	 */
	public interface Builder<G extends PropertyFieldGroup, B extends Builder<G, B>> {

		/**
		 * Add given properties to the {@link PropertyFieldGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add
		 * @return this
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		<P extends Property> B properties(P... properties);

		/**
		 * Add given properties to the {@link PropertyFieldGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		<P extends Property> B properties(Iterable<P> properties);

		/**
		 * Set the given property as read-only. If a property is read-only, the Field bound to the property will be
		 * setted as read-only too, and its value will never be written to a {@link PropertyBox} nor validated.
		 * @param <T> Property type
		 * @param property Property to set as read-only (not null)
		 * @return this
		 */
		<T> B readOnly(Property<T> property);

		/**
		 * Set the given property as required. If a property is required, the Field bound to the property will be setted
		 * as required, and its validation will fail when empty.
		 * @param <T> Property type
		 * @param property Property to set as required (not null)
		 * @return this
		 */
		<T> B required(Property<T> property);

		/**
		 * Set the given property as hidden. If a property is hidden, the Field bound to the property will never be
		 * generated, but its value will be written to a {@link PropertyBox} using {@link PropertyFieldGroup#getValue()}
		 * or {@link PropertyFieldGroup#flush(PropertyBox)}.
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
		 * Adds a {@link Validator} to the Field bound to given <code>property</code>.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		<T> B withValidator(Property<T> property, Validator<T> validator);

		/**
		 * Adds a {@link Validator} to the {@link PropertyFieldGroup}, using a {@link PropertyBox} to provide the
		 * property values to validate.
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		B withValidator(Validator<PropertyBox> validator);

		/**
		 * Set a specific {@link PropertyRenderer} to use to render the {@link Field} to bind to given
		 * <code>property</code>.
		 * @param <T> Property type
		 * @param <F> Rendered Field type
		 * @param property Property (not null)
		 * @param renderer Property renderer (not null)
		 * @return this
		 */
		<T, F extends T> B bind(Property<T> property, PropertyRenderer<Field<F>, T> renderer);

		/**
		 * Convenience method to set a specific {@link PropertyRenderer} to use to render the {@link Field} to bind to
		 * given <code>property</code> using the {@link FieldPropertyRenderer} functional interface.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param renderer Property renderer (not null)
		 * @return this
		 */
		<T> B bind(Property<T> property, FieldPropertyRenderer<T> renderer);

		/**
		 * Bind the given <code>property</code> to given <code>field</code> instance. If the property was already bound
		 * to a Field, the old Field will be replaced by the new Field.
		 * <p>
		 * This method also adds property validators to given Field when applicable.
		 * </p>
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param field Field to bind (not null)
		 * @return this
		 */
		default <T> B bind(Property<T> property, Field<? extends T> field) {
			ObjectUtils.argumentNotNull(field, "Field must be not null");
			return bind(property, p -> field);
		}

		/**
		 * Set whether to stop fields validation at first validation failure. If <code>true</code>, only the first
		 * {@link ValidationException} is thrown at validation, otherwise a {@link ValidationException} containing all
		 * the occurred validation exception is thrown.
		 * @param stopFieldValidationAtFirstFailure <code>true</code> to stop field validation at first validation
		 *        failure
		 * @return this
		 */
		B stopFieldValidationAtFirstFailure(boolean stopFieldValidationAtFirstFailure);

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
		 * Set whether to ignore any {@link Validator} bound to property fields or directly to
		 * {@link PropertyFieldGroup}. Default is <code>false</code>.
		 * @param ignoreValidation <code>true</code> to ignore any {@link Validator} bound to property fields or
		 *        directly to {@link PropertyFieldGroup}.
		 * @return this
		 */
		B ignoreValidation(boolean ignoreValidation);

		/**
		 * Set a default {@link ValidationErrorHandler} to use to handle {@link ValidationException} when validation is
		 * performed.
		 * <p>
		 * If a default {@link ValidationErrorHandler} is configured, the {@link PropertyFieldGroup#validate()} will
		 * never throw any {@link ValidationException}, delegating validation error management to the default handler.
		 * </p>
		 * @param validationErrorHandler The handler to set
		 * @return this
		 */
		B defaultValidationErrorHandler(ValidationErrorHandler validationErrorHandler);

		/**
		 * Set whether to ignore properties without a bound {@link Field}. Default is <code>false</code>, and an
		 * exception is thrown if a property of the {@link PropertyFieldGroup} cannot be bound to any field.
		 * @param ignoreMissingField Whether to ignore when the Field for a property is missing
		 * @return this
		 */
		B ignoreMissingFields(boolean ignoreMissingField);

		/**
		 * Add a {@link FieldConfigurator} to allow further {@link Field} configuration after generation and before the
		 * field is actually bound to a property.
		 * @param fieldConfigurator {@link FieldConfigurator} to add (not null)
		 * @return this
		 */
		B withFieldConfigurator(FieldConfigurator fieldConfigurator);

		/**
		 * Build the {@link PropertyFieldGroup}
		 * @return PropertyFieldGroup instance
		 */
		G build();

	}

	/**
	 * {@link ValidationException} extension to discern {@link Field}'s validation and overall container validation
	 * exceptions.
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
