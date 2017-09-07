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
package com.holonplatform.vaadin.internal.data.container;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.container.ItemAdapter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

@SuppressWarnings("rawtypes")
public class BeanItemAdapter implements ItemAdapter {

	private static final long serialVersionUID = -9058266880411104997L;

	private final Set<String> nestedPropertyIds;

	public BeanItemAdapter() {
		this(null);
	}

	public BeanItemAdapter(Collection<String> nestedPropertyIds) {
		super();
		this.nestedPropertyIds = (nestedPropertyIds != null) ? new HashSet<>(nestedPropertyIds)
				: Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemAdapter#adapt(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration, java.lang.Object)
	 */
	@Override
	public Item adapt(Configuration configuration, Object item) {
		if (item != null) {
			BeanItem bi = new BeanItem<>(item);
			nestedPropertyIds.forEach(id -> bi.addNestedProperty(id));
			return bi;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemAdapter#restore(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration, com.vaadin.data.Item)
	 */
	@Override
	public Object restore(Configuration configuration, Item item) {
		if (item != null) {
			if (item instanceof BeanItem) {
				return ((BeanItem) item).getBean();
			} else {
				throw new UnsupportedOperationException("The bean item adapter only supports BeanItem type items");
			}
		}
		return null;
	}

}
