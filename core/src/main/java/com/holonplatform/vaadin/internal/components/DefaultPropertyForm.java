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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.holonplatform.core.Path;
import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.PropertyForm;
import com.holonplatform.vaadin.components.PropertyInputGroup;
import com.holonplatform.vaadin.components.PropertyInputGroup.InputPostProcessor;
import com.holonplatform.vaadin.components.ValidationErrorHandler;
import com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Panel;

/**
 * Default {@link PropertyForm} implementation.
 * 
 * @param <C> Content component type.
 * 
 * @since 5.0.0
 */
public class DefaultPropertyForm<C extends Component> extends Panel implements PropertyForm, InputPostProcessor {

	private static final long serialVersionUID = 6071630379695045884L;

	/**
	 * Backing field group
	 */
	private PropertyInputGroup fieldGroup;

	/**
	 * Form content initializer
	 */
	private Consumer<C> initializer;

	/**
	 * Composer
	 */
	private Composer<? super C> composer;

	/**
	 * Compose on attach behaviour
	 */
	private boolean composeOnAttach = true;

	/**
	 * Composition state
	 */
	private boolean composed = false;

	/**
	 * Custom property captions
	 */
	private Map<Property<?>, Localizable> propertyCaptions = new HashMap<>(8);

	/**
	 * Hidden property captions
	 */
	private Collection<Property<?>> hiddenPropertyCaptions = new HashSet<>(8);

	/**
	 * Constructor
	 */
	public DefaultPropertyForm() {
		this(null);
	}

	/**
	 * Constructor with form content
	 * @param content Form composition content
	 */
	public DefaultPropertyForm(C content) {
		super();
		if (content != null) {
			setContent(content);
		}
		// default style name
		addStyleName("h-propertyform");
	}

	/**
	 * Sets the backing field group
	 * @param fieldGroup the fieldGroup to set
	 */
	protected void setFieldGroup(PropertyInputGroup fieldGroup) {
		this.fieldGroup = fieldGroup;
	}

	/**
	 * Gets the backing field group
	 * @return the fieldGroup
	 */
	protected PropertyInputGroup getFieldGroup() {
		return fieldGroup;
	}

	/**
	 * Get the form content initializer
	 * @return the form content initializer
	 */
	public Optional<Consumer<C>> getInitializer() {
		return Optional.ofNullable(initializer);
	}

	/**
	 * Set the form content initializer
	 * @param initializer the initializer to set
	 */
	public void setInitializer(Consumer<C> initializer) {
		this.initializer = initializer;
	}

	/**
	 * Get the fields composer
	 * @return the composer
	 */
	public Composer<? super C> getComposer() {
		return composer;
	}

	/**
	 * Set the fields compsoer
	 * @param composer the composer to set
	 */
	public void setComposer(Composer<? super C> composer) {
		this.composer = composer;
	}

	/**
	 * Gets whether the form must be composed on {@link #attach()}, if not already composed invoking {@link #compose()}.
	 * @return <code>true</code> if the form must be composed on {@link #attach()}
	 */
	public boolean isComposeOnAttach() {
		return composeOnAttach;
	}

	/**
	 * Sets whether the form must be composed on {@link #attach()}, if not already composed invoking {@link #compose()}.
	 * @param composeOnAttach <code>true</code> to compose the form on {@link #attach()}
	 */
	public void setComposeOnAttach(boolean composeOnAttach) {
		this.composeOnAttach = composeOnAttach;
	}

	/**
	 * Set the caption for the {@link Field}s bound to given property
	 * @param property Property
	 * @param caption Localizable caption
	 */
	protected void setPropertyCaption(Property<?> property, Localizable caption) {
		if (property != null && caption != null) {
			propertyCaptions.put(property, caption);
		}
	}

