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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder;
import com.holonplatform.vaadin.internal.components.DefaultItemListing;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.server.Resource;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.FooterClickListener;
import com.vaadin.ui.Table.HeaderClickListener;
import com.vaadin.ui.Table.RowGenerator;
import com.vaadin.ui.Table.TableDragMode;

/**
 * {@link BaseTableItemListingBuilder} implementation.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 * @param <C> Component type
 * @param <I> Internal instance
 * @param <B> Concrete builder type
 *
 * @since 5.0.0
 */
public abstract class AbstractTableItemListingBuilder<T, P, C extends ItemListing<T, P>, I extends DefaultItemListing<T, P>, B extends BaseTableItemListingBuilder<T, P, C, B>>
		extends AbstractItemListingBuilder<T, P, C, I, B, Table> implements BaseTableItemListingBuilder<T, P, C, B> {

	private TableFooterGenerator<T, P> footerGenerator;

	public AbstractTableItemListingBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#expandRatio(java.
	 * lang.Object, float)
	 */
	@Override
	public B expandRatio(P property, float expandRatio) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setTableExpandRatio(expandRatio);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#icon(java.lang.
	 * Object, com.vaadin.server.Resource)
	 */
	@Override
	public B icon(P property, Resource icon) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setIcon(icon);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#cacheRate(double)
	 */
	@Override
	public B cacheRate(double cacheRate) {
		getInstance().getTable().setCacheRate(cacheRate);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#pageLength(int)
	 */
	@Override
	public B pageLength(int pageLength) {
		getInstance().getTable().setPageLength(pageLength);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#footer(com.
	 * holonframework.vaadin.components.builders.ItemListingBuilder.TableFooterGenerator)
	 */
	@Override
	public B footer(TableFooterGenerator<T, P> footerGenerator) {
		ObjectUtils.argumentNotNull(footerGenerator, "Generator must be not null");
		this.footerGenerator = footerGenerator;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#
	 * withHeaderClickListener(com.vaadin.ui.Table.HeaderClickListener)
	 */
	@Override
	public B withHeaderClickListener(HeaderClickListener listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		getInstance().getTable().addHeaderClickListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#
	 * withFooterClickListener(com.vaadin.ui.Table.FooterClickListener)
	 */
	@Override
	public B withFooterClickListener(FooterClickListener listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		getInstance().getTable().addFooterClickListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#dragMode(com.vaadin.
	 * ui.Table.TableDragMode)
	 */
	@Override
	public B dragMode(TableDragMode dragMode) {
		getInstance().getTable().setDragMode(dragMode);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#dropHandler(com.
	 * vaadin.event.dd.DropHandler)
	 */
	@Override
	public B dropHandler(DropHandler dropHandler) {
		getInstance().getTable().setDropHandler(dropHandler);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#withActionHandler(
	 * com.vaadin.event.Action.Handler)
	 */
	@Override
	public B withActionHandler(Handler actionHandler) {
		ObjectUtils.argumentNotNull(actionHandler, "Handler must be not null");
		getInstance().getTable().addActionHandler(actionHandler);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseTableItemListingBuilder#rowGenerator(com.
	 * vaadin.ui.Table.RowGenerator)
	 */
	@Override
	public B rowGenerator(RowGenerator rowGenerator) {
		ObjectUtils.argumentNotNull(rowGenerator, "RowGenerator must be not null");
		getInstance().getTable().setRowGenerator(rowGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.builders.AbstractItemListingBuilder#configure(com.holonplatform.
	 * vaadin.internal.components.DefaultItemListing)
	 */
	@Override
	protected void configure(I instance) {
		if (footerGenerator != null) {
			instance.addItemSetChangeListener(e -> {
				if (instance.isFooterVisible()) {
					for (P property : instance.getPropertyColumns()) {
						instance.getTable().setColumnFooter(property, footerGenerator.getFooter(instance, property));
					}
				}
			});
		}
	}

}
