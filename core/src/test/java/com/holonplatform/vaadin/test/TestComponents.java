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
package com.holonplatform.vaadin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.holonplatform.vaadin7.internal.components.NumberField;

public class TestComponents {

	@Test
	public void testNumberField() {

		NumberField<Long> tf = new NumberField<>(Long.class);

		assertNull(tf.getValue());

		tf.setValue(7L);

		assertNotNull(tf.getValue());
		assertEquals(Long.valueOf(7), tf.getValue());

		// Components.enumField(TestEnum1.class);

	}

}
