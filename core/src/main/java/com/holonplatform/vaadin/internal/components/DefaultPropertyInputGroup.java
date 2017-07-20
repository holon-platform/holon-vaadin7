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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.PropertyBinding;
import com.holonplatform.vaadin.components.PropertyBinding.PostProcessor;
import com.holonplatform.vaadin.components.PropertyInputGroup;
import com.holonplatform.vaadin.components.PropertyValueComponentSource;
import com.holonplatform.vaadin.components.ValidationErrorHandler;
import com.holonplatform.vaadin.components.ValueComponent;
import com.vaadin.data.Validator.InvalidValueException;

/**
 * Default {@link PropertyInputGroup} implementation.
 *
 * @since 5.0.0
 */
public class DefaultPropertyInputGroup implements PropertyInputGroup, PropertyValueComponentSource {

	private static final long serialVersionUID = -5441417959315472240L;

	/**
	 * Property set
	 */
	@SuppressWarnings("rawtypes")
	private final List<Property> properties = new LinkedList<>();

	/**
	 * Inputs bindings
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, Input> propertyInputs = new HashMap<>();

	/**
	 * Custom PropertyRenderers
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, PropertyRenderer> propertyRenderers = new HashMap<>(8);

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
	 * Input post-processors
	 */
	private final List<PostProcessor<Input<?>>> postProcessors = new LinkedList<>();

	/**
	 * Default {@link ValidationErrorHandler}
	 */
	private ValidationErrorHandler defaultValidationErrorHandler;

	/**
	 * Validation behaviour
	 */
	private boolean stopValidationAtFirstFailure = false;

	/**
	 * Overall validation behaviour
	 */
	private boolean stopOverallValidationAtFirstFailure = false;

	/**
	 * Ignore validation
	 */
	private boolean ignoreValidation = false;

	/**
	 * Whether to ignore missing inputs
	 */
	private boolean ignoreMissingInputs = false;

	/**
	 * Constructor
	 */
	public DefaultPropertyInputGroup() {
		super();
	}

