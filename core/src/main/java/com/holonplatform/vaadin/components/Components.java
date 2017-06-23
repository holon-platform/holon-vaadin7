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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.mockito.internal.util.reflection.Fields;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.components.Dialog.DialogBuilder;
import com.holonplatform.vaadin.components.Dialog.QuestionDialogBuilder;
import com.holonplatform.vaadin.components.PropertyFieldGroup.PropertyFieldGroupBuilder;
import com.holonplatform.vaadin.components.PropertyForm.Composer;
import com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder;
import com.holonplatform.vaadin.components.builders.BooleanFieldBuilder;
import com.holonplatform.vaadin.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.components.builders.ClickableLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.components.builders.CssLayoutBuilder;
import com.holonplatform.vaadin.components.builders.DateFieldBuilder;
import com.holonplatform.vaadin.components.builders.FieldBuilder;
import com.holonplatform.vaadin.components.builders.FieldConfigurator;
import com.holonplatform.vaadin.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.components.builders.GridLayoutBuilder;
import com.holonplatform.vaadin.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.components.builders.LabelBuilder;
import com.holonplatform.vaadin.components.builders.LayoutConfigurator;
import com.holonplatform.vaadin.components.builders.MultiSelectFieldBuilder;
import com.holonplatform.vaadin.components.builders.NumberFieldBuilder;
import com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.PanelBuilder;
import com.holonplatform.vaadin.components.builders.SecretFieldBuilder;
import com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder;
import com.holonplatform.vaadin.components.builders.StringFieldBuilder;
import com.holonplatform.vaadin.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectFieldBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.components.builders.ClickableLayoutConfigurator.BaseClickableLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.ComponentConfigurator.BaseComponentConfigurator;
import com.holonplatform.vaadin.components.builders.FieldConfigurator.BaseFieldConfigurator;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.TableItemListingBuilder;
import com.holonplatform.vaadin.components.builders.LayoutConfigurator.BaseLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator.BaseOrderedLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.TablePropertyListingBuilder;
import com.holonplatform.vaadin.components.builders.TemporalFieldBuilder.TemporalWithTimeFieldBuilder;
import com.holonplatform.vaadin.components.builders.TemporalFieldBuilder.TemporalWithoutTimeFieldBuilder;
import com.holonplatform.vaadin.internal.components.BooleanField;
import com.holonplatform.vaadin.internal.components.DateField;
import com.holonplatform.vaadin.internal.components.Filler;
import com.holonplatform.vaadin.internal.components.InlineDateField;
import com.holonplatform.vaadin.internal.components.LocalDateField;
import com.holonplatform.vaadin.internal.components.LocalDateTimeField;
import com.holonplatform.vaadin.internal.components.NumberField;
import com.holonplatform.vaadin.internal.components.SecretField;
import com.holonplatform.vaadin.internal.components.StringArea;
import com.holonplatform.vaadin.internal.components.StringField;
import com.holonplatform.vaadin.internal.components.builders.DefaultButtonBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultButtonConfigurator;
import com.holonplatform.vaadin.internal.components.builders.DefaultClickableLayoutConfigurator;
import com.holonplatform.vaadin.internal.components.builders.DefaultComponentConfigurator;
import com.holonplatform.vaadin.internal.components.builders.DefaultCssLayoutBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultFieldConfigurator;
import com.holonplatform.vaadin.internal.components.builders.DefaultFormLayoutBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultGridItemListingBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultGridLayoutBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultGridPropertyListingBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultHorizontalLayoutBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultLabelBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultLayoutConfigurator;
import com.holonplatform.vaadin.internal.components.builders.DefaultOrderedLayoutConfigurator;
import com.holonplatform.vaadin.internal.components.builders.DefaultPanelBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultTableItemListingBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultTablePropertyListingBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultVerticalLayoutBuilder;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * Main provider of {@link Component} builders and configurators.
 * 
 * <p>
 * Provide static methods to obtain builder for common UI components type, allowing fluent and implementation-agnostic
 * components creation and configuration.
 * </p>
 * 
 * @since 5.0.0
 */
public final class Components implements Serializable {

	private static final long serialVersionUID = -2338969677506089465L;

	/**
	 * Default validation error message for required fields.
	 * <p>
	 * This message is used as the default required error from {@link FieldBuilder}.
	 * </p>
	 */
	public static final Localizable DEFAULT_REQUIRED_ERROR = Localizable.builder().message("Value is required")
			.messageCode(Validator.DEFAULT_MESSAGE_CODE_PREFIX + "required").build();

