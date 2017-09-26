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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder;
import com.holonplatform.vaadin.internal.components.DefaultBeanListing;
import com.holonplatform.vaadin.internal.components.DefaultItemListing.RenderingMode;
import com.holonplatform.vaadin.internal.data.container.BeanItemAdapter;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.renderers.Renderer;

/**
 * {@link GridItemListingBuilder} implementation.
 * 
 * @param <T> Item data type
 *
 * @since 5.0.0
 */
public class DefaultGridItemListingBuilder<T> extends
		AbstractGridItemListingBuilder<T, String, BeanListing<T>, DefaultBeanListing<T>, GridItemListingBuilder<T>>
		implements GridItemListingBuilder<T> {

	@SuppressWarnings("unchecked")
	public DefaultGridItemListingBuilder(Class<T> beanType) {
		super(new DefaultBeanListing<>(RenderingMode.GRID));
		ObjectUtils.argumentNotNull(beanType, "Item bean type must be not null");
		// setup datasource
		BeanPropertySet<T> ps = BeanPropertySet.create(beanType);
		getInstance().setPropertySet(ps);
		final List<String> nested = new LinkedList<>();
		ps.forEach(p -> {
			final String name = p.fullName();
			dataSourceBuilder.withProperty(name, p.getType());

			if (p.getParent().isPresent()) {
				nested.add(p.relativeName());
			}
		});
		// item adapter
		dataSourceBuilder.itemAdapter(new BeanItemAdapter(nested));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#render(java.lang.String,
	 * com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public GridItemListingBuilder<T> render(String property, Renderer<?> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setRenderer(renderer);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#render(java.lang.String,
	 * com.vaadin.data.util.converter.Converter, com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public <V, P> GridItemListingBuilder<T> render(String property, Converter<V, P> converter,
			Renderer<? super P> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setConverter(converter);
		getInstance().getPropertyColumn(property).setRenderer(renderer);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected BeanListing<T> build(DefaultBeanListing<T> instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected GridItemListingBuilder<T> builder() {
		return this;
	}

}
