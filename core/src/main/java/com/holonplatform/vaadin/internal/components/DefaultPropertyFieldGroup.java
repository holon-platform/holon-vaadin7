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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationErrorHandler;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.vaadin.components.FieldsValidator;
import com.holonplatform.vaadin.components.PropertyFieldGroup;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Field;

/**
 * Default {@link PropertyFieldGroup} implementation.
 *
 * @since 5.0.0
 */
public class DefaultPropertyFieldGroup implements PropertyFieldGroupConfigurator {

	private static final long serialVersionUID = -5441417959315472240L;

	/**
	 * Property set
	 */
	@SuppressWarnings("rawtypes")
	private final List<Property> properties = new LinkedList<>();

	/**
	 * Bound fields
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, Field> propertyFields = new HashMap<>();

	/**
	 * Custom FieldPropertyRenderers
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, FieldPropertyRenderer> propertyRenderers = new HashMap<>(8);

	/**
	 * Read-only properties
	 */
	@SuppressWarnings("rawtypes")
	private final List<Property> readOnlyProperties = new LinkedList<>();

	/**
	 * Required properties
	 */
	@SuppressWarnings("rawtypes")
	private final List<Property> requiredProperties = new LinkedList<>();

	/**
	 * Hidden properties
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, Object> hiddenProperties = new HashMap<>();

	/**
	 * Validators
	 */
	private final List<Validator<PropertyBox>> validators = new LinkedList<>();

	/**
	 * Property validators
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, List<Validator>> propertyValidators = new HashMap<>(8);

	/**
	 * Default value providers
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, DefaultValueProvider> defaultValues = new HashMap<>(4);

	/**
	 * Field configurators
	 */
	private final List<FieldConfigurator> fieldConfigurators = new LinkedList<>();

	/**
	 * Default {@link ValidationErrorHandler}
	 */
	private ValidationErrorHandler defaultValidationErrorHandler;

	/**
	 * Field validation behaviour
	 */
	private boolean stopFieldValidationAtFirstFailure = false;

	/**
	 * Overall validation behaviour
	 */
	private boolean stopOverallValidationAtFirstFailure = false;

	/**
	 * Ignore validation
	 */
	private boolean ignoreValidation = false;

	/**
	 * Ignore missing fields
	 */
	private boolean ignoreMissingField = false;

	/**
	 * Enabled status
	 */
	private boolean enabled = true;
	/**
	 * Read-only status
	 */
	private boolean readOnly = false;

	/**
	 * Constructor
	 */
	public DefaultPropertyFieldGroup() {
		super();
	}

