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

import com.holonplatform.vaadin.components.Input;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

/**
 * Adapter for input {@link com.holonplatform.vaadin.components.Input.ValueChangeListener}s.
 * 
 * @param <V> Value type
 * 
 * @since 5.0.0
 */
public class ValueChangeListenerAdapter<V> implements ValueChangeListener {

	private static final long serialVersionUID = 8418286403853201418L;

	private final Input<V> source;
	private final com.holonplatform.vaadin.components.Input.ValueChangeListener<V> wrapped;

	public ValueChangeListenerAdapter(Input<V> source,
			com.holonplatform.vaadin.components.Input.ValueChangeListener<V> wrapped) {
		super();
		this.source = source;
		this.wrapped = wrapped;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void valueChange(ValueChangeEvent event) {
		wrapped.valueChange(new InputValueChangeEvent<>(source, (V) event.getProperty().getValue()));
	}

}
