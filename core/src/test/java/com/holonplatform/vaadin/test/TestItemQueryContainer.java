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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.Test;

import com.holonplatform.core.Path;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.query.QueryUtils;
import com.holonplatform.core.internal.query.filter.EqualFilter;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TestUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryExpression;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.CompositeQuerySort;
import com.holonplatform.core.query.QuerySort.PathQuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.container.ItemAdapter;
import com.holonplatform.vaadin.data.container.ItemDataSourceContainer;
import com.holonplatform.vaadin.internal.data.container.PropertyBoxItemAdapter;
import com.holonplatform.vaadin.test.data.TestBean;
import com.holonplatform.vaadin.test.data.TestDataDomain;
import com.holonplatform.vaadin.test.data.TestDataItem;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.IsNull;

public class TestItemQueryContainer {

	@SuppressWarnings("serial")
	private final static ItemAdapter<TestDataDomain> ADAPTER = new ItemAdapter<TestDataDomain>() {

		@Override
		public Item adapt(Configuration<?> configuration, TestDataDomain item) {
			return new TestDataItem(item.getCode(), item.getDescription(), item.getSequence(), item.getObsolete());
		}

		@Override
		public TestDataDomain restore(Configuration<?> configuration, Item item) {
			TestDataDomain d = new TestDataDomain();
			d.setCode((String) item.getItemProperty("code").getValue());
			d.setDescription((String) item.getItemProperty("description").getValue());
			d.setSequence((Integer) item.getItemProperty("sequence").getValue());
			d.setObsolete((Integer) item.getItemProperty("obsolete").getValue());
			return d;
		}
	};

	@Test
	public void testItemQueryContainer() {

		ItemDataSourceContainer<TestDataDomain, String> container = ItemDataSourceContainer
				.<TestDataDomain, String>builder().dataSource(new TestItemDataProvider()).batchSize(10)
				.itemAdapter(ADAPTER).withSortableProperty("code", String.class)
				.withSortableProperty("description", String.class).withSortableProperty("sequence", Integer.class)
				.withSortableProperty("obsolete", int.class).build();

		int size = container.size();
		assertEquals(23, size);

		container.addContainerFilter(new Compare.Equal("description", "test eq"));

		size = container.size();
		assertEquals(3, size);

		container.removeAllContainerFilters();

		size = container.size();
		assertEquals(23, size);

		Item itm = container.getItem(0);
		assertNotNull(itm);

		container.sort(new String[] { "code" }, new boolean[] { true });

		itm = container.getItem(0);
		assertNotNull(itm);

		assertEquals("c1", itm.getItemProperty("code").getValue());

		container.sort(new String[] { "code" }, new boolean[] { false });

		itm = container.getItem(0);
		assertNotNull(itm);

		assertEquals(9, itm.getItemProperty("sequence").getValue());

		assertEquals(23, container.getItemIds().size());

		assertEquals(0, container.getIdByIndex(0));

		assertEquals(4, container.getContainerPropertyIds().size());

	}

	@Test
	public void testItemQueryContainerId() {

		ItemDataSourceContainer<TestDataDomain, String> container = ItemDataSourceContainer
				.<TestDataDomain, String>builder().dataSource(new TestItemDataProvider()).itemAdapter(ADAPTER)
				.batchSize(10).withSortableProperty("code", String.class)
				.withSortableProperty("description", String.class).withSortableProperty("sequence", Integer.class)
				.withSortableProperty("obsolete", int.class).itemIdentifier(i -> i.getCode()).build();

		int size = container.size();
		assertEquals(23, size);

		Item itm = container.getItem("c2");
		assertNotNull(itm);

		assertEquals("c2", itm.getItemProperty("code").getValue());

		container.sort(new String[] { "sequence" }, new boolean[] { true });

		int idx = container.indexOfId("c20");
		assertEquals(19, idx);

	}

