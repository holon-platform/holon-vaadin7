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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.holonplatform.vaadin.components.Dialog.DialogBuilder;
import com.holonplatform.vaadin.components.Dialog.QuestionDialogBuilder;
import com.holonplatform.vaadin.components.PropertyForm.Composer;
import com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder;
import com.holonplatform.vaadin.components.PropertyInputGroup.PropertyInputGroupBuilder;
import com.holonplatform.vaadin.components.PropertyViewGroup.PropertyViewGroupBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.components.builders.ClickableLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.ClickableLayoutConfigurator.BaseClickableLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.components.builders.ComponentConfigurator.BaseComponentConfigurator;
import com.holonplatform.vaadin.components.builders.CssLayoutBuilder;
import com.holonplatform.vaadin.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.components.builders.GridLayoutBuilder;
import com.holonplatform.vaadin.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.components.builders.InputConfigurator;
import com.holonplatform.vaadin.components.builders.InputConfigurator.BaseFieldConfigurator;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.TableItemListingBuilder;
import com.holonplatform.vaadin.components.builders.LabelBuilder;
import com.holonplatform.vaadin.components.builders.LayoutConfigurator;
import com.holonplatform.vaadin.components.builders.LayoutConfigurator.BaseLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.MultiSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator.BaseOrderedLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.PanelBuilder;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.TablePropertyListingBuilder;
import com.holonplatform.vaadin.components.builders.SecretInputBuilder;
import com.holonplatform.vaadin.components.builders.SingleSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.components.builders.TemporalInputBuilder.TemporalWithTimeFieldBuilder;
import com.holonplatform.vaadin.components.builders.TemporalInputBuilder.TemporalWithoutTimeFieldBuilder;
import com.holonplatform.vaadin.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.internal.components.BooleanField;
import com.holonplatform.vaadin.internal.components.DateField;
import com.holonplatform.vaadin.internal.components.DefaultPropertyViewGroup;
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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * Main provider of UI components builders and configurators.
 * <p>
 * Provides static methods to obtain builder for common UI components type, allowing fluent and implementation-agnostic
 * components creation and configuration.
 * </p>
 * 
 * @since 5.0.0
 */
public interface Components {

	// Configurators

	/**
	 * Get a {@link ComponentConfigurator} to configure given component.
	 * @param component Component to configure (not null)
	 * @return BaseComponentConfigurator
	 */
	static BaseComponentConfigurator configure(AbstractComponent component) {
		return new DefaultComponentConfigurator(component);
	}

	/**
	 * Get a {@link ButtonConfigurator} to configure given button.
	 * @param button Button to configure (not null)
	 * @return BaseButtonConfigurator
	 */
	static BaseButtonConfigurator configure(Button button) {
		return new DefaultButtonConfigurator(button);
	}

	/**
	 * Get a {@link InputConfigurator} to configure given field.
	 * @param <T> Field type
	 * @param field Field to configure (not null)
	 * @return BaseFieldConfigurator
	 */
	static <T> BaseFieldConfigurator<T> configure(AbstractField<T> field) {
		return new DefaultFieldConfigurator<>(field);
	}

	/**
	 * Get a {@link OrderedLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	static BaseOrderedLayoutConfigurator configure(VerticalLayout layout) {
		return new DefaultOrderedLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link OrderedLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	static BaseOrderedLayoutConfigurator configure(HorizontalLayout layout) {
		return new DefaultOrderedLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link OrderedLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	static BaseOrderedLayoutConfigurator configure(FormLayout layout) {
		return new DefaultOrderedLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link LayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	static BaseLayoutConfigurator configure(GridLayout layout) {
		return new DefaultLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link ClickableLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	static BaseClickableLayoutConfigurator configure(CssLayout layout) {
		return new DefaultClickableLayoutConfigurator<>(layout);
	}

	/**
	 * Get a {@link ClickableLayoutConfigurator} to configure given layout.
	 * @param layout Layout to configure
	 * @return Layout configurator
	 */
	static BaseClickableLayoutConfigurator configure(AbsoluteLayout layout) {
		return new DefaultClickableLayoutConfigurator<>(layout);
	}

	// Builders

	/**
	 * Build a filler component, i.e. a {@link Label} with undefined size and the HTML entity <code>&nbsp;</code> as
	 * content, which can be used with full expand ratio as a space filler in layouts.
	 * @return Filler
	 */
	static Component filler() {
		return new Filler();
	}

	/**
	 * Gets a builder to create {@link Label}s.
	 * @return Label builder
	 */
	static LabelBuilder label() {
		return new DefaultLabelBuilder();
	}

