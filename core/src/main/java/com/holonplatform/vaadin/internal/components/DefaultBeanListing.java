/*
 * Copyright 2000-2017 Holon TDCN.
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

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.vaadin.components.BeanListing;

/**
 * Default {@link BeanListing} implementation.
 * 
 * @param <T> Bean type
 * 
 * @since 5.0.0
 */
public class DefaultBeanListing<T> extends DefaultItemListing<T, String> implements BeanListing<T> {

	private static final long serialVersionUID = 1249610620320288976L;

	private BeanPropertySet<T> propertySet;

	public DefaultBeanListing(RenderingMode renderingMode) {
		super(renderingMode);
	}

	public void setPropertySet(BeanPropertySet<T> propertySet) {
		this.propertySet = propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#buildPropertyColumn(java.lang.Object)
	 */
	@Override
	protected PropertyColumn<T, String> buildPropertyColumn(String property) {
		PropertyColumn<T, String> column = super.buildPropertyColumn(property);
		if (propertySet != null) {
			propertySet.getProperty(property).ifPresent(p -> column.setCaption(p));
		}
		return column;
	}

}
