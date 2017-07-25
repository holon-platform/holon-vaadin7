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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Registration;
import com.holonplatform.vaadin.components.ValueHolder;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;

/**
 * A {@link Registration} object using a {@link ValueChangeNotifier}.
 * 
 * @since 5.0.0
 */
public class ValueChangeNotifierRegistration implements Registration {

	private static final long serialVersionUID = 6956947394852629133L;

	private final ValueChangeNotifier notifier;
	private final ValueChangeListener listener;

	public ValueChangeNotifierRegistration(ValueChangeNotifier notifier, ValueChangeListener listener) {
		super();
		this.notifier = notifier;
		this.listener = listener;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Registration#remove()
	 */
	@Override
	public void remove() {
		notifier.removeValueChangeListener(listener);
	}

	/**
	 * Adapt a {@link ValueHolder} {@link com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener} and
	 * register it in given notifier.
	 * @param <V> Value type
	 * @param source Source {@link ValueHolder}
	 * @param notifier Actual {@link ValueChangeNotifier}
	 * @param listener Listener to adapt
	 * @return Listener registration
	 */
	public static <V> Registration adapt(ValueHolder<V> source, ValueChangeNotifier notifier,
			com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener<V> listener) {
		ObjectUtils.argumentNotNull(notifier, "ValueChangeNotifier must be not null");
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		final com.vaadin.data.Property.ValueChangeListener vcl = new ValueChangeListenerAdapter<>(source, listener);
		notifier.addValueChangeListener(vcl);
		return new ValueChangeNotifierRegistration(notifier, vcl);
	}

}