	/**
	 * Default style class name for invalid Fields
	 */
	public static final String DEFAULT_INVALID_FIELD_STYLE_CLASS = "error";

	/*
	 * Empty private constructor: this class is intended only to provide constants ad utility methods.
	 */
	private Components() {
	}

	// Configurators

	/**
	 * Get a {@link ComponentConfigurator} to configure given component.
	 * @param component Component to configure (not null)
	 * @return BaseComponentConfigurator
	 */
	public static BaseComponentConfigurator configure(AbstractComponent component) {
		return new DefaultComponentConfigurator(component);
	}

	/**
	 * Get a {@link ButtonConfigurator} to configure given button.
	 * @param button Button to configure (not null)
	 * @return BaseButtonConfigurator
	 */
	public static BaseButtonConfigurator configure(Button button) {
		return new DefaultButtonConfigurator(button);
	}

	/**
	 * Get a {@link FieldConfigurator} to configure given field.
	 * @param <T> Field type
	 * @param field Field to configure (not null)
	 * @return BaseFieldConfigurator
	 */
	public static <T> BaseFieldConfigurator<T> configure(AbstractField<T> field) {
		return new DefaultFieldConfigurator<>(field);
	}

	/**
	 * Get a {@link OrderedLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	public static BaseOrderedLayoutConfigurator configure(VerticalLayout layout) {
		return new DefaultOrderedLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link OrderedLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	public static BaseOrderedLayoutConfigurator configure(HorizontalLayout layout) {
		return new DefaultOrderedLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link OrderedLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	public static BaseOrderedLayoutConfigurator configure(FormLayout layout) {
		return new DefaultOrderedLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link LayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	public static BaseLayoutConfigurator configure(GridLayout layout) {
		return new DefaultLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link ClickableLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	public static BaseClickableLayoutConfigurator configure(CssLayout layout) {
		return new DefaultClickableLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link ClickableLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	public static BaseClickableLayoutConfigurator configure(AbsoluteLayout layout) {
		return new DefaultClickableLayoutConfigurator<>(layout);
	}

	// Builders

	/**
	 * Build a filler component, i.e. a {@link Label} with undefined size and the HTML entity <code>&nbsp;</code> as
	 * content, which can be used with full expand ratio as a space filler in layouts.
	 * @return Filler
	 */
	public static Component filler() {
		return new Filler();
	}

	/**
	 * Gets a builder to create {@link Label}s.
	 * @return Label builder
	 */
	public static LabelBuilder label() {
		return new DefaultLabelBuilder();
	}

	/**
	 * Gets a builder to create {@link Button}s.
	 * @return Button builder
	 */
	public static ButtonBuilder button() {
		return button(false);
	}

	/**
	 * Gets a builder to create {@link Button}s
	 * @param nativeMode <code>true</code> to create a "native" button, i.e. implemented using the native button of web
	 *        browsers, using the HTML <code>&lt;button&gt;</code> element.
	 * @return Button builder
	 */
	public static ButtonBuilder button(boolean nativeMode) {
		return new DefaultButtonBuilder(nativeMode);
	}

	/**
	 * Gets a builder to create {@link CssLayout}s.
	 * @return CssLayout builder
	 */
	public static CssLayoutBuilder cssLayout() {
		return new DefaultCssLayoutBuilder();
	}

	/**
	 * Gets a builder to create {@link FormLayout}s.
	 * @return FormLayout builder
	 */
	public static FormLayoutBuilder formLayout() {
		return new DefaultFormLayoutBuilder();
	}

	/**
	 * Gets a builder to create {@link FormLayout}s.
	 * @return FormLayout builder
	 */
	public static GridLayoutBuilder gridLayout() {
		return gridLayout(1, 1);
	}

	/**
	 * Gets a builder to create {@link GridLayout}s.
	 * @param columns Initial number of columns
	 * @param rows Initial number of rows
	 * @return GridLayout builder
	 */
	public static GridLayoutBuilder gridLayout(int columns, int rows) {
		return new DefaultGridLayoutBuilder(columns, rows);
	}

	/**
	 * Gets a builder to create {@link HorizontalLayout}s.
	 * @return HorizontalLayout builder
	 */
	public static HorizontalLayoutBuilder hl() {
		return new DefaultHorizontalLayoutBuilder();
	}

	/**
	 * Gets a builder to create {@link VerticalLayout}s.
	 * @return VerticalLayout builder
	 */
	public static VerticalLayoutBuilder vl() {
		return new DefaultVerticalLayoutBuilder();
	}

