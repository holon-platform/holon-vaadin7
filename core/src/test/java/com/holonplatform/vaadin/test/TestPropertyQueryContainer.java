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
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.datastore.jdbc.spring.EnableJdbcDatastore;
import com.holonplatform.jdbc.spring.EnableDataSource;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.container.ItemDataSourceContainer;
import com.holonplatform.vaadin.internal.data.DatastoreItemDataProvider;
import com.holonplatform.vaadin.internal.data.container.PropertyBoxItemAdapter;
import com.holonplatform.vaadin.test.data.TestData;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPropertyQueryContainer.Config.class)
public class TestPropertyQueryContainer {

	@Configuration
	@PropertySource("test_datastore.properties")
	// @EnableJpa(entityPackageClasses = TestDataDomain.class, autoFlush=true)
	@EnableDataSource
	@EnableJdbcDatastore
	@EnableTransactionManagement
	protected static class Config {

		@Bean
		public PlatformTransactionManager transactionManager(DataSource dataSource) {
			return new DataSourceTransactionManager(dataSource);
		}

	}

	private final static DataTarget<?> TARGET = DataTarget.named("testdata");

	@Autowired
	private Datastore datastore;

	@SuppressWarnings("rawtypes")
	@Test
	public void testPropertyContainer() {

		ItemDataSourceContainer<PropertyBox, Property> container = ItemDataSourceContainer
				.<PropertyBox, Property>builder().dataSource(new TestPropertyDataProvider(datastore))
				.itemAdapter(new PropertyBoxItemAdapter()).withProperty(TestData.ID, TestData.ID.getType())
				.withProperty(TestData.DESCRIPTION, TestData.DESCRIPTION.getType())
				.withProperty(TestData.SEQUENCE, TestData.SEQUENCE.getType())
				.withProperty(TestData.OBSOLETE, TestData.OBSOLETE.getType())
				.fixedFilter(TestData.ID.isNotNull().and(TestData.SEQUENCE.lt(23))).fixedSort(TestData.ID.asc())
				.defaultSort(TestData.DESCRIPTION.asc()).build();

		int size = container.size();
		assertEquals(22, size);

		container.addQueryParameter("test", "val");
		assertEquals("val", container.getConfiguration().getQueryParameters().getParameter("test").orElse(null));

		container.removeQueryParameter("test");
		assertFalse(container.getConfiguration().getQueryParameters().hasParameter("test"));

		container = ItemDataSourceContainer.<PropertyBox, Property>builder()
				.dataSource(new TestPropertyDataProvider(datastore)).itemAdapter(new PropertyBoxItemAdapter())
				.withProperty(TestData.ID, TestData.ID.getType())
				.withProperty(TestData.DESCRIPTION, TestData.DESCRIPTION.getType())
				.withProperty(TestData.SEQUENCE, TestData.SEQUENCE.getType())
				.withProperty(TestData.OBSOLETE, TestData.OBSOLETE.getType())
				.fixedFilter(TestData.ID.isNotNull().and(TestData.SEQUENCE.lt(23))).fixedSort(TestData.ID.asc())
				.defaultSort(TestData.DESCRIPTION.asc()).itemIdentifier(i -> i.getValue(TestData.ID)).build();

		assertEquals("c20", container.firstItemId());

		QueryConfigurationProvider qcp = new QueryConfigurationProvider() {

			@Override
			public QuerySort getQuerySort() {
				return TestData.DESCRIPTION.desc();
			}

			@Override
			public ParameterSet getQueryParameters() {
				return null;
			}

			@Override
			public QueryFilter getQueryFilter() {
				return TestData.SEQUENCE.lt(21);
			}
		};

		container.addQueryConfigurationProvider(qcp);

		size = container.size();
		assertEquals(20, size);

		assertEquals("c7", container.firstItemId());

		container.sort(new Property[] { TestData.DESCRIPTION }, new boolean[] { true });

		assertEquals("c20", container.firstItemId());

		container.removeQueryConfigurationProvider(qcp);

		size = container.size();
		assertEquals(22, size);

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testPropertyContainerBuilder() {

		ItemDataSourceContainer<PropertyBox, Property> container = ItemDataSourceContainer
				.<PropertyBox, Property>builder().dataSource(new TestPropertyDataProvider(datastore))
				.itemAdapter(new PropertyBoxItemAdapter()).withProperty(TestData.ID, TestData.ID.getType())
				.withProperty(TestData.DESCRIPTION, TestData.DESCRIPTION.getType())
				.withProperty(TestData.SEQUENCE, TestData.SEQUENCE.getType())
				.withProperty(TestData.OBSOLETE, TestData.OBSOLETE.getType()).autoRefresh(false).build();

		int size = container.size();
		assertEquals(0, size);

		container.refresh();

		size = container.size();
		assertEquals(23, size);

		container = ItemDataSourceContainer.<PropertyBox, Property>builder()
				.dataSource(new TestPropertyDataProvider(datastore)).itemAdapter(new PropertyBoxItemAdapter())
				.withProperty(TestData.ID, TestData.ID.getType())
				.withProperty(TestData.DESCRIPTION, TestData.DESCRIPTION.getType())
				.withProperty(TestData.SEQUENCE, TestData.SEQUENCE.getType())
				.withProperty(TestData.OBSOLETE, TestData.OBSOLETE.getType())
				.itemIdentifier(i -> i.getValue(TestData.ID)).fixedFilter(TestData.SEQUENCE.goe(20))
				.withQueryConfigurationProvider(new QueryConfigurationProvider() {

					@Override
					public QuerySort getQuerySort() {
						return null;
					}

					@Override
					public ParameterSet getQueryParameters() {
						return null;
					}

					@Override
					public QueryFilter getQueryFilter() {
						return TestData.SEQUENCE.loe(20);
					}
				}).build();

		size = container.size();
		assertEquals(1, size);

		assertEquals("c20", container.firstItemId());

		boolean obs = (boolean) container.getItem("c20").getItemProperty(TestData.OBSOLETE).getValue();
		assertTrue(obs);

		Optional<PropertyBox> box = datastore.query().target(TARGET).filter(TestData.ID.eq("c20"))
				.findOne(TestData.PROPERTIES);
		assertTrue(box.isPresent());

		box.get().setValue(TestData.OBSOLETE, false);

		datastore.save(TARGET, box.get());

		container.refreshItem("c20");

		obs = (boolean) container.getItem("c20").getItemProperty(TestData.OBSOLETE).getValue();
		assertFalse(obs);
		
		datastore.bulkUpdate(TARGET).filter(TestData.ID.eq("c20")).set(TestData.OBSOLETE, Boolean.TRUE).execute();
		
		container = ItemDataSourceContainer.<PropertyBox, Property>builder()
				.dataSource(new TestPropertyDataProvider(datastore)).itemAdapter(new PropertyBoxItemAdapter())
				.withProperty(TestData.ID, TestData.ID.getType())
				.withProperty(TestData.DESCRIPTION, TestData.DESCRIPTION.getType())
				.withProperty(TestData.SEQUENCE, TestData.SEQUENCE.getType())
				.withProperty(TestData.OBSOLETE, TestData.OBSOLETE.getType()).queryParameter("test", "val")
				.fixedSort(TestData.ID.asc()).defaultSort(TestData.DESCRIPTION.asc())
				.fixedFilter(TestData.SEQUENCE.between(1, 2)).build();

		size = container.size();
		assertEquals(2, size);

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testPropertyContainerDatastore() {

		ItemDataSourceContainer<PropertyBox, Property> container = ItemDataSourceContainer
				.<PropertyBox, Property>builder().dataSource(new DatastoreItemDataProvider(datastore, TARGET))
				.itemAdapter(new PropertyBoxItemAdapter()).withProperty(TestData.ID, TestData.ID.getType())
				.withProperty(TestData.DESCRIPTION, TestData.DESCRIPTION.getType())
				.withProperty(TestData.SEQUENCE, TestData.SEQUENCE.getType())
				.withProperty(TestData.OBSOLETE, TestData.OBSOLETE.getType())
				.itemIdentifier(i -> i.getValue(TestData.ID)).fixedFilter(TestData.ID.neq("c1")).build();

		int size = container.size();
		assertEquals(22, size);

		QueryConfigurationProvider qcp = new QueryConfigurationProvider() {

			@Override
			public QuerySort getQuerySort() {
				return TestData.DESCRIPTION.desc();
			}

			@Override
			public ParameterSet getQueryParameters() {
				return null;
			}

			@Override
			public QueryFilter getQueryFilter() {
				return TestData.SEQUENCE.lt(23);
			}
		};
		container.addQueryConfigurationProvider(qcp);

		size = container.size();
		assertEquals(21, size);

		assertEquals("c7", container.firstItemId());

		boolean obs = (boolean) container.getItem("c20").getItemProperty(TestData.OBSOLETE).getValue();
		assertTrue(obs);
		
		
		Optional<PropertyBox> box = datastore.query().target(TARGET).filter(TestData.ID.eq("c20"))
				.findOne(TestData.PROPERTIES);
		assertTrue(box.isPresent());

		box.get().setValue(TestData.OBSOLETE, false);

		datastore.save(TARGET, box.get());

		container.refreshItem("c20");

		obs = (boolean) container.getItem("c20").getItemProperty(TestData.OBSOLETE).getValue();
		assertFalse(obs);
		
		datastore.bulkUpdate(TARGET).filter(TestData.ID.eq("c20")).set(TestData.OBSOLETE, Boolean.TRUE).execute();
		
	}

	@SuppressWarnings("serial")
	static class TestPropertyDataProvider implements ItemDataProvider<PropertyBox> {

		private final Datastore datastore;

		public TestPropertyDataProvider(Datastore datastore) {
			super();
			this.datastore = datastore;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.ItemSetLoader#load(com.holonplatform.vaadin.data.
		 * ItemDataSource.Configuration, int, int)
		 */
		@Override
		public Stream<PropertyBox> load(com.holonplatform.vaadin.data.ItemDataSource.Configuration<?> configuration,
				int offset, int limit) throws DataAccessException {
			Query q = datastore.query().target(TARGET);

			if (configuration.getQueryFilter() != null) {
				q.filter(configuration.getQueryFilter());
			}
			if (configuration.getQuerySort() != null) {
				q.sort(configuration.getQuerySort());
			}
			if (limit > 0) {
				q.limit(limit);
				q.offset(offset);
			}

			return q.stream(TestData.PROPERTIES);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.ItemSetCounter#size(com.holonplatform.vaadin.data.
		 * ItemDataSource.Configuration)
		 */
		@Override
		public long size(com.holonplatform.vaadin.data.ItemDataSource.Configuration<?> configuration)
				throws DataAccessException {
			Query q = datastore.query().target(TARGET);
			if (configuration.getQueryFilter() != null) {
				q.filter(configuration.getQueryFilter());
			}
			return q.count();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataProvider#refresh(com.holonplatform.vaadin.data.ItemDataSource.
		 * Configuration, java.lang.Object)
		 */
		@Override
		public PropertyBox refresh(com.holonplatform.vaadin.data.ItemDataSource.Configuration<?> configuration,
				PropertyBox item) throws UnsupportedOperationException, DataAccessException {
			return datastore.refresh(TARGET, item);
		}

	}

}
