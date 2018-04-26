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
package com.holonplatform.vaadin.navigator.test.components;

import com.holonplatform.vaadin7.navigator.ViewContentProvider;
import com.holonplatform.vaadin7.navigator.annotations.OnShow;
import com.holonplatform.vaadin7.navigator.annotations.ViewParameter;
import com.holonplatform.vaadin7.navigator.annotations.WindowView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@WindowView(resizable = false)
public class ViewFive implements View, ViewContentProvider {

	private static final long serialVersionUID = 1L;

	@ViewParameter
	private String testPar;

	private boolean entered = false;
	private boolean showed = false;

	public String getTestPar() {
		return testPar;
	}

	public boolean isEntered() {
		return entered;
	}

	public boolean isShowed() {
		return showed;
	}

	@Override
	public Component getViewContent() {
		return new Label((testPar != null) ? testPar : "FIVE");
	}

	@Override
	public void enter(ViewChangeEvent event) {
		entered = true;
	}

	@OnShow
	public void showing() {
		showed = true;
	}

	@OnShow
	public void leaving() {
	}

}
