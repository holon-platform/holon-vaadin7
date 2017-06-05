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
package com.holonplatform.vaadin.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.vaadin.spring.SpringViewNavigator;
import com.holonplatform.vaadin.spring.config.EnableViewAuthorization;
import com.holonplatform.vaadin.spring.config.EnableViewNavigator;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.boot.VaadinAutoConfiguration;
import com.vaadin.spring.navigator.SpringNavigator;

/**
 * Spring boot auto configuration class to enable Holon-Spring-Vaadin integration layer.
 *
 * @since 5.0.0
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(SpringUI.class)
@AutoConfigureBefore(VaadinAutoConfiguration.class)
public class HolonVaadinAutoConfiguration {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(HolonVaadinAutoConfiguration.class);

	@Configuration
	@EnableVaadin
	static class EnableVaadinConfiguration implements InitializingBean {
		@Override
		public void afterPropertiesSet() throws Exception {
			logger.debug("{} initialized", getClass().getName());
		}
	}

	@Configuration
	@ConditionalOnClass(SpringViewNavigator.class)
	@ConditionalOnMissingBean(SpringNavigator.class)
	@EnableViewNavigator
	@EnableViewAuthorization
	static class EnableViewNavigatorConfiguration implements InitializingBean {
		@Override
		public void afterPropertiesSet() throws Exception {
			logger.debug("{} initialized", getClass().getName());
		}
	}

}
