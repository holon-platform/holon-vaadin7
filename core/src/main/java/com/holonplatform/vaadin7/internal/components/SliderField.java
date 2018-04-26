/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin7.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin7.internal.converters.NumberToNumberConverter;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.Slider;

/**
 * Default Slider Field implementation.
 * 
 * @param <T> Number type
 * 
 * @since 5.0.0
 */
public class SliderField<T extends Number> extends AbstractCustomField<T, Slider> {

	private static final long serialVersionUID = -5951702885128017698L;

	/**
	 * Constructor
	 * @param type Concrete field type
	 */
	public SliderField(Class<? extends T> type) {
		super(type);

		addStyleName("h-field");
		addStyleName("h-sliderfield");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected Slider buildInternalField(Class<? extends T> type) {
		Slider slider = new Slider();
		if (TypeUtils.isIntegerNumber(getType())) {
			slider.setResolution(0);
		}
		slider.setConverter(new NumberToNumberConverter<>(Double.class, type));
		return slider;
	}

	/**
	 * Convert given model value into presentation type value
	 * @param value Value to convert
	 * @return Converted value
	 */
	protected Double convertToPresentation(T value) {
		return getInternalField().getConverter().convertToPresentation(value, Double.class, getLocale());
	}

	/**
	 * Convert given double presentation value into actual model type value
	 * @param value Value to convert
	 * @return Converted value
	 */
	@SuppressWarnings("unchecked")
	protected T convertToModel(Double value) {
		return (T) getInternalField().getConverter().convertToModel(value, getType(), getLocale());
	}

	/**
	 * Gets the maximum slider value
	 * @return the largest value the slider can have
	 */
	public T getMax() {
		return convertToModel(getInternalField().getMax());
	}

	/**
	 * Set the maximum slider value. If the current value of the slider is larger than this, the value is set to the new
	 * maximum.
	 * @param max The new maximum slider value
	 */
	public void setMax(T max) {
		ObjectUtils.argumentNotNull(max, "Max value must be not null");
		getInternalField().setMax(convertToPresentation(max));
	}

	/**
	 * Gets the minimum slider value
	 * @return the smallest value the slider can have
	 */
	public T getMin() {
		return convertToModel(getInternalField().getMin());
	}

	/**
	 * Set the minimum slider value. If the current value of the slider is smaller than this, the value is set to the
	 * new minimum.
	 * @param min The new minimum slider value
	 */
	public void setMin(T min) {
		ObjectUtils.argumentNotNull(min, "Min value must be not null");
		getInternalField().setMin(convertToPresentation(min));
	}

	/**
	 * Get the current orientation of the slider (horizontal or vertical).
	 * @return {@link SliderOrientation#HORIZONTAL} or {@link SliderOrientation#VERTICAL}
	 */
	public SliderOrientation getOrientation() {
		return getInternalField().getOrientation();
	}

	/**
	 * Set the orientation of the slider.
	 * @param orientation The new orientation, either {@link SliderOrientation#HORIZONTAL} or
	 *        {@link SliderOrientation#VERTICAL}
	 */
	public void setOrientation(SliderOrientation orientation) {
		getInternalField().setOrientation(orientation);
	}

	/**
	 * Get the current resolution of the slider. The resolution is the number of digits after the decimal point.
	 * @return resolution
	 */
	public int getResolution() {
		return getInternalField().getResolution();
	}

	/**
	 * Set a new resolution for the slider. The resolution is the number of digits after the decimal point.
	 * @param resolution The resolution to set. Ignored for integer value types
	 */
	public void setResolution(int resolution) {
		if (!TypeUtils.isIntegerNumber(getType())) {
			getInternalField().setResolution(resolution);
		}
	}

}
