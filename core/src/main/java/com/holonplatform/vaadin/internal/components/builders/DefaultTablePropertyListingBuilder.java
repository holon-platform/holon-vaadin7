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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.TablePropertyListingBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.internal.components.DefaultPropertyListing;
import com.holonplatform.vaadin.internal.components.DefaultItemListing.RenderingMode;
import com.holonplatform.vaadin.internal.data.PropertiesItemIdentifier;
import com.holonplatform.vaadin.internal.data.container.PropertyBoxItemAdapter;

/**
 * Defauklt {@link TablePropertyListingBuilder} implementation.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public class DefaultTablePropertyListingBuilder extends
		AbstractTableItemListingBuilder<PropertyBox, Property, PropertyListing, DefaultPropertyListing, TablePropertyListingBuilder>
		implements TablePropertyListingBuilder {

	public DefaultTablePropertyListingBuilder() {
		super(new DefaultPropertyListing(RenderingMode.TABLE));
		// default adapter
		dataSourceBuilder.itemAdapter(new PropertyBoxItemAdapter());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder#withProperties(java.lang.Iterable)
	 */
	@Override
	public <P extends Property> TablePropertyListingBuilder withProperties(Iterable<P> properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		properties.forEach(p -> {
			dataSourceBuilder.withProperty(p, p.getType());
			if (p.isReadOnly()) {
				dataSourceBuilder.readOnly(p, true);
			}
			if (Path.class.isAssignableFrom(p.getClass())) {
				dataSourceBuilder.sortable(p, true);
			}
		});
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder#dataSource(com.holonplatform.vaadin.data.
	 * ItemDataProvider, com.holonplatform.core.property.Property[])
	 */
	@Override
	public TablePropertyListingBuilder dataSource(ItemDataProvider<PropertyBox> dataProvider,
			Property... identifierProperties) {
		ObjectUtils.argumentNotNull(identifierProperties, "Identifier properties must be not null");
		if (identifierProperties.length == 0) {
			throw new IllegalArgumentException("Identifier properties must be not empty");
		}
		return dataSource(dataProvider, new PropertiesItemIdentifier(identifierProperties));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected PropertyListing build(DefaultPropertyListing instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected TablePropertyListingBuilder builder() {
		return this;
	}

}