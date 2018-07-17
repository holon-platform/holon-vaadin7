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
package com.holonplatform.vaadin7.navigator;

import com.vaadin.navigator.View;
import com.vaadin.ui.Component;

/**
 * Interface to delegate {@link View} content handling to {@link #getViewContent()} method.
 * 
 * @since 5.0.0
 */
@FunctionalInterface
public interface ViewContentProvider {

	/**
	 * Get view content component. This method is called by {@link ViewNavigator} when a {@link View} which implements
	 * ViewContentProvider has to be shown in application UI.
	 * @return View content component
	 */
	Component getViewContent();

}