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
package com.holonplatform.vaadin.components.builders;

import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.Field;

/**
 * Builder to create Slider field.
 * 
 * @param <T> Number type
 * 
 * @since 5.0.0
 */
public interface SliderFieldBuilder<T extends Number> extends FieldBuilder<T, Field<T>, SliderFieldBuilder<T>> {

	/**
	 * Set the maximum value. If the current value of the field is larger than this, the value is set to the new
	 * maximum.
	 * @param max The new maximum field value
	 * @return this
	 */
	SliderFieldBuilder<T> max(T max);

	/**
	 * Set the minimum field value. If the current value of the field is smaller than this, the value is set to the new
	 * minimum.
	 * @param min The new minimum field value
	 * @return this
	 */
	SliderFieldBuilder<T> min(T min);

	/**
	 * Set the orientation of the slider.
	 * @param orientation The new orientation, either {@link SliderOrientation#HORIZONTAL} or
	 *        {@link SliderOrientation#VERTICAL}
	 * @return this
	 */
	SliderFieldBuilder<T> orientation(SliderOrientation orientation);

	/**
	 * Set a new resolution for the slider. The resolution is the number of digits after the decimal point.
	 * <p>
	 * For integer type fields, this method has no effect, since resolution is always <code>0</code>.
	 * </p>
	 * @param resolution The resolution to set
	 * @return this
	 */
	SliderFieldBuilder<T> resolution(int resolution);

}
