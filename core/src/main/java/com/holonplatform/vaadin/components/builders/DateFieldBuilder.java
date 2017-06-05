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

import java.util.Date;

import com.vaadin.shared.ui.datefield.Resolution;

/**
 * A {@link CalendarFieldBuilder} for {@link Date} type Fields.
 * 
 * @since 5.0.0
 */
public interface DateFieldBuilder extends CalendarFieldBuilder<Date, DateFieldBuilder> {

	/**
	 * Sets the field resolution.
	 * <p>
	 * The default resolution is {@link Resolution#DAY}.
	 * </p>
	 * @param resolution The resolution to set
	 * @return this
	 */
	DateFieldBuilder resolution(Resolution resolution);

}