	/**
	 * Gets a builder to create {@link Panel}s.
	 * @return Panel builder
	 */
	public static PanelBuilder panel() {
		return new DefaultPanelBuilder();
	}

	/**
	 * Gets a builder to create {@link String} type {@link Field}s.
	 * @return Field builder
	 */
	public static StringFieldBuilder stringField() {
		return new StringField.Builder();
	}

	/**
	 * Gets a builder to create {@link String} type {@link Field}s rendered as a text area in UI.
	 * @return Field builder
	 */
	public static StringFieldBuilder stringArea() {
		return new StringArea.Builder();
	}

	/**
	 * Gets a builder to create {@link String} type {@link Field}s which not display user input on screen, used to enter
	 * secret text information like passwords.
	 * @return Field builder
	 */
	public static SecretFieldBuilder secretField() {
		return new SecretField.Builder();
	}

	/**
	 * Gets a builder to create {@link Number} type {@link Field}s.
	 * @param <T> Number type
	 * @param numberClass Concrete number class
	 * @return Field builder
	 */
	public static <T extends Number> NumberFieldBuilder<T> numberField(Class<T> numberClass) {
		return new NumberField.Builder<>(numberClass);
	}

	/**
	 * Gets a builder to create {@link Boolean} type {@link Field}s.
	 * @return Field builder
	 */
	public static BooleanFieldBuilder booleanField() {
		return new BooleanField.Builder();
	}

	/**
	 * Gets a builder to create {@link Date} type {@link Field}s.
	 * @param inline <code>true</code> to render field using an inline calendar
	 * @return Field builder
	 */
	public static DateFieldBuilder dateField(boolean inline) {
		return inline ? new InlineDateField.Builder() : new DateField.Builder();
	}

	/**
	 * Gets a builder to create {@link Date} type {@link Field}s.
	 * @return Field builder
	 */
	public static DateFieldBuilder dateField() {
		return dateField(false);
	}

	/**
	 * Gets a builder to create {@link LocalDate} type {@link Field}s.
	 * @param inline <code>true</code> to render field using an inline calendar
	 * @return Field builder
	 */
	public static TemporalWithoutTimeFieldBuilder<LocalDate> localDateField(boolean inline) {
		return new LocalDateField.Builder(inline);
	}

	/**
	 * Gets a builder to create {@link LocalDate} type {@link Field}s.
	 * @return Field builder
	 */
	public static TemporalWithoutTimeFieldBuilder<LocalDate> localDateField() {
		return localDateField(false);
	}

	/**
	 * Gets a builder to create {@link LocalDateTime} type {@link Field}s.
	 * @param inline <code>true</code> to render field using an inline calendar
	 * @return Field builder
	 */
	public static TemporalWithTimeFieldBuilder<LocalDateTime> localDateTimeField(boolean inline) {
		return new LocalDateTimeField.Builder(inline);
	}

	/**
	 * Gets a builder to create {@link LocalDateTime} type {@link Field}s.
	 * @return Field builder
	 */
	public static TemporalWithTimeFieldBuilder<LocalDateTime> localDateTimeField() {
		return localDateTimeField(false);
	}

	/**
	 * Gets a builder to create a single selection {@link Field}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @param renderingMode Rendering mode
	 * @return Field builder
	 */
	public static <T> SingleSelectFieldBuilder<T> singleSelect(Class<? extends T> type, RenderingMode renderingMode) {
		return SingleSelect.builder(type, renderingMode);
	}

	/**
	 * Gets a builder to create a single selection {@link Field} using default {@link RenderingMode#SELECT}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @return Field builder
	 */
	public static <T> SingleSelectFieldBuilder<T> singleSelect(Class<? extends T> type) {
		return SingleSelect.builder(type);
	}

	/**
	 * Gets a builder to create a multiple selection {@link Field}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @param renderingMode Rendering mode
	 * @return Field builder
	 */
	public static <T> MultiSelectFieldBuilder<T> multiSelect(Class<? extends T> type, RenderingMode renderingMode) {
		return MultiSelect.builder(type, renderingMode);
	}

	/**
	 * Gets a builder to create a multiple selection {@link Field} using default {@link RenderingMode#OPTIONS}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @return Field builder
	 */
	public static <T> MultiSelectFieldBuilder<T> multiSelect(Class<? extends T> type) {
		return MultiSelect.builder(type);
	}
	
