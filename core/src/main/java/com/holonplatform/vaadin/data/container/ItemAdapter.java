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
package com.holonplatform.vaadin.data.container;

import java.io.Serializable;

import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.vaadin.data.Item;

/**
 * Adapter to convert data source item data type to a container {@link Item} and back.
 * 
 * @param <ITEM> Data source item data type
 * 
 * @since 5.0.0
 */
public interface ItemAdapter<ITEM> extends Serializable {

	/**
	 * Convert item data into a container {@link Item}.
	 * @param configuration Data source configuration
	 * @param item Item data to convert
	 * @return Container Item
	 */
	Item adapt(Configuration<?> configuration, ITEM item);

	/**
	 * Restore item data from given container item.
	 * @param configuration Data source configuration
	 * @param item Container item
	 * @return Item data
	 */
	ITEM restore(Configuration<?> configuration, Item item);

}