	/**
	 * Gets a builder to create {@link Button}s.
	 * @return Button builder
	 */
	static ButtonBuilder button() {
		return button(false);
	}

	/**
	 * Gets a builder to create {@link Button}s
	 * @param nativeMode <code>true</code> to create a "native" button, i.e. implemented using the native button of web
	 *        browsers, using the HTML <code>&lt;button&gt;</code> element.
	 * @return Button builder
	 */
	static ButtonBuilder button(boolean nativeMode) {
		return new DefaultButtonBuilder(nativeMode);
	}

	/**
	 * Gets a builder to create {@link CssLayout}s.
	 * @return CssLayout builder
	 */
	static CssLayoutBuilder cssLayout() {
		return new DefaultCssLayoutBuilder();
	}

	/**
	 * Gets a builder to create {@link FormLayout}s.
	 * @return FormLayout builder
	 */
	static FormLayoutBuilder formLayout() {
		return new DefaultFormLayoutBuilder();
	}

	/**
	 * Gets a builder to create {@link FormLayout}s.
	 * @return FormLayout builder
	 */
	static GridLayoutBuilder gridLayout() {
		return gridLayout(1, 1);
	}

	/**
	 * Gets a builder to create {@link GridLayout}s.
	 * @param columns Initial number of columns
	 * @param rows Initial number of rows
	 * @return GridLayout builder
	 */
	static GridLayoutBuilder gridLayout(int columns, int rows) {
		return new DefaultGridLayoutBuilder(columns, rows);
	}

	/**
	 * Gets a builder to create {@link HorizontalLayout}s.
	 * @return HorizontalLayout builder
	 */
	static HorizontalLayoutBuilder hl() {
		return new DefaultHorizontalLayoutBuilder();
	}

	/**
	 * Gets a builder to create {@link VerticalLayout}s.
	 * @return VerticalLayout builder
	 */
	static VerticalLayoutBuilder vl() {
		return new DefaultVerticalLayoutBuilder();
	}

	/**
	 * Gets a builder to create {@link Panel}s.
	 * @return Panel builder
	 */
	static PanelBuilder panel() {
		return new DefaultPanelBuilder();
	}

	/**
	 * Gets a builder to create and open a {@link Dialog} window. The dialog will present by default a single
	 * <em>ok</em> button.
	 * @return DialogBuilder
	 */
	static DialogBuilder dialog() {
		return Dialog.builder();
	}

	/**
	 * Gets a builder to create and open a question {@link Dialog} window. The dialog will present by default a
	 * <em>yes</em> and a <em>no</em> button. Use
	 * {@link QuestionDialogBuilder#callback(com.holonplatform.vaadin.components.Dialog.QuestionCallback)} to handle the
	 * user selected answer.
	 * @return QuestionDialogBuilder
	 */
	static QuestionDialogBuilder questionDialog() {
		return Dialog.question();
	}

	// Inputs

	/**
	 * {@link Input} and {@link InputGroup} builders provider.
	 */
	static interface input {

		/**
		 * Gets a builder to create {@link String} type {@link Input}s.
		 * @return Input builder
		 */
		static StringInputBuilder string() {
			return string(false);
		}

		/**
		 * Gets a builder to create {@link String} type {@link Input}s.
		 * @param area <code>true</code> to create an input component rendered as a <em>text area</em>,
		 *        <code>false</code> for a standard text input
		 * @return Input builder
		 */
		static StringInputBuilder string(boolean area) {
			return area ? new StringArea.Builder() : new StringField.Builder();
		}

		/**
		 * Gets a builder to create {@link String} type {@link Input}s which not display user input on screen, used to
		 * enter secret text information like passwords.
		 * @return Input builder
		 */
		static SecretInputBuilder secretString() {
			return new SecretField.Builder();
		}

		/**
		 * Gets a builder to create {@link Number} type {@link Input}s.
		 * @param <T> Number type
		 * @param numberClass Concrete number class
		 * @return Input builder
		 */
		static <T extends Number> NumberInputBuilder<T> number(Class<T> numberClass) {
			return new NumberField.Builder<>(numberClass);
		}

		/**
		 * Gets a builder to create {@link Boolean} type {@link Input}s.
		 * @return Input builder
		 */
		static BooleanInputBuilder boolean_() {
			return new BooleanField.Builder();
		}

		/**
		 * Gets a builder to create {@link Date} type {@link Input}s.
		 * @param inline <code>true</code> to render the input component using an inline calendar
		 * @return Input builder
		 */
		static DateInputBuilder date(boolean inline) {
			return inline ? new InlineDateField.Builder() : new DateField.Builder();
		}

		/**
		 * Gets a builder to create {@link Date} type {@link Input}s.
		 * @return Input builder
		 */
		static DateInputBuilder date() {
			return date(false);
		}