	/**
	 * Set the caption for the {@link Field}s bound to given property as hidden
	 * @param property Property
	 */
	protected void hidePropertyCaption(Property<?> property) {
		if (property != null) {
			hiddenPropertyCaptions.add(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyForm#compose()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void compose() {
		if (getContent() == null) {
			throw new IllegalStateException("Missing form content");
		}
		if (getComposer() == null) {
			throw new IllegalStateException("Missing form composer");
		}

		C content;
		try {
			content = (C) getContent();
		} catch (Exception e) {
			throw new IllegalStateException("Form content is not of expected type", e);
		}

		// init form content
		getInitializer().ifPresent(i -> i.accept(content));

		// compose
		getComposer().compose(content, getFieldGroup());

		this.composed = true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyInputGroup.InputPostProcessor#process(com.holonplatform.core.property
	 * .Property, com.holonplatform.vaadin.components.Input)
	 */
	@Override
	public void process(Property<?> property, Input<?> input) {
		// caption
		final Component inputComponent = input.getComponent();
		if (inputComponent != null) {
			if (hiddenPropertyCaptions.contains(property)) {
				inputComponent.setCaption(null);
			} else {
				if (propertyCaptions.containsKey(property)) {
					inputComponent.setCaption(LocalizationContext.translate(propertyCaptions.get(property), true));
				} else {
					if (inputComponent.getCaption() == null) {
						if (Path.class.isAssignableFrom(property.getClass())) {
							inputComponent.setCaption(((Path<?>) property).getName());
						} else {
							inputComponent.setCaption(property.toString());
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#attach()
	 */
	@Override
	public void attach() {
		super.attach();

		// check compose on attach
		if (!composed) {
			compose();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyFieldGroup#addValidator(com.holonplatform.core.property.Property,
	 * com.holonplatform.core.Validator)
	 */
	@Override
	public <T> void addValidator(Property<T> property, Validator<T> validator) {
		getFieldGroup().addValidator(property, validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#removeValidator(com.holonplatform.core.property.
	 * Property, com.holonplatform.core.Validator)
	 */
	@Override
	public <T> void removeValidator(Property<T> property, Validator<T> validator) {
		getFieldGroup().removeValidator(property, validator);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyFieldGroup#setDefaultValueProvider(com.holonplatform.core.property.
	 * Property, com.holonplatform.vaadin.components.PropertyFieldGroup.DefaultValueProvider)
	 */
	@Override
	public <T> void setDefaultValueProvider(Property<T> property, DefaultValueProvider<T> defaultValueProvider) {
		getFieldGroup().setDefaultValueProvider(property, defaultValueProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#removeDefaultValueProvider(com.holonplatform.core.
	 * property.Property)
	 */
	@Override
	public <T> void removeDefaultValueProvider(Property<T> property) {
		getFieldGroup().removeDefaultValueProvider(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#clear()
	 */
	@Override
	public void clear() {
		getFieldGroup().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableValue#validateValue()
	 */
	@Override
	public void validateValue() throws ValidationException {
		getFieldGroup().validateValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#getValue(boolean)
	 */
	@Override
	public PropertyBox getValue(boolean validate) {
		return getFieldGroup().getValue(validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#flush(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@Override
	public void flush(PropertyBox propertyBox, boolean validate) {
		getFieldGroup().flush(propertyBox, validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldGroup#setValue(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@Override
	public void setValue(PropertyBox propertyBox, boolean validate) {
		getFieldGroup().setValue(propertyBox, validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldContainer#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return getFieldGroup().getProperties();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#getInputs()
	 */
	@Override
	public Iterable<Input<?>> getInputs() {
		return getFieldGroup().getInputs();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyInputContainer#getInput(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<Input<T>> getInput(Property<T> property) {
		return getFieldGroup().getInput(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyFieldContainer#stream()
	 */
	@Override
	public <T> Stream<Binding<T>> stream() {
		return getFieldGroup().stream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator.ValidatorSupport#addValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public void addValidator(Validator<PropertyBox> validator) {
		getFieldGroup().addValidator(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator.ValidatorSupport#removeValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public void removeValidator(Validator<PropertyBox> validator) {
		getFieldGroup().removeValidator(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator.Validatable#getValidators()
	 */
	@Override
	public Collection<Validator<PropertyBox>> getValidators() {
		return getFieldGroup().getValidators();
	}

	// Builder

	/**
	 * Default {@link PropertyFormBuilder}.
	 * @param <C> Content type
	 */
	public static class DefaultBuilder<C extends Component>
			extends AbstractComponentBuilder<PropertyForm, DefaultPropertyForm<C>, PropertyFormBuilder<C>>
			implements PropertyFormBuilder<C> {

		private final PropertyInputGroup.PropertyInputGroupBuilder fieldGroupBuilder;

		/**
		 * Constructor
		 * @param content Form composition content
		 */
		public DefaultBuilder(C content) {
			super(new DefaultPropertyForm<>(content));
			this.fieldGroupBuilder = PropertyInputGroup.builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#properties(com.holonplatform.core.property.
		 * Property[])
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <P extends Property> PropertyFormBuilder<C> properties(P... properties) {
			fieldGroupBuilder.properties(properties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#properties(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> PropertyFormBuilder<C> properties(Iterable<P> properties) {
			fieldGroupBuilder.properties(properties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#readOnly(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyFormBuilder<C> readOnly(Property<T> property) {
			fieldGroupBuilder.readOnly(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#required(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyFormBuilder<C> required(Property<T> property) {
			fieldGroupBuilder.required(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#hidden(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyFormBuilder<C> hidden(Property<T> property) {
			fieldGroupBuilder.hidden(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#defaultValue(com.holonplatform.core.property
		 * .Property, com.holonplatform.vaadin.components.PropertyFieldGroup.DefaultValueProvider)
		 */
		@Override
		public <T> PropertyFormBuilder<C> defaultValue(Property<T> property,
				DefaultValueProvider<T> defaultValueProvider) {
			fieldGroupBuilder.defaultValue(property, defaultValueProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#withValidator(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> PropertyFormBuilder<C> withValidator(Property<T> property, Validator<T> validator) {
			fieldGroupBuilder.withValidator(property, validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#withValidator(com.holonplatform.core.
		 * Validator)
		 */
		@Override
		public PropertyFormBuilder<C> withValidator(Validator<PropertyBox> validator) {
			fieldGroupBuilder.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#bind(com.holonplatform.core.property.Property,
		 * com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T, F extends T> PropertyFormBuilder<C> bind(Property<T> property,
				PropertyRenderer<Input<F>, T> renderer) {
			fieldGroupBuilder.bind(property, renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#stopFieldValidationAtFirstFailure(boolean)
		 */
		@Override
		public PropertyFormBuilder<C> stopValidationAtFirstFailure(boolean stopFieldValidationAtFirstFailure) {
			fieldGroupBuilder.stopValidationAtFirstFailure(stopFieldValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#stopOverallValidationAtFirstFailure(boolean)
		 */
		@Override
		public PropertyFormBuilder<C> stopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure) {
			fieldGroupBuilder.stopOverallValidationAtFirstFailure(stopOverallValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#ignoreValidation(boolean)
		 */
		@Override
		public PropertyFormBuilder<C> ignoreValidation(boolean ignoreValidation) {
			fieldGroupBuilder.ignoreValidation(ignoreValidation);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#defaultValidationErrorHandler(com.
		 * holonframework.core.Validator.ValidationErrorHandler)
		 */
		@Override
		public PropertyFormBuilder<C> defaultValidationErrorHandler(ValidationErrorHandler validationErrorHandler) {
			fieldGroupBuilder.defaultValidationErrorHandler(validationErrorHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyFieldGroup.Builder#ignoreMissingFields(boolean)
		 */
		@Override
		public PropertyFormBuilder<C> ignoreMissingInputs(boolean ignoreMissingField) {
			fieldGroupBuilder.ignoreMissingInputs(ignoreMissingField);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withInputPostProcessor(com.holonplatform.
		 * vaadin.components.PropertyInputGroup.InputPostProcessor)
		 */
		@Override
		public PropertyFormBuilder<C> withInputPostProcessor(InputPostProcessor postProcessor) {
			fieldGroupBuilder.withInputPostProcessor(postProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#initializer(java.util.function.
		 * Consumer)
		 */
		@Override
		public PropertyFormBuilder<C> initializer(Consumer<C> initializer) {
			ObjectUtils.argumentNotNull(initializer, "Form content initializer must be not null");
			getInstance().setInitializer(initializer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#composer(com.holonplatform.vaadin.
		 * components.PropertyForm.Composer)
		 */
		@Override
		public com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder<C> composer(
				com.holonplatform.vaadin.components.PropertyForm.Composer<? super C> composer) {
			ObjectUtils.argumentNotNull(composer, "Composer must be not null");
			getInstance().setComposer(composer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#composeOnAttach(boolean)
		 */
		@Override
		public PropertyFormBuilder<C> composeOnAttach(boolean composeOnAttach) {
			getInstance().setComposeOnAttach(composeOnAttach);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#propertyCaption(com.holonplatform.core
		 * .property.Property, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public PropertyFormBuilder<C> propertyCaption(Property<?> property, Localizable caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(caption, "Caption must be not null");
			getInstance().setPropertyCaption(property, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#propertyCaption(com.holonplatform.core
		 * .property.Property, java.lang.String)
		 */
		@Override
		public PropertyFormBuilder<C> propertyCaption(Property<?> property, String caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().setPropertyCaption(property, Localizable.builder().message(caption).build());
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#propertyCaption(com.holonplatform.core
		 * .property.Property, java.lang.String, java.lang.String, java.lang.Object[])
		 */
		@Override
		public PropertyFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption, String messageCode,
				Object... arguments) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().setPropertyCaption(property, Localizable.builder().message(defaultCaption)
					.messageCode(messageCode).messageArguments(arguments).build());
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#hidePropertyCaption(com.holonplatform.
		 * core.property.Property)
		 */
		@Override
		public PropertyFormBuilder<C> hidePropertyCaption(Property<?> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().hidePropertyCaption(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder<C> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
		 * AbstractComponent)
		 */
		@Override
		protected PropertyForm build(DefaultPropertyForm<C> instance) {
			instance.setFieldGroup(fieldGroupBuilder.withInputPostProcessor(instance).build());
			return instance;
		}

	}

}