	/**
	 * Add a property to the property set
	 * @param property Property to add
	 */
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
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#getInputs()
	 */
	@Override
	public Iterable<Input<?>> getInputs() {
		return properties.stream().filter(p -> propertyInputs.containsKey(p)).map(p -> propertyInputs.get(p))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyInputContainer#getInput(com.holonplatform.core.property.Property)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<Input<T>> getInput(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return Optional.ofNullable(propertyInputs.get(property));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#stream()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Stream<PropertyBinding<T, Input<T>>> stream() {
		return propertyInputs.entrySet().stream().map(e -> PropertyBinding.create(e.getKey(), e.getValue()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#getValueComponents()
	 */
	@Override
	public Iterable<ValueComponent<?>> getValueComponents() {
		return properties.stream().filter(p -> propertyInputs.containsKey(p)).map(p -> propertyInputs.get(p))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#getValueComponent(com.holonplatform.core.
	 * property.Property)
	 */
	@Override
	public Optional<ValueComponent<?>> getValueComponent(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return Optional.ofNullable(propertyInputs.get(property));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#streamOfValueComponents()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Stream<PropertyBinding<T, ValueComponent<T>>> streamOfValueComponents() {
		return propertyInputs.entrySet().stream().map(e -> PropertyBinding.create(e.getKey(), e.getValue()));
	}

	@Override
	public <T> void addValidator(Property<T> property, Validator<T> validator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getInput(property).ifPresent(i -> i.addValidator(validator));
	}

	@Override
	public <T> void removeValidator(Property<T> property, Validator<T> validator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getInput(property).ifPresent(i -> i.removeValidator(validator));
	}

	@Override
	public <T> void setDefaultValueProvider(Property<T> property, DefaultValueProvider<T> defaultValueProvider) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(defaultValueProvider, "DefaultValueProvider must be not null");
		defaultValues.put(property, defaultValueProvider);
	}

	@Override
	public <T> void removeDefaultValueProvider(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		defaultValues.remove(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.InputGroup#clear()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		try {
			properties.forEach(p -> {
				_input(p).ifPresent(i -> {
					i.clear();
					// check default value
					if (!p.isReadOnly() && defaultValues.containsKey(p)) {
						i.setValue(defaultValues.get(p).getDefaultValue(p));
					}
				});
			});
		} catch (@SuppressWarnings("unused") ValidationException | InvalidValueException e) {
			// ignore any validation error
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableValue#validateValue()
	 */
	@Override
	public void validateValue() throws ValidationException {
		// validate inputs
		validateInputs();
		// validate value
		validate(getValue(false));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator.Validatable#validate(java.lang.Object)
	 */
	@Override
	public void validate(PropertyBox value) throws ValidationException {
		validateValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableValue#isValid(com.holonplatform.vaadin.components.
	 * ValidationErrorHandler)
	 */
	@Override
	public boolean isValid(ValidationErrorHandler handler) {
		try {
			validateValue();
		} catch (ValidationException e) {
			ValidationErrorHandler eh = (handler != null) ? handler : getDefaultValidationErrorHandler().orElse(null);
			if (eh != null) {
				eh.handleValidationError(e);
			}
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValue(boolean)
	 */
	@Override
	public PropertyBox getValue(boolean validate) {
		PropertyBox value = PropertyBox.builder(properties).build();
		flush(value, validate);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValueIfValid(com.holonplatform.vaadin.components.
	 * ValidationErrorHandler)
	 */
	@Override
	public Optional<PropertyBox> getValueIfValid(final ValidationErrorHandler errorHandler) {
		try {
			return Optional.of(getValue(true));
		} catch (ValidationException e) {
			ValidationErrorHandler eh = (errorHandler != null) ? errorHandler
					: getDefaultValidationErrorHandler().orElse(null);
			if (eh != null) {
				eh.handleValidationError(e);
			} else {
				e.printStackTrace();
			}
			return Optional.empty();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#flush(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void flush(PropertyBox propertyBox, boolean validate) {
		ObjectUtils.argumentNotNull(propertyBox, "PropertyBox must be not null");

		if (validate) {
			// inputs validation
			validateInputs();
		}

		properties.forEach(p -> {
			if (isPropertyHidden(p)) {
				propertyBox.setValue(p, hiddenProperties.get(p));
			} else {
				_input(p).ifPresent(i -> {
					propertyBox.setValue(p, i.getValue());
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
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setValue(com.holonplatform.core.property.PropertyBox,
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
					_input(p).ifPresent(i -> {
						Object value = getPropertyValue(propertyBox, p);
						if (value != null) {
							// ignore read-only
							boolean ro = i.isReadOnly();
							if (ro)
								i.setReadOnly(false);
							i.setValue(value);
							if (ro)
								i.setReadOnly(true);
						} else {
							i.clear();
						}
					});
				}
			});
		}
		if (validate) {
			validateValue();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getInputs().forEach(i -> i.getComponent().setEnabled(enabled));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		properties.forEach(p -> {
			_input(p).ifPresent(f -> {
				if (!p.isReadOnly() && !readOnlyProperties.contains(p)
				/* && (f.getPropertyDataSource() == null || !f.getPropertyDataSource().isReadOnly()) */) {
					f.setReadOnly(readOnly);
				} else {
					f.setReadOnly(true);
				}
			});
		});
	}

	/**
	 * Check whether the given property was setted as read-only
	 * @param property Property to check
	 * @return <code>true</code> if read-only
	 */
	@SuppressWarnings("rawtypes")
	protected boolean isPropertyReadOnly(Property property) {
		return property != null && (property.isReadOnly() || readOnlyProperties.contains(property));
	}

	/**
	 * Set a property as read-only
	 * @param property Property to set as read-only
	 */
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
	 * @param property Property to set as required
	 */
	@SuppressWarnings("rawtypes")
	public void setPropertyRequired(Property property) {
		if (property != null && !requiredProperties.contains(property)) {
			requiredProperties.add(property);
		}
	}

	/**
	 * Set a property as hidden in UI
	 * @param property Property to set as hidden
	 */
	@SuppressWarnings("rawtypes")
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
	public void setDefaultValidationErrorHandler(ValidationErrorHandler defaultValidationErrorHandler) {
		this.defaultValidationErrorHandler = defaultValidationErrorHandler;
	}

	/**
	 * Get whether to stop validation at first validation failure.
	 * @return whether to stop validation at first validation failure
	 */
	protected boolean isStopValidationAtFirstFailure() {
		return stopValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop validation at first validation failure.
	 * @param stopValidationAtFirstFailure <code>true</code> to stop validation at first validation failure
	 */
	public void setStopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
		this.stopValidationAtFirstFailure = stopValidationAtFirstFailure;
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
	public void setIgnoreValidation(boolean ignoreValidation) {
		this.ignoreValidation = ignoreValidation;
	}

	/**
	 * Whether to ignore missing property {@link Input}s.
	 * @return <code>true</code> if missing property inputs must be ignored
	 */
	protected boolean isIgnoreMissingInputs() {
		return ignoreMissingInputs;
	}

	/**
	 * Set whether to ignore missing property inputs
	 * @param ignoreMissingInputs <code>true</code> to ignore missing property inputs
	 */
	public void setIgnoreMissingInputs(boolean ignoreMissingInputs) {
		this.ignoreMissingInputs = ignoreMissingInputs;
	}

	/**
	 * Add an {@link Input} {@link PostProcessor}.
	 * @param postProcessor the post-processor to add
	 */
	public void addInputPostProcessor(PostProcessor<Input<?>> postProcessor) {
		ObjectUtils.argumentNotNull(postProcessor, "InputPostProcessor must be not null");
		postProcessors.add(postProcessor);
	}

	/**
	 * Add a property validator
	 * @param <T> Property type
	 * @param property Property
	 * @param validator Validator to add
	 */
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
	 * Set the {@link PropertyRenderer} to use with given property to obtain the property {@link Input} component.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param renderer Renderer
	 */
	public <T, F extends T> void setPropertyRenderer(Property<T> property, PropertyRenderer<Input<F>, T> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		if (renderer != null) {
			propertyRenderers.put(property, renderer);
		} else {
			propertyRenderers.remove(property);
		}
	}

	/**
	 * Build and bind {@link Input}s to the properties of the property set.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void build() {
		propertyInputs.clear();
		// render and bind inputs
		properties.forEach(p -> {
			if (!isPropertyHidden(p)) {
				final Optional<Input> input = render(p);
				if (!input.isPresent() && !isIgnoreMissingInputs()) {
					throw new NoSuitableRendererAvailableException(
							"No Input renderer available to render the property [" + p.toString() + "]");
				}
				input.ifPresent(f -> {
					if (isIgnoreValidation()) {
						// TODO
						// f.removeAllValidators();
					}
					// configure
					configureInput(p, f);
					// bind
					propertyInputs.put(p, f);
				});
			}
		});
	}

	/**
	 * Render given property as a {@link Input}.
	 * @param <T> Property type
	 * @param property Property to render
	 * @return Rendered input
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> Optional<Input> render(Property<T> property) {
		// check custom renderer
		if (propertyRenderers.containsKey(property)) {
			final PropertyRenderer r = propertyRenderers.get(property);
			// check render type
			if (!Input.class.isAssignableFrom(r.getRenderType())) {
				throw new IllegalStateException(
						"Renderer for property [" + property + "] is not of Input type: [" + r.getRenderType() + "]");
			}
			return Optional.ofNullable((Input) r.render(property));
		}
		// use registry
		return property.renderIfAvailable(Input.class);
	}

	/**
	 * Configure {@link Input} component before binding it to a {@link Property}.
	 * @param property Property
	 * @param input {@link Input} component to configure
	 */
	@SuppressWarnings("unchecked")
	protected void configureInput(final Property<?> property, @SuppressWarnings("rawtypes") final Input input) {
		// Validators
		if (!isIgnoreValidation()) {
			// property validators
			propertyValidators.getOrDefault(property, Collections.emptyList()).forEach(v -> input.addValidator(v));
		}
		// Read-only
		if (property.isReadOnly() || readOnlyProperties.contains(property)) {
			input.setReadOnly(true);
		}
		// Required
		if (requiredProperties.contains(property)) {
			input.setRequired(true);
		}
		// post processors
		postProcessors.forEach(fc -> fc.process(property, input));
		// check default value
		if (!property.isReadOnly() && defaultValues.containsKey(property)) {
			input.setValue(defaultValues.get(property).getDefaultValue(property));
		}
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

	/**
	 * Validate all the {@link Input}s.
	 * @throws ValidationException If one or more input is not valid
	 */
	private void validateInputs() throws ValidationException {
		final LinkedList<ValidationException> validationExceptions = new LinkedList<>();

		propertyInputs.entrySet().forEach(e -> {
			// exclude read-only properties
			if (!isPropertyReadOnly(e.getKey())) {
				try {
					// validate input
					e.getValue().validateValue();
				} catch (ValidationException ve) {
					if (isStopValidationAtFirstFailure()) {
						throw ve;
					}
					validationExceptions.add(ve);
				}
			}
		});

		// collect validation exceptions, if any
		if (!validationExceptions.isEmpty()) {
			if (validationExceptions.size() == 1) {
				throw validationExceptions.getFirst();
			} else {
				throw new ValidationException(validationExceptions.toArray(new ValidationException[0]));
			}
		}
	}

	/**
	 * Get the {@link Input} bound to given property, if present
	 * @param property Property
	 * @return Property {@link Input}
	 */
	@SuppressWarnings("rawtypes")
	private Optional<Input> _input(Property property) {
		return Optional.ofNullable(propertyInputs.get(property));
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

	// Builder

	/**
	 * {@link PropertyInputGroup} builder.
	 */
	static class InternalBuilder
			extends AbstractBuilder<DefaultPropertyInputGroup, DefaultPropertyInputGroup, InternalBuilder> {

		public InternalBuilder() {
			super(new DefaultPropertyInputGroup());
		}

		@Override
		protected InternalBuilder builder() {
			return this;
		}

		@Override
		public DefaultPropertyInputGroup build() {
			instance.build();
			return instance;
		}

	}

	/**
	 * Default {@link PropertyInputGroupBuilder} implementation.
	 */
	public static class DefaultBuilder
			extends AbstractBuilder<DefaultPropertyInputGroup, PropertyInputGroup, PropertyInputGroupBuilder>
			implements PropertyInputGroupBuilder {

		public DefaultBuilder() {
			super(new DefaultPropertyInputGroup());
		}

		@Override
		protected PropertyInputGroupBuilder builder() {
			return this;
		}

		@Override
		public PropertyInputGroup build() {
			instance.build();
			return instance;
		}

	}

	/**
	 * Abstract {@link Builder} implementation.
	 * @param <G> Actual {@link PropertyInputGroup} type
	 * @param <B> Concrete builder type
	 */
	public abstract static class AbstractBuilder<C extends DefaultPropertyInputGroup, G extends PropertyInputGroup, B extends Builder<G, B>>
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
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#properties(com.holonplatform.core.property.
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
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#properties(java.lang.Iterable)
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
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#readOnly(com.holonplatform.core.property.
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
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
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
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#hidden(com.holonplatform.core.property.
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
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#defaultValue(com.holonplatform.core.property
		 * .Property, com.holonplatform.vaadin.components.PropertyInputGroup.DefaultValueProvider)
		 */
		@Override
		public <T> B defaultValue(Property<T> property, DefaultValueProvider<T> defaultValueProvider) {
			instance.setDefaultValueProvider(property, defaultValueProvider);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> B withValidator(Property<T> property, Validator<T> validator) {
			instance.addPropertyValidator(property, validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * Validator)
		 */
		@Override
		public B withValidator(Validator<PropertyBox> validator) {
			instance.addValidator(validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#bind(com.holonplatform.core.property.Property,
		 * com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T, F extends T> B bind(Property<T> property, PropertyRenderer<Input<F>, T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "Renderer must be not null");
			instance.setPropertyRenderer(property, renderer);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#defaultValidationErrorHandler(com.
		 * holonframework.core.Validator.ValidationErrorHandler)
		 */
		@Override
		public B defaultValidationErrorHandler(ValidationErrorHandler validationErrorHandler) {
			instance.setDefaultValidationErrorHandler(validationErrorHandler);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#stopValidationAtFirstFailure(boolean)
		 */
		@Override
		public B stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
			instance.setStopValidationAtFirstFailure(stopValidationAtFirstFailure);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#setStopOverallValidationAtFirstFailure(
		 * boolean)
		 */
		@Override
		public B stopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure) {
			instance.setStopOverallValidationAtFirstFailure(stopOverallValidationAtFirstFailure);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#ignoreValidation(boolean)
		 */
		@Override
		public B ignoreValidation(boolean ignoreValidation) {
			instance.setIgnoreValidation(ignoreValidation);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#ignoreMissingInputs(boolean)
		 */
		@Override
		public B ignoreMissingInputs(boolean ignoreMissingInputs) {
			instance.setIgnoreMissingInputs(ignoreMissingInputs);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withPostProcessor(com.holonplatform.vaadin.
		 * components.PropertyBinding.PostProcessor)
		 */
		@Override
		public B withPostProcessor(PostProcessor<Input<?>> postProcessor) {
			ObjectUtils.argumentNotNull(postProcessor, "PostProcessor must be not null");
			instance.addInputPostProcessor(postProcessor);
			return builder();
		}

	}

}