	/**
	 * Builder to create an {@link ItemListing} instance using a {@link Grid} as backing component.
	 * @param <T> Item data type
	 * @param <P> Item property type
	 * @return Grid {@link ItemListing} builder
	 */
	public static <T, P> GridItemListingBuilder<T, P> gridListing() {
		return new DefaultGridItemListingBuilder<>();
	}
	
	/**
	 * Builder to create an {@link ItemListing} instance using a {@link Table} as backing component.
	 * @param <T> Item data type
	 * @param <P> Item property type
	 * @return Table {@link ItemListing} builder
	 */
	public static <T, P> TableItemListingBuilder<T, P> tableListing() {
		return new DefaultTableItemListingBuilder<>();
	}

	/**
	 * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
	 * @return Grid {@link PropertyListing} builder
	 */
	public static GridPropertyListingBuilder gridPropertyListing() {
		return new DefaultGridPropertyListingBuilder();
	}

	/**
	 * Builder to create an {@link PropertyListing} instance using a {@link Table} as backing component.
	 * @return Table {@link PropertyListing} builder
	 */
	public static TablePropertyListingBuilder tablePropertyListing() {
		return new DefaultTablePropertyListingBuilder();
	}

	/**
	 * Gets a builder to create a {@link PropertyFieldGroup}.
	 * @return {@link PropertyFieldGroup} builder
	 */
	public static PropertyFieldGroupBuilder propertyFieldGroup() {
		return PropertyFieldGroup.builder();
	}

	/**
	 * Gets a builder to create a {@link PropertyForm}.
	 * @param <C> Content type
	 * @param content Form content, where fields will be composed by the form {@link Composer} (not null)
	 * @return {@link PropertyForm} builder
	 */
	public static <C extends Component> PropertyFormBuilder<C> propertyForm(C content) {
		return PropertyForm.builder(content);
	}

	/**
	 * Gets a builder to create a {@link PropertyForm} using a {@link FormLayout} as layout component and a default
	 * {@link PropertyForm#componentContainerComposer()} to compose the fields on layout.
	 * @return {@link PropertyForm} builder
	 */
	public static PropertyFormBuilder<FormLayout> propertyForm() {
		return PropertyForm.builder(formLayout().fullWidth().build())
				.composer(PropertyForm.componentContainerComposer());
	}

	/**
	 * Gets a builder to create a {@link PropertyForm} using a {@link VerticalLayout} as layout component and a default
	 * {@link PropertyForm#componentContainerComposer()} to compose the fields on layout.
	 * @return {@link PropertyForm} builder
	 */
	public static PropertyFormBuilder<VerticalLayout> verticalPropertyForm() {
		return PropertyForm.builder(vl().fullWidth().build()).composer(PropertyForm.componentContainerComposer());
	}

	/**
	 * Gets a builder to create a {@link PropertyForm} using a {@link HorizontalLayout} as layout component and a
	 * default {@link PropertyForm#componentContainerComposer()} to compose the fields on layout.
	 * @return {@link PropertyForm} builder
	 */
	public static PropertyFormBuilder<HorizontalLayout> horizontalPropertyForm() {
		return PropertyForm.builder(hl().build()).composer(PropertyForm.componentContainerComposer(false));
	}

	/**
	 * Gets a builder to create a {@link PropertyForm} using a {@link VerticalLayout} as layout component and a default
	 * {@link PropertyForm#componentContainerComposer()} to compose the fields on layout.
	 * @return {@link PropertyForm} builder
	 */
	public static PropertyFormBuilder<GridLayout> gridPropertyForm() {
		return PropertyForm.builder(gridLayout().fullWidth().build())
				.composer(PropertyForm.componentContainerComposer());
	}

	/**
	 * Gets a builder to create a {@link FieldsValidator} to perform validations on a set of {@link Fields}.
	 * @return {@link FieldsValidator} builder
	 */
	public static FieldsValidator.Builder fieldsValidator() {
		return FieldsValidator.builder();
	}

	/**
	 * Gets a builder to create and open a {@link Dialog} window. The dialog will present by default a single
	 * <em>ok</em> button.
	 * @return DialogBuilder
	 */
	public static DialogBuilder dialog() {
		return Dialog.builder();
	}

	/**
	 * Gets a builder to create and open a question {@link Dialog} window. The dialog will present by default a
	 * <em>yes</em> and a <em>no</em> button. Use
	 * {@link QuestionDialogBuilder#callback(com.holonplatform.vaadin.components.Dialog.QuestionCallback)} to handle
	 * the user selected answer.
	 * @return QuestionDialogBuilder
	 */
	public static QuestionDialogBuilder questionDialog() {
		return Dialog.question();
	}

}
