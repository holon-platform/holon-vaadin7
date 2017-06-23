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
package com.holonplatform.vaadin.test.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;

@SuppressWarnings("serial")
public class TestDataItem implements Item {

	private final static List<String> PROPERTY_IDS;

	static {
		PROPERTY_IDS = new ArrayList<>(4);
		PROPERTY_IDS.add("code");
		PROPERTY_IDS.add("description");
		PROPERTY_IDS.add("sequence");
		PROPERTY_IDS.add("obsolete");
	}

	private final Map<String, Property<?>> properties = new HashMap<>(4);

	public TestDataItem(String code, String description, Integer sequence, int obsolete) {
		super();
		properties.put("code", new ObjectProperty<>(code, String.class));
		properties.put("description", new ObjectProperty<>(description, String.class));
		properties.put("sequence", new ObjectProperty<>(sequence, Integer.class));
		properties.put("obsolete", new ObjectProperty<>(obsolete, int.class));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Item#getItemProperty(java.lang.Object)
	 */
	@Override
	public Property<?> getItemProperty(Object id) {
		return properties.get(id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Item#getItemPropertyIds()
	 */
	@Override
	public Collection<?> getItemPropertyIds() {
		return PROPERTY_IDS;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Item#addItemProperty(java.lang.Object, com.vaadin.data.Property)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Item#removeItemProperty(java.lang.Object)
	 */
	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