		/**
		 * Gets a builder to create {@link LocalDate} type {@link Input}s.
		 * @param inline <code>true</code> to render the input component using an inline calendar
		 * @return Input builder
		 */
		static TemporalWithoutTimeFieldBuilder<LocalDate> localDate(boolean inline) {
			return new LocalDateField.Builder(inline);
		}

		/**
		 * Gets a builder to create {@link LocalDate} type {@link Input}s.
		 * @return Input builder
		 */
		static TemporalWithoutTimeFieldBuilder<LocalDate> localDate() {
			return localDate(false);
		}

		/**
		 * Gets a builder to create {@link LocalDateTime} type {@link Input}s.
		 * @param inline <code>true</code> to render the input component using an inline calendar
		 * @return Input builder
		 */
		static TemporalWithTimeFieldBuilder<LocalDateTime> localDateTime(boolean inline) {
			return new LocalDateTimeField.Builder(inline);
		}

		/**
		 * Gets a builder to create {@link LocalDateTime} type {@link Input}s.
		 * @return Input builder
		 */
		static TemporalWithTimeFieldBuilder<LocalDateTime> localDateTime() {
			return localDateTime(false);
		}

		/**
		 * Gets a builder to create a single selection {@link Input}.
		 * @param <T> Selection value type
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 * @return Input builder
		 */
		static <T> SingleSelectInputBuilder<T> singleSelect(Class<? extends T> type, RenderingMode renderingMode) {
			return SingleSelect.builder(type, renderingMode);
		}

		/**
		 * Gets a builder to create a single selection {@link Input} using default {@link RenderingMode#SELECT}.
		 * @param <T> Selection value type
		 * @param type Selection value type
		 * @return Input builder
		 */
		static <T> SingleSelectInputBuilder<T> singleSelect(Class<? extends T> type) {
			return SingleSelect.builder(type);
		}

		/**
		 * Gets a builder to create a multiple selection {@link Input}.
		 * @param <T> Selection value type
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 * @return Input builder
		 */
		static <T> MultiSelectInputBuilder<T> multiSelect(Class<? extends T> type, RenderingMode renderingMode) {
			return MultiSelect.builder(type, renderingMode);
		}

		/**
		 * Gets a builder to create a multiple selection {@link Input} using default {@link RenderingMode#OPTIONS}.
		 * @param <T> Selection value type
		 * @param type Selection value type
		 * @return Input builder
		 */
		static <T> MultiSelectInputBuilder<T> multiSelect(Class<? extends T> type) {
			return MultiSelect.builder(type);
		}

		/**
		 * Gets a builder to create a single selection {@link Input} with given {@link Enum} class as data source, using
		 * all enum constants as selection items.
		 * @param <E> Enum value type
		 * @param type Enum value type
		 * @param renderingMode Rendering mode
		 * @return Input builder
		 */
		static <E extends Enum<E>> SingleSelectInputBuilder<E> enumSingle(Class<E> type, RenderingMode renderingMode) {
			return singleSelect(type, renderingMode).items(type.getEnumConstants());
		}

		/**
		 * Gets a builder to create a single selection {@link Input} with given {@link Enum} class as data source, using
		 * all enum constants as selection items and default {@link RenderingMode#SELECT} rendering mode.
		 * @param <E> Enum value type
		 * @param type Enum value type
		 * @return Input builder
		 */
		static <E extends Enum<E>> SingleSelectInputBuilder<E> enumSingle(Class<E> type) {
			return singleSelect(type).items(type.getEnumConstants());
		}

		/**
		 * Gets a builder to create a multiple selection {@link Input} with given {@link Enum} class as data source,
		 * using all enum constants as selection items.
		 * @param <E> Enum value type
		 * @param type Enum value type
		 * @param renderingMode Rendering mode
		 * @return Input builder
		 */
		static <E extends Enum<E>> MultiSelectInputBuilder<E> enumMulti(Class<E> type, RenderingMode renderingMode) {
			return multiSelect(type, renderingMode).items(type.getEnumConstants());
		}

		/**
		 * Gets a builder to create a multiple selection {@link Input} with given {@link Enum} class as data source,
		 * using all enum constants as selection items and default {@link RenderingMode#OPTIONS} rendering mode.
		 * @param <E> Enum value type
		 * @param type Enum value type
		 * @return Input builder
		 */
		static <E extends Enum<E>> MultiSelectInputBuilder<E> enumMulti(Class<E> type) {
			return multiSelect(type).items(type.getEnumConstants());
		}

		/**
		 * Gets a builder to create a {@link InputGroup}.
		 * @return {@link InputGroup} builder
		 */
		static InputGroup.Builder group() {
			return InputGroup.builder();
		}

		/**
		 * Gets a builder to create a {@link PropertyInputGroup}.
		 * @return {@link PropertyInputGroup} builder
		 */
		static PropertyInputGroupBuilder propertyGroup() {
			return PropertyInputGroup.builder();
		}

	}

	// View components

	/**
	 * {@link ViewComponent} and {@link PropertyViewGroup} builders provider.
	 */
	static interface view {

		/**
		 * Gets a builder to create a {@link ViewComponent} instance.
		 * @param <T> Value type
		 * @param valueType the value type handled by the {@link ViewComponent}
		 * @return {@link ViewComponent} builder
		 */
		static <T> ViewComponentBuilder<T> component(Class<? extends T> valueType) {
			return ViewComponent.builder(valueType);
		}

		/**
		 * Get a builder to create and setup a {@link PropertyViewGroup}.
		 * @return {@link PropertyViewGroup} builder
		 */
		static PropertyViewGroupBuilder propertyGroup() {
			return new DefaultPropertyViewGroup.DefaultBuilder();
		}

	}

	// Item listings

	/**
	 * {@link ItemListing} builders provider.
	 */
	static interface listing {

		/**
		 * Builder to create an {@link ItemListing} instance using a {@link Grid} as backing component.
		 * @param <T> Item data type
		 * @param <P> Item property type
		 * @return Grid {@link ItemListing} builder
		 */
		static <T, P> GridItemListingBuilder<T, P> items() {
			return new DefaultGridItemListingBuilder<>();
		}

		/**
		 * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
		 * @return Grid {@link PropertyListing} builder
		 */
		static GridPropertyListingBuilder properties() {
			return new DefaultGridPropertyListingBuilder();
		}

		/**
		 * Builder to create an {@link ItemListing} instance using a {@link Table} as backing component.
		 * @param <T> Item data type
		 * @param <P> Item property type
		 * @return Table {@link ItemListing} builder
		 */
		static <T, P> TableItemListingBuilder<T, P> itemsUsingTable() {
			return new DefaultTableItemListingBuilder<>();
		}

		/**
		 * Builder to create an {@link PropertyListing} instance using a {@link Table} as backing component.
		 * @return Table {@link PropertyListing} builder
		 */
		static TablePropertyListingBuilder propertiesUsingTable() {
			return new DefaultTablePropertyListingBuilder();
		}

	}

	/**
	 * {@link PropertyForm} builders provider.
	 */
	static interface form {

		/**
		 * Gets a builder to create a {@link PropertyForm}.
		 * @param <C> Content type
		 * @param content Form content, where fields will be composed by the form {@link Composer} (not null)
		 * @return {@link PropertyForm} builder
		 */
		static <C extends Component> PropertyFormBuilder<C> builder(C content) {
			return PropertyForm.builder(content);
		}

		/**
		 * Gets a builder to create a {@link PropertyForm} using a {@link FormLayout} as layout component and a default
		 * {@link PropertyForm#componentContainerComposer()} to compose the fields on layout.
		 * @return {@link PropertyForm} builder
		 */
		static PropertyFormBuilder<FormLayout> builder() {
			return PropertyForm.builder(formLayout().fullWidth().build())
					.composer(PropertyForm.componentContainerComposer());
		}

		/**
		 * Gets a builder to create a {@link PropertyForm} using a {@link VerticalLayout} as layout component and a
		 * default {@link PropertyForm#componentContainerComposer()} to compose the fields on layout.
		 * @return {@link PropertyForm} builder
		 */
		static PropertyFormBuilder<VerticalLayout> vertical() {
			return PropertyForm.builder(vl().fullWidth().build()).composer(PropertyForm.componentContainerComposer());
		}

		/**
		 * Gets a builder to create a {@link PropertyForm} using a {@link HorizontalLayout} as layout component and a
		 * default {@link PropertyForm#componentContainerComposer()} to compose the fields on layout.
		 * @return {@link PropertyForm} builder
		 */
		static PropertyFormBuilder<HorizontalLayout> horizontal() {
			return PropertyForm.builder(hl().build()).composer(PropertyForm.componentContainerComposer(false));
		}

		/**
		 * Gets a builder to create a {@link PropertyForm} using a {@link VerticalLayout} as layout component and a
		 * default {@link PropertyForm#componentContainerComposer()} to compose the fields on layout.
		 * @return {@link PropertyForm} builder
		 */
		static PropertyFormBuilder<GridLayout> grid() {
			return PropertyForm.builder(gridLayout().fullWidth().build())
					.composer(PropertyForm.componentContainerComposer());
		}

	}

}