	@Test
	public void testItemQueryContainerAutoRefresh() {

		ItemDataSourceContainer<TestDataDomain, String> container = ItemDataSourceContainer
				.<TestDataDomain, String>builder().dataSource(new TestItemDataProvider()).itemAdapter(ADAPTER)
				.autoRefresh(false).batchSize(10).withSortableProperty("code", String.class)
				.withSortableProperty("description", String.class).withSortableProperty("sequence", Integer.class)
				.withSortableProperty("obsolete", int.class).itemIdentifier(i -> i.getCode()).build();

		int size = container.size();
		assertEquals(0, size);

		container.refresh();

		size = container.size();
		assertEquals(23, size);

		container.clear();

		size = container.size();
		assertEquals(0, size);

		container.refresh();

		size = container.size();
		assertEquals(23, size);

	}

	@SuppressWarnings({ "unchecked", "serial" })
	@Test
	public void testBuilder() {

		final AtomicInteger cc = new AtomicInteger(0);
		final AtomicInteger mc = new AtomicInteger(0);
		final AtomicInteger isc = new AtomicInteger(0);

		final CommitHandler<TestDataDomain> commitHandler = new CommitHandler<TestDataDomain>() {

			@Override
			public void commit(Collection<TestDataDomain> addedItems, Collection<TestDataDomain> modifiedItems,
					Collection<TestDataDomain> removedItems) {
				cc.incrementAndGet();
				if (modifiedItems != null && modifiedItems.size() > 0) {
					mc.incrementAndGet();
				}
			}
		};

		ItemDataSourceContainer<TestDataDomain, String> container = ItemDataSourceContainer
				.<TestDataDomain, String>builder().dataSource(new TestItemDataProvider()).itemAdapter(ADAPTER)
				.autoRefresh(true).batchSize(10).maxCacheSize(100).itemIdentifier(i -> i.getCode())
				.withProperty("code", String.class).withSortableProperty("description", String.class)
				.withSortableProperty("sequence", Integer.class).withProperty("obsolete", int.class)
				.sortable("code", true).sortable("obsolete", false).defaultValue("obsolete", 0)
				.readOnly("obsolete", true).commitHandler(commitHandler).buffered(true)
				.withItemSetChangeListener(new ItemSetChangeListener() {

					@Override
					public void containerItemSetChange(ItemSetChangeEvent event) {
						assertNotNull(event.getContainer());
						isc.incrementAndGet();
					}
				}).build();

		Collection<String> pids = ConversionUtils.iterableAsList(container.getConfiguration().getProperties());
		assertNotNull(pids);
		assertEquals(4, pids.size());

		Collection<?> sids = container.getSortableContainerPropertyIds();
		assertNotNull(sids);
		assertEquals(3, sids.size());

		int size = container.size();
		assertEquals(23, size);

		Item itm = container.getItem("c1");
		assertNotNull(itm);
		com.vaadin.data.Property<String> p = itm.getItemProperty("description");
		assertNotNull(p);

		assertFalse(container.isModified());

		p.setValue("MOD_DES");

		assertTrue(container.isModified());

		container.commit();

		assertFalse(container.isModified());

		assertEquals(1, cc.get());
		assertEquals(1, mc.get());

		container.refresh();

		assertEquals(1, isc.get());

		container = ItemDataSourceContainer.<TestDataDomain, String>builder().dataSource(new TestItemDataProvider())
				.itemAdapter(ADAPTER).autoRefresh(false).itemIdentifier(i -> i.getCode())
				.withReadOnlyProperty("code", String.class).withSortableProperty("description", String.class)
				.withSortableProperty("sequence", Integer.class).withReadOnlySortableProperty("obsolete", int.class)
				.build();

		container.refresh();

		assertTrue(container.containsId("c1"));

		p = container.getContainerProperty("c1", "code");
		assertNotNull(p);

		container.sort(new String[] { "sequence" }, new boolean[] { false });

		assertTrue(container.isFirstId("c23"));
		assertTrue(container.isLastId("c1"));

		assertEquals("c23", container.firstItemId());
		assertEquals("c1", container.lastItemId());

		container.sort(new String[] { "sequence" }, new boolean[] { true });

		assertEquals("c2", container.nextItemId("c1"));
		assertEquals("c1", container.prevItemId("c2"));

		List<?> ids = container.getItemIds(1, 2);
		assertNotNull(ids);
		assertEquals(2, ids.size());
		assertEquals("c2", ids.get(0));
		assertEquals("c3", ids.get(1));

		final AtomicInteger psc2 = new AtomicInteger(0);

		final PropertySetChangeListener pscl = new PropertySetChangeListener() {

			@Override
			public void containerPropertySetChange(PropertySetChangeEvent event) {
				psc2.incrementAndGet();
			}
		};
		container.addPropertySetChangeListener(pscl);

		container.addContainerProperty("testp", String.class, "x");
		assertEquals(1, psc2.get());

		container.removeContainerProperty("testp");
		assertEquals(2, psc2.get());

		container.removePropertySetChangeListener(pscl);

		final Filter flt = new IsNull("code");
		container.addContainerFilter(flt);
		assertEquals(1, container.getContainerFilters().size());

		container.removeContainerFilter(flt);
		assertEquals(0, container.getContainerFilters().size());

		container.addContainerFilter(flt);
		container.removeAllContainerFilters();
		assertEquals(0, container.getContainerFilters().size());

		assertTrue(container.removeItem("c1"));

		final AtomicInteger gic = new AtomicInteger(0);

		final TestItemDataProvider q2 = new TestItemDataProvider() {

			@Override
			public TestDataDomain refresh(TestDataDomain item)
					throws UnsupportedOperationException, DataAccessException {
				gic.incrementAndGet();
				TestDataDomain d = new TestDataDomain();
				d.setCode(item.getCode());
				d.setDescription("refreshed");
				d.setSequence(999);
				d.setObsolete(0);
				return d;
			}

		};

		ItemDataSourceContainer<TestDataDomain, String> container2 = ItemDataSourceContainer
				.<TestDataDomain, String>builder().dataSource(q2).itemAdapter(ADAPTER).autoRefresh(false)
				.itemIdentifier(i -> i.getCode()).withReadOnlyProperty("code", String.class)
				.commitHandler(commitHandler).build();

		container2.refresh();
		container2.refreshItem("c20");

		assertEquals(1, gic.get());

		TestUtils.expectedException(UnsupportedOperationException.class, new Runnable() {

			@Override
			public void run() {
				container2.addItemAfter(null);
			}
		});
		TestUtils.expectedException(UnsupportedOperationException.class, new Runnable() {

			@Override
			public void run() {
				container2.addItemAfter(null, null);
			}
		});
		TestUtils.expectedException(UnsupportedOperationException.class, new Runnable() {

			@Override
			public void run() {
				container2.addItemAt(0);
			}
		});
		TestUtils.expectedException(UnsupportedOperationException.class, new Runnable() {

			@Override
			public void run() {
				container2.addItemAt(0, null);
			}
		});
		TestUtils.expectedException(UnsupportedOperationException.class, new Runnable() {

			@Override
			public void run() {
				container2.removeAllItems();
			}
		});

		container2.discard();

		assertFalse(container2.isModified());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testPropertyDataSource() {

		final Property<String> CODE = PathProperty.create("code", String.class);
		final Property<String> TEXT = PathProperty.create("text", String.class);

		final List<TestBean> data = new LinkedList<>();
		data.add(new TestBean("a", "A"));
		data.add(new TestBean("b", "B"));
		data.add(new TestBean("c", "C"));

		ItemDataSourceContainer<PropertyBox, Property> container = ItemDataSourceContainer
				.<PropertyBox, Property>builder()
				.dataSource(ItemDataProvider.create(c -> 3,
						(c, o, l) -> data.stream()
								.map((t) -> PropertyBox.builder(PropertySet.of(CODE, TEXT)).set(CODE, t.getCode())
										.set(TEXT, t.getText()).build())))
				.itemAdapter(new PropertyBoxItemAdapter()).withProperty(CODE, CODE.getType())
				.withProperty(TEXT, TEXT.getType()).itemIdentifier(i -> i.getValue(CODE)).autoRefresh(true).build();

		assertEquals(3, container.size());

		Item itm = container.getItem("a");
		assertNotNull(itm);
		assertEquals("A", itm.getItemProperty(TEXT).getValue());
		itm = container.getItem("b");
		assertNotNull(itm);
		assertEquals("B", itm.getItemProperty(TEXT).getValue());
		itm = container.getItem("c");
		assertNotNull(itm);
		assertEquals("C", itm.getItemProperty(TEXT).getValue());

	}

	@SuppressWarnings("serial")
	class TestItemDataProvider implements ItemDataProvider<TestDataDomain> {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.ItemSetCounter#size(com.holonplatform.vaadin.data.
		 * ItemDataSource.Configuration)
		 */
		@Override
		public long size(QueryConfigurationProvider configuration) throws DataAccessException {
			StringBuilder sb = new StringBuilder();
			sb.append("select count(*) from testdata");
			setupQuery(configuration, sb, false);

			try (Connection connection = getConnection();
					Statement stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(sb.toString())) {
				rs.next();
				return rs.getLong(1);
			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.ItemSetLoader#load(com.holonplatform.vaadin.data.
		 * ItemDataSource.Configuration, int, int)
		 */
		@Override
		public Stream<TestDataDomain> load(QueryConfigurationProvider configuration, int offset, int limit)
				throws DataAccessException {
			StringBuilder sb = new StringBuilder();
			sb.append("select * from testdata");
			setupQuery(configuration, sb, true);

			if (limit > 0) {
				sb.append(" limit ");
				sb.append(limit);
				sb.append(" offset ");
				sb.append(offset);
			}

			List<TestDataDomain> items = new ArrayList<>();

			try (Connection connection = getConnection();
					Statement stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(sb.toString())) {

				while (rs.next()) {
					TestDataDomain d = new TestDataDomain();
					d.setCode(rs.getString("code"));
					d.setDescription(rs.getString("description"));
					d.setSequence(rs.getInt("sequence"));
					d.setObsolete(rs.getInt("obsolete"));
					items.add(d);
				}

			} catch (SQLException e) {
				throw new DataAccessException(e);
			}

			return items.stream();
		}

		@SuppressWarnings("rawtypes")
		private void setupQuery(QueryConfigurationProvider configuration, StringBuilder sb, boolean applySorts) {
			QueryFilter filter = configuration.getQueryFilter();
			if (filter != null) {
				if (filter instanceof EqualFilter) {
					EqualFilter ef = (EqualFilter) filter;
					sb.append(" where ");
					sb.append(((Path) ef.getLeftOperand()).getName());
					sb.append(" = '");
					sb.append(QueryUtils.getConstantExpressionValue((QueryExpression<?>) ef.getRightOperand().get()));
					sb.append("'");
				}
			}

			if (applySorts) {
				QuerySort sort = configuration.getQuerySort();
				if (sort != null) {
					sb.append(" order by ");
					if (sort instanceof CompositeQuerySort) {
						List<QuerySort> sorts = ((CompositeQuerySort) sort).getComposition();
						sorts.forEach(s -> {
							if (s instanceof PathQuerySort) {
								sb.append(((PathQuerySort) s).getPath().getName());
								sb.append(" ");
								sb.append(
										((PathQuerySort) s).getDirection() == SortDirection.ASCENDING ? "asc" : "desc");
								sb.append(" ");
							}
						});
					} else {
						if (sort instanceof PathQuerySort) {
							sb.append(((PathQuerySort) sort).getPath().getName());
							sb.append(" ");
							sb.append(
									((PathQuerySort) sort).getDirection() == SortDirection.ASCENDING ? "asc" : "desc");
						}
					}
				}
			}

			/*
			 * if (applySorts && !configuration.getItemSorts().isEmpty()) {
			 * sb.append(configuration.getItemSorts().stream() .map(s -> s.getProperty() + " " + (s.isAscending() ?
			 * "asc" : "desc")) .collect(Collectors.joining(",", " order by ", ""))); }
			 */
		}

		private Connection getConnection() throws DataAccessException {
			try {
				return DriverManager.getConnection("jdbc:h2:mem:testdb1;INIT=RUNSCRIPT FROM 'classpath:test-db.sql'",
						"sa", "");
			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
		}

	}

}