	/**
	 * Add a property to the property set
	 * @param property Property to add
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void addProperty(Property property) {
		if (property != null) {
			properties.add(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator.ValidatorSupport#addValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public void addValidator(Validator<PropertyBox> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		validators.add(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator.ValidatorSupport#removeValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public void removeValidator(Validator<PropertyBox> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		validators.remove(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator.Validatable#getValidators()
	 */
	@Override
	public Collection<Validator<PropertyBox>> getValidators() {
		return Collections.unmodifiableList(validators);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#getFields()
	 */
	@Override
	public Iterable<Field<?>> getFields() {
		final List<Field<?>> fs = new ArrayList<>(propertyFields.size());
		properties.forEach(p -> propertyField(p).ifPresent(f -> fs.add(f)));
		return fs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#getField(com.holonplatform.core.property.Property)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<Field<T>> getField(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return Optional.ofNullable(propertyFields.get(property));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#stream()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Stream<Binding<T>> stream() {
		return propertyFields.entrySet().stream().map(e -> new PropertyFieldBinding<>(e.getKey(), e.getValue()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyFieldGroup#addValidator(com.holonplatform.core.property.Property,
	 * com.holonplatform.core.Validator)
	 */
	@Override
	public <T> void addValidator(Property<T> property, Validator<T> validator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getField(property).ifPresent(f -> f.addValidator(ValidationUtils.asVaadinValidator(validator)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#removeValidator(com.holonplatform.core.property.
	 * Property, com.holonplatform.core.Validator)
	 */
	@Override
	public <T> void removeValidator(Property<T> property, Validator<T> validator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getField(property).ifPresent(f -> ValidationUtils.removeValidator(f, validator));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyFieldGroup#setDefaultValueProvider(com.holonplatform.core.property.
	 * Property, com.holonplatform.vaadin.components.PropertyFieldGroup.DefaultValueProvider)
	 */
	@Override
	public <T> void setDefaultValueProvider(Property<T> property, DefaultValueProvider<T> defaultValueProvider) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(defaultValueProvider, "DefaultValueProvider must be not null");
		defaultValues.put(property, defaultValueProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#removeDefaultValueProvider(com.holonplatform.core.
	 * property.Property)
	 */
	@Override
	public <T> void removeDefaultValueProvider(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		defaultValues.remove(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#clear()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		try {
			properties.forEach(p -> {
				propertyField(p).ifPresent(f -> {
					f.clear();
					// check default value
					if (!p.isReadOnly() && defaultValues.containsKey(p)) {
						f.setValue(defaultValues.get(p).getDefaultValue(p));
					}
				});
			});
		} catch (@SuppressWarnings("unused") ValidationException | InvalidValueException e) {
			// ignore any validation error
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		// validate fields
		FieldsValidator.builder().defaultValidationErrorHandler(defaultValidationErrorHandler)
				.stopFieldValidationAtFirstFailure(stopFieldValidationAtFirstFailure).add(getValidatableFields())
				.build().validate();
		// validate value
		validate(getValue(false));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#validate(com.holonplatform.core.Validator.
	 * ValidationErrorHandler)
	 */
	@Override
	public boolean validate(ValidationErrorHandler handler) {
		ObjectUtils.argumentNotNull(handler, "ValidationErrorHandler must be not null");
		// validate fields
		if (!FieldsValidator.builder().defaultValidationErrorHandler(defaultValidationErrorHandler)
				.stopFieldValidationAtFirstFailure(stopFieldValidationAtFirstFailure).add(getValidatableFields())
				.build().validate(handler)) {
			return false;
		}
		// validate value
		try {
			validateValue(getValue(false));
		} catch (ValidationException e) {
			handler.handleValidationError(e);
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator.Validatable#validate(java.lang.Object)
	 */
	@Override
	public void validate(PropertyBox value) throws ValidationException {
		try {
			validateValue(value);
		} catch (ValidationException e) {
			if (!getDefaultValidationErrorHandler().isPresent()) {
				throw e;
			}
			getDefaultValidationErrorHandler().ifPresent(h -> h.handleValidationError(e));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#getValue(boolean)
	 */
	@Override
	public PropertyBox getValue(boolean validate) {
		PropertyBox value = PropertyBox.builder(properties).build();
		flush(value, validate);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#flush(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void flush(PropertyBox propertyBox, boolean validate) {
		ObjectUtils.argumentNotNull(propertyBox, "PropertyBox must be not null");

		if (validate) {
			// Fields validation
			FieldsValidator.builder().defaultValidationErrorHandler(defaultValidationErrorHandler)
					.stopFieldValidationAtFirstFailure(stopFieldValidationAtFirstFailure).add(getValidatableFields())
					.build().validate();
		}

		properties.forEach(p -> {
			if (isPropertyHidden(p)) {
				propertyBox.setValue(p, hiddenProperties.get(p));
			} else {
				propertyField(p).ifPresent(f -> {
					propertyBox.setValue(p, f.getValue());
				});
			}
		});

		if (validate) {
			// Overall validation
			validate(propertyBox);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyFieldGroup#setValue(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(final PropertyBox propertyBox, boolean validate) {
		hiddenProperties.forEach((p, v) -> hiddenProperties.put(p, null));
		if (propertyBox == null) {
			clear();
		} else {
			properties.forEach(p -> {
				if (isPropertyHidden(p)) {
					hiddenProperties.put(p, getPropertyValue(propertyBox, p));
				} else {
					propertyField(p).ifPresent(f -> {
						Object value = getPropertyValue(propertyBox, p);
						if (value != null) {
							// ignore read-only
							boolean ro = f.isReadOnly();
							if (ro)
								f.setReadOnly(false);
							f.setValue(value);
							if (ro)
								f.setReadOnly(true);
						} else {
							f.clear();
						}
					});
				}
			});
		}
		if (validate) {
			validate();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getFields().forEach(f -> f.setEnabled(enabled));
		this.enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		properties.forEach(p -> {
			propertyField(p).ifPresent(f -> {
				if (!p.isReadOnly() && !readOnlyProperties.contains(p)
						&& (f.getPropertyDataSource() == null || !f.getPropertyDataSource().isReadOnly())) {
					f.setReadOnly(readOnly);
				} else {
					f.setReadOnly(true);
				}
			});
		});

		this.readOnly = readOnly;
	}

	/**
	 * Check whether the given property was setted as read-only
	 * @param property Property to check
	 * @return <code>true</code> if read-only
	 */
	@SuppressWarnings("rawtypes")
	protected boolean isPropertyReadOnly(Property property) {
		return property != null && readOnlyProperties.contains(property);
	}

	/**
	 * Set a property as read-only
	 * @param property Property
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void setPropertyReadOnly(Property property) {
		if (property != null && !readOnlyProperties.contains(property)) {
			readOnlyProperties.add(property);
		}
	}

	/**
	 * Check whether the given property was setted as required
	 * @param property Property to check
	 * @return <code>true</code> if required
	 */
	@SuppressWarnings("rawtypes")
	protected boolean isPropertyRequired(Property property) {
		return property != null && requiredProperties.contains(property);
	}

	/**
	 * Set a property as required
	 * @param property Property
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void setPropertyRequired(Property property) {
		if (property != null && !requiredProperties.contains(property)) {
			requiredProperties.add(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.PropertyFieldGroupConfigurator#setPropertyHidden(com.holonplatform
	 * .core.property.Property)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void setPropertyHidden(Property property) {
		if (property != null && !hiddenProperties.containsKey(property)) {
			hiddenProperties.put(property, null);
		}
	}

	@SuppressWarnings("rawtypes")
	protected boolean isPropertyHidden(Property property) {
		return property != null && hiddenProperties.containsKey(property);
	}

	/**
	 * Get the default {@link ValidationErrorHandler} to use.
	 * @return the default ValidationErrorHandler
	 */
	public Optional<ValidationErrorHandler> getDefaultValidationErrorHandler() {
		return Optional.ofNullable(defaultValidationErrorHandler);
	}

	/**
	 * Set the default {@link ValidationErrorHandler} to use.
	 * @param defaultValidationErrorHandler the default ValidationErrorHandler to set
	 */
	@Override
	public void setDefaultValidationErrorHandler(ValidationErrorHandler defaultValidationErrorHandler) {
		this.defaultValidationErrorHandler = defaultValidationErrorHandler;
	}

	/**
	 * Get whether to stop field validation at first validation failure.
	 * @return whether to stop field validation at first validation failure
	 */
	protected boolean isStopFieldValidationAtFirstFailure() {
		return stopFieldValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop field validation at first validation failure.
	 * @param stopFieldValidationAtFirstFailure <code>true</code> to stop field validation at first validation failure
	 */
	@Override
	public void setStopFieldValidationAtFirstFailure(boolean stopFieldValidationAtFirstFailure) {
		this.stopFieldValidationAtFirstFailure = stopFieldValidationAtFirstFailure;
	}

	/**
	 * Get whether to stop overall validation at first validation failure.
	 * @return whether to stop overall validation at first validation failure
	 */
	protected boolean isStopOverallValidationAtFirstFailure() {
		return stopOverallValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop overall validation at first validation failure.
	 * @param stopOverallValidationAtFirstFailure <code>true</code> to stop overall validation at first validation
	 *        failure
	 */
	@Override
	public void setStopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure) {
		this.stopOverallValidationAtFirstFailure = stopOverallValidationAtFirstFailure;
	}

	/**
	 * Whether to ignore validation
	 * @return <code>true</code> if validation must be ignored
	 */
	public boolean isIgnoreValidation() {
		return ignoreValidation;
	}

	/**
	 * Set whether to ignore validation
	 * @param ignoreValidation <code>true</code> to ignore validation
	 */
	@Override
	public void setIgnoreValidation(boolean ignoreValidation) {
		this.ignoreValidation = ignoreValidation;
	}

	/**
	 * Whether to ignore missing property fields
	 * @return <code>true</code> if missing property fields must be ignored
	 */
	protected boolean isIgnoreMissingField() {
		return ignoreMissingField;
	}

	/**
	 * Set whether to ignore missing property fields
	 * @param ignoreMissingField <code>true</code> to ignore missing property fields
	 */
	@Override
	public void setIgnoreMissingField(boolean ignoreMissingField) {
		this.ignoreMissingField = ignoreMissingField;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyFieldGroupConfigurator#addFieldConfigurator(com.
	 * holonframework.vaadin.components.PropertyFieldGroup.FieldConfigurator)
	 */
	@Override
	public void addFieldConfigurator(FieldConfigurator fieldConfigurator) {
		if (fieldConfigurator != null) {
			fieldConfigurators.add(fieldConfigurator);
		}
	}

	/**
	 * Add a property validator
	 * @param <T> Property type
	 * @param property Property
	 * @param validator Validator to add
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public <T> void addPropertyValidator(Property<T> property, Validator<T> validator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		List<Validator> vs = propertyValidators.get(property);
		if (vs == null) {
			vs = new LinkedList<>();
			propertyValidators.put(property, vs);
		}
		vs.add(validator);
	}

	/**
	 * Set the {@link FieldPropertyRenderer} to use with given property
	 * @param <T> Property type
	 * @param property Property
	 * @param renderer Renderer
	 */
	@Override
	public <T> void setPropertyRenderer(Property<T> property, FieldPropertyRenderer<T> renderer) {
		if (property != null && renderer != null) {
			propertyRenderers.put(property, renderer);
		}
	}

	/**
	 * Build and bind {@link Field}s to the properties of the property set.
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void build() {
		propertyFields.clear();
		// render and bind fields
		properties.forEach(p -> {
			if (!isPropertyHidden(p)) {
				final Optional<Field> field = render(p);
				if (!field.isPresent() && !isIgnoreMissingField()) {
					throw new NoSuitableRendererAvailableException(
							"No Field render available to render the property [" + p.toString() + "] as a Field");
				}
				field.ifPresent(f -> {
					if (isIgnoreValidation()) {
						f.removeAllValidators();
					}
					// configure
					configureField(p, f);
					// bind
					propertyFields.put(p, f);
				});
			}
		});
	}

	/**
	 * Get the value of given <code>property</code> using given <code>propertyBox</code>.
	 * @param propertyBox PropertyBox
	 * @param property Property
	 * @return Property value
	 */
	@SuppressWarnings("unchecked")
	private <T> T getPropertyValue(PropertyBox propertyBox, Property<T> property) {
		if (VirtualProperty.class.isAssignableFrom(property.getClass())) {
			if (((VirtualProperty<T>) property).getValueProvider() != null) {
				return ((VirtualProperty<T>) property).getValueProvider().getPropertyValue(propertyBox);
			}
			return null;
		}
		if (propertyBox.containsValue(property)) {
			return propertyBox.getValue(property);
		}
		if (defaultValues.containsKey(property)) {
			return (T) defaultValues.get(property).getDefaultValue(property);
		}
		return null;
	}

	/**
	 * Render given property as a {@link Field}, using custom {@link FieldPropertyRenderer} or default
	 * {@link Property#render(Class)} method.
	 * @param <T> Property type
	 * @param property Property to render
	 * @return Rendered field
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> Optional<Field> render(Property<T> property) {
		// check custom renderer
		if (propertyRenderers.containsKey(property)) {
			final FieldPropertyRenderer<T> r = propertyRenderers.get(property);
			return Optional.ofNullable(r.render(property));
		}
		// use registry
		return property.renderIfAvailable(Field.class);
	}

	/**
	 * Configure Field before binding
	 * @param property Property
	 * @param field Field to configure
	 */
	@SuppressWarnings("unchecked")
	protected void configureField(final Property<?> property, @SuppressWarnings("rawtypes") final Field field) {
		// Property validators
		if (!isIgnoreValidation()) {
			property.getValidators().forEach(v -> {
				ValidationUtils.removeValidator(field, v);
				field.addValidator(ValidationUtils.asVaadinValidator(v));
			});
			@SuppressWarnings("rawtypes")
			List<Validator> pvs = propertyValidators.get(property);
			if (pvs != null) {
				pvs.forEach(v -> field.addValidator(ValidationUtils.asVaadinValidator(v)));
			}
		}
		// Read-only
		if (property.isReadOnly() || readOnlyProperties.contains(property)) {
			field.setReadOnly(true);
		}
		// Required
		if (requiredProperties.contains(property)) {
			field.setRequired(true);
		}
		// FieldConfigurators
		fieldConfigurators.forEach(fc -> fc.configureField(property, field));
		// check default value
		if (!property.isReadOnly() && defaultValues.containsKey(property)) {
			field.setValue(defaultValues.get(property).getDefaultValue(property));
		}
	}

	/**
	 * Get the {@link Field} bound to given property, if present
	 * @param property Property
	 * @return Property field
	 */
	@SuppressWarnings("rawtypes")
	private Optional<Field> propertyField(Property property) {
		return Optional.ofNullable(propertyFields.get(property));
	}

	/**
	 * Get all validatable fields
	 * @return Validatable fields, an empty list if none
	 */
	@SuppressWarnings("rawtypes")
	private List<Field> getValidatableFields() {
		final List<Field> fields = new LinkedList<>();
		properties.forEach(p -> {
			if (!p.isReadOnly() && !isPropertyReadOnly(p)) {
				propertyField(p).ifPresent(f -> fields.add(f));
			}
		});
		return fields;
	}

	/**
	 * Overall validation
	 * @param value Value to validate
	 * @throws OverallValidationException If validation fails
	 */
	private void validateValue(PropertyBox value) throws OverallValidationException {
		if (!isIgnoreValidation()) {

			LinkedList<ValidationException> failures = new LinkedList<>();
			for (Validator<PropertyBox> validator : getValidators()) {
				try {
					validator.validate(value);
				} catch (ValidationException ve) {
					failures.add(ve);
					if (isStopOverallValidationAtFirstFailure()) {
						break;
					}
				}
			}

			OverallValidationException ve = null;
			if (!failures.isEmpty()) {
				if (failures.size() == 1) {
					ve = new OverallValidationException(failures.getFirst().getMessage(),
							failures.getFirst().getMessageCode(), failures.getFirst().getMessageArguments());
				} else {
					ve = new OverallValidationException(failures.toArray(new ValidationException[failures.size()]));
				}
			}
			if (ve != null) {
				throw ve;
			}

		}
	}

	// Builder

	/**
	 * Default {@link PropertyFieldGroupBuilder} implementation.
	 */
	public static class DefaultBuilder
			extends AbstractBuilder<PropertyFieldGroupConfigurator, PropertyFieldGroup, PropertyFieldGroupBuilder>
			implements PropertyFieldGroupBuilder {

		public DefaultBuilder() {
			super(new DefaultPropertyFieldGroup());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.DefaultPropertyFieldGroup.AbstractBuilder#builder()
		 */
		@Override
		protected PropertyFieldGroupBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#build()
		 */
		@Override
		public PropertyFieldGroup build() {
			instance.build();
			return instance;
		}

	}

	/**
	 * Abstract {@link Builder} implementation.
	 * @param <G> Actual {@link PropertyFieldGroup} type
	 * @param <B> Concrete builder type
	 */
	public abstract static class AbstractBuilder<C extends PropertyFieldGroupConfigurator, G extends PropertyFieldGroup, B extends Builder<G, B>>
			implements Builder<G, B> {

		/**
		 * Instance to build
		 */
		protected final C instance;

		/**
		 * Constructor
		 * @param instance Instance to build
		 */
		public AbstractBuilder(C instance) {
			super();
			this.instance = instance;
		}

		/**
		 * Actual builder
		 * @return Builder
		 */
		protected abstract B builder();

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#properties(com.holonplatform.core.property.
		 * Property[])
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <P extends Property> B properties(P... properties) {
			if (properties != null) {
				for (P property : properties) {
					instance.addProperty(property);
				}
			}
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#properties(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> B properties(Iterable<P> properties) {
			ObjectUtils.argumentNotNull(properties, "Properties must be not null");
			for (P property : properties) {
				instance.addProperty(property);
			}
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#readOnly(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B readOnly(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyReadOnly(property);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#required(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B required(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyRequired(property);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#hidden(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B hidden(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyHidden(property);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#defaultValue(com.holonplatform.core.property
		 * .Property, com.holonplatform.vaadin.components.PropertyFieldGroup.DefaultValueProvider)
		 */
		@Override
		public <T> B defaultValue(Property<T> property, DefaultValueProvider<T> defaultValueProvider) {
			instance.setDefaultValueProvider(property, defaultValueProvider);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#withValidator(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> B withValidator(Property<T> property, Validator<T> validator) {
			instance.addPropertyValidator(property, validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#withValidator(com.holonplatform.core.
		 * Validator)
		 */
		@Override
		public B withValidator(Validator<PropertyBox> validator) {
			instance.addValidator(validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T, F extends T> B bind(Property<T> property, PropertyRenderer<Field<F>, T> renderer) {
			ObjectUtils.argumentNotNull(renderer, "PropertyRenderer must be not null");
			return bind(property, p -> renderer.render(p));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.vaadin.components.FieldPropertyRenderer)
		 */
		@Override
		public <T> B bind(Property<T> property, FieldPropertyRenderer<T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "PropertyRenderer must be not null");
			instance.setPropertyRenderer(property, renderer);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#defaultValidationErrorHandler(com.
		 * holonframework.core.Validator.ValidationErrorHandler)
		 */
		@Override
		public B defaultValidationErrorHandler(ValidationErrorHandler validationErrorHandler) {
			instance.setDefaultValidationErrorHandler(validationErrorHandler);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#stopFieldValidationAtFirstFailure(boolean)
		 */
		@Override
		public B stopFieldValidationAtFirstFailure(boolean stopFieldValidationAtFirstFailure) {
			instance.setStopFieldValidationAtFirstFailure(stopFieldValidationAtFirstFailure);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#setStopOverallValidationAtFirstFailure(
		 * boolean)
		 */
		@Override
		public B stopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure) {
			instance.setStopOverallValidationAtFirstFailure(stopOverallValidationAtFirstFailure);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#ignoreValidation(boolean)
		 */
		@Override
		public B ignoreValidation(boolean ignoreValidation) {
			instance.setIgnoreValidation(ignoreValidation);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#ignoreMissingFields(boolean)
		 */
		@Override
		public B ignoreMissingFields(boolean ignoreMissingField) {
			instance.setIgnoreMissingField(ignoreMissingField);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#withFieldConfigurator(com.holonplatform.
		 * vaadin.components.PropertyFieldGroup.FieldConfigurator)
		 */
		@Override
		public B withFieldConfigurator(FieldConfigurator fieldConfigurator) {
			ObjectUtils.argumentNotNull(fieldConfigurator, "FieldConfigurator must be not null");
			instance.addFieldConfigurator(fieldConfigurator);
			return builder();
		}

	}

	/**
	 * Default {@link Binding} implementation.
	 * @param <T> Property type
	 */
	private static class PropertyFieldBinding<T> implements Binding<T> {

		private final Property<T> property;
		private final Field<T> field;

		public PropertyFieldBinding(Property<T> property, Field<T> field) {
			super();
			this.property = property;
			this.field = field;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Binding#getProperty()
		 */
		@Override
		public Property<T> getProperty() {
			return property;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Binding#getField()
		 */
		@Override
		public Field<T> getField() {
			return field;
		}

	}

}
