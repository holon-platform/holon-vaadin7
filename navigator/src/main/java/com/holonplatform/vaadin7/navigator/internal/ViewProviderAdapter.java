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
package com.holonplatform.vaadin7.navigator.internal;

import com.holonplatform.vaadin7.navigator.ViewNavigator;
import com.vaadin.navigator.ViewProvider;

/**
 * Adapter to extend a {@link ViewProvider} for {@link ViewNavigator} Views management
 * 
 * @since 5.0.0
 */
public interface ViewProviderAdapter extends ViewProvider {

	/**
	 * Get concrete view provider
	 * @return Wrapped ViewProvider
	 */
	ViewProvider getWrappedProvider();

}
