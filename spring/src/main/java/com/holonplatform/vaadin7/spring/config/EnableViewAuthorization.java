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
package com.holonplatform.vaadin7.spring.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.core.Context;
import com.holonplatform.vaadin7.spring.internal.SecurityAnnotationsViewAccessControlConfiguration;
import com.vaadin.navigator.View;

/**
 * Enables {@link View} authorization control using <code>javax.annotation.security</code> annotations on View class
 * ({@link RolesAllowed}, {@link PermitAll}, {@link DenyAll}) and relying on {@link AuthContext} to perform
 * authorization control.
 * <p>
 * An {@link AuthContext} instance must be available as {@link Context} resource in order to perform access control,
 * otherwise an {@link IllegalStateException} is thrown.
 * </p>
 * <p>
 * This annotation should be added on a {@link Configuration} class of the application to automatically import
 * {@link SecurityAnnotationsViewAccessControlConfiguration}.
 * </p>
 *
 * @since 5.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SecurityAnnotationsViewAccessControlConfiguration.class)
public @interface EnableViewAuthorization {

}
