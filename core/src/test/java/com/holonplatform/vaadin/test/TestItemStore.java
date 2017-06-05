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
package com.holonplatform.vaadin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.Test;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.ItemDataSource.ItemSort;
import com.holonplatform.vaadin.internal.data.DefaultItemStore;
import com.holonplatform.vaadin.internal.data.ItemStore;
import com.holonplatform.vaadin.test.data.TestData;
import com.vaadin.data.Property;

public class TestItemStore {

	private final AtomicInteger scount = new AtomicInteger();
	private final AtomicInteger qcount = new AtomicInteger();
	private final AtomicInteger rcount = new AtomicInteger();

	@Test
	public void testStore() {

		ItemStore<String> store = new DefaultItemStore<>(new TestConfiguration(0), new TestDataProvider(),
				ItemIdentifierProvider.identity(), 4);

		assertEquals(4, store.getMaxCacheSize());

		assertEquals(5, store.getItemIds().size());

		assertEquals(1, scount.get());

		int size = store.size();
		assertEquals(5, size);

		assertEquals(1, scount.get());
		assertEquals(0, qcount.get());

		String itm = store.getItem(1);
		assertNotNull(itm);
		assertEquals("b", itm);

		assertEquals(1, qcount.get());

		itm = store.getItem(0);
		assertNotNull(itm);
		assertEquals("a", itm);

		assertEquals(1, qcount.get());

		itm = store.getItem(2);
		assertNotNull(itm);
		assertEquals("c", itm);

		assertEquals(2, qcount.get());

		itm = store.getItem(0);
		assertNotNull(itm);
		assertEquals("a", itm);

		assertEquals(2, qcount.get());

		itm = store.getItem(4);
		assertNotNull(itm);
		assertEquals("e", itm);

		assertEquals(3, qcount.get());

		itm = store.getItem(3);
		assertNotNull(itm);
		assertEquals("d", itm);

		assertEquals(3, qcount.get());

		itm = store.getItem(1);
		assertNotNull(itm);
		assertEquals("b", itm);

		assertEquals(4, qcount.get());

		store.reset(false, false);

		itm = store.getItem(1);
		assertNotNull(itm);
		assertEquals("b", itm);

		assertEquals(5, qcount.get());

		assertFalse(store.isModified());
		assertEquals(0, store.getAddedItems().size());
		assertEquals(0, store.getModifiedItems().size());
		assertEquals(0, store.getRemovedItems().size());

		store.setItemModified("b");

		assertTrue(store.isModified());
		assertEquals(1, store.getModifiedItems().size());

		assertEquals(5, qcount.get());

		store.removeItem(2);
		assertEquals(1, store.getRemovedItems().size());

		assertEquals(6, qcount.get());

		store.addItem("f");
		assertEquals(1, store.getAddedItems().size());

		assertEquals(6, qcount.get());

		itm = store.getItem(0);
		assertNotNull(itm);
		assertEquals("f", itm);

		assertEquals(6, qcount.get());

		assertTrue(store.containsItem("f"));

		store.refreshItem("a");

		assertEquals(6, qcount.get());

		store.refreshItem("f");

		assertEquals(6, qcount.get());

		assertEquals(2, rcount.get());
	}

	@Test
	public void testStoreMaxSize() {

		ItemStore<String> store = new DefaultItemStore<>(new TestConfiguration(2), new TestDataProvider(),
				null, 100);

		assertEquals(2, store.size());

	}

	@SuppressWarnings("serial")
	private final class TestDataProvider implements ItemDataProvider<String> {

		private final List<String> data;

		public TestDataProvider() {
			this.data = new LinkedList<>();
			this.data.add("a");
			this.data.add("b");
			this.data.add("c");
			this.data.add("d");
			this.data.add("e");
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.ItemSetCounter#size(com.holonplatform.vaadin.data.
		 * ItemDataSource.Configuration)
		 */
		@Override
		public long size(Configuration<?> configuration) throws DataAccessException {
			scount.incrementAndGet();
			return data.size();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.ItemSetLoader#load(com.holonplatform.vaadin.data.
		 * ItemDataSource.Configuration, int, int)
		 */
		@Override
		public Stream<String> load(Configuration<?> configuration, int offset, int limit) throws DataAccessException {
			qcount.incrementAndGet();
			return data.stream().skip(offset).limit(limit);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataProvider#refresh(com.holonplatform.vaadin.data.ItemDataSource.
		 * Configuration, java.lang.Object)
		 */
		@Override
		public String refresh(Configuration<?> configuration, String item)
				throws UnsupportedOperationException, DataAccessException {
			rcount.incrementAndGet();
			return item;
		}

	}

	@SuppressWarnings("serial")
	private final class TestConfiguration implements Configuration<Property<?>> {

		private final int maxSize;

		public TestConfiguration(int maxSize) {
			this.maxSize = maxSize;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQueryFilter()
		 */
		@Override
		public QueryFilter getQueryFilter() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getProperties()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Iterable<Property<?>> getProperties() {
			return (Iterable<Property<?>>) TestData.PROPERTIES;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getPropertyType(java.lang.Object)
		 */
		@Override
		public Class<?> getPropertyType(Property<?> property) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#isPropertyReadOnly(java.lang.Object)
		 */
		@Override
		public boolean isPropertyReadOnly(Property<?> property) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#isPropertySortable(java.lang.Object)
		 */
		@Override
		public boolean isPropertySortable(Property<?> property) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getPropertyDefaultValue(java.lang.Object)
		 */
		@Override
		public Object getPropertyDefaultValue(Property<?> property) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#isAutoRefresh()
		 */
		@Override
		public boolean isAutoRefresh() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getItemSorts()
		 */
		@Override
		public List<ItemSort<Property<?>>> getItemSorts() {
			return Collections.emptyList();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.querycontainer.ItemQueryDefinition#getBatchSize()
		 */
		@Override
		public int getBatchSize() {
			return 2;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.querycontainer.ItemQueryDefinition#getMaxSize()
		 */
		@Override
		public int getMaxSize() {
			return maxSize;
		}

	}

}
