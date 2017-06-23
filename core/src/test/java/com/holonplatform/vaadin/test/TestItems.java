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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.internal.query.QueryUtils;
import com.holonplatform.core.internal.query.filter.AndFilter;
import com.holonplatform.core.internal.query.filter.BetweenFilter;
import com.holonplatform.core.internal.query.filter.EqualFilter;
import com.holonplatform.core.internal.query.filter.GreaterFilter;
import com.holonplatform.core.internal.query.filter.LessFilter;
import com.holonplatform.core.internal.query.filter.NotFilter;
import com.holonplatform.core.internal.query.filter.NullFilter;
import com.holonplatform.core.internal.query.filter.OrFilter;
import com.holonplatform.core.internal.query.filter.StringMatchFilter;
import com.holonplatform.core.internal.utils.TestUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QueryFilter.CompositeQueryFilter;
import com.holonplatform.core.query.QueryFilter.OperationQueryFilter;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.ItemDataSource.ItemSort;
import com.holonplatform.vaadin.data.container.PropertyBoxItem;
import com.holonplatform.vaadin.internal.data.ItemCacheMap;
import com.holonplatform.vaadin.internal.data.NaturalNumberIdsList;
import com.holonplatform.vaadin.internal.data.container.ContainerUtils;
import com.holonplatform.vaadin.test.data.TestData;
import com.holonplatform.vaadin.test.data.TestDataItem;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;

public class TestItems {

	@SuppressWarnings({ "rawtypes", "serial" })
	private final static Configuration<PathProperty> CFG = new Configuration<PathProperty>() {

		@Override
		public QueryFilter getQueryFilter() {
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Iterable<PathProperty> getProperties() {
			return (Iterable<PathProperty>) TestData.PROPERTIES;
		}

		@Override
		public Class<?> getPropertyType(PathProperty property) {
			return property.getType();
		}

		@Override
		public boolean isPropertyReadOnly(PathProperty property) {
			return property.isReadOnly();
		}

		@Override
		public boolean isPropertySortable(PathProperty property) {
			return true;
		}

		@Override
		public Object getPropertyDefaultValue(PathProperty property) {
			return null;
		}

		@Override
		public int getBatchSize() {
			return 10;
		}

		@Override
		public int getMaxSize() {
			return 100;
		}

		@Override
		public boolean isAutoRefresh() {
			return true;
		}

		@Override
		public List<ItemSort<PathProperty>> getItemSorts() {
			return Collections.emptyList();
		}

	};

	@Test
	public void testWellDefined() {
		TestUtils.checkUtilityClass(ContainerUtils.class);
	}

	@Test
	public void testPropertyBoxItem() {
		PropertyBox box = PropertyBox.builder(TestData.PROPERTIES).set(TestData.ID, "id")
				.set(TestData.DESCRIPTION, "des").set(TestData.SEQUENCE, 1).set(TestData.OBSOLETE, true).build();

		final Item item = PropertyBoxItem.create(box);

		Collection<?> ids = item.getItemPropertyIds();
		assertNotNull(ids);
		assertEquals(4, ids.size());
		assertTrue(ids.contains(TestData.ID));
		assertTrue(ids.contains(TestData.DESCRIPTION));
		assertTrue(ids.contains(TestData.SEQUENCE));
		assertTrue(ids.contains(TestData.OBSOLETE));

		Property<?> p = item.getItemProperty(TestData.ID);
		assertNotNull(p);
		Class<?> type = p.getType();
		assertEquals(String.class, type);
		Object value = p.getValue();
		assertEquals("id", value);

		p = item.getItemProperty(TestData.DESCRIPTION);
		assertNotNull(p);
		type = p.getType();
		assertEquals(String.class, type);
		value = p.getValue();
		assertEquals("des", value);

		p = item.getItemProperty(TestData.SEQUENCE);
		assertNotNull(p);
		type = p.getType();
		assertEquals(Integer.class, type);
		value = p.getValue();
		assertEquals(1, value);

		p = item.getItemProperty(TestData.OBSOLETE);
		assertNotNull(p);
		type = p.getType();
		assertTrue(Boolean.class.isAssignableFrom(type));
		value = p.getValue();
		assertEquals(Boolean.TRUE, value);

		p = item.getItemProperty(TestData.OBSOLETE);
		assertNotNull(p);

		p = item.getItemProperty("invalid");
		assertNull(p);

		TestUtils.expectedException(UnsupportedOperationException.class, new Runnable() {

			@Override
			public void run() {
				item.addItemProperty("invalid", null);
			}
		});

		TestUtils.expectedException(UnsupportedOperationException.class, new Runnable() {

			@Override
			public void run() {
				item.removeItemProperty("invalid");
			}
		});

		Item item2 = PropertyBoxItem.create(box);

		p = item2.getItemProperty(TestData.ID);
		assertNotNull(p);
		assertFalse(p.isReadOnly());

	}

	@Test
	public void testContainerUtils() {
		assertNotNull(ContainerUtils.getQueryExpression(TestData.ID, CFG));

		assertNull(ContainerUtils.getQueryExpression(null, CFG));

		TestUtils.expectedException(InvalidExpressionException.class, new Callable<Void>() {

			@Override
			public Void call() throws InvalidExpressionException {
				ContainerUtils.getQueryExpression("invalid", CFG);
				return null;
			}
		});
	}

	@Test
	public void testContainerFilters() {
		Filter filter = new IsNull(TestData.ID);
		QueryFilter qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.ID, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof NullFilter);

		filter = new IsNull("code");
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.ID, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof NullFilter);

		filter = new SimpleStringFilter(TestData.ID, "test", false, false);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.ID, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof StringMatchFilter);
		StringMatchFilter lf = (StringMatchFilter) qf;
		assertFalse(lf.isIgnoreCase());
		assertEquals("test", QueryUtils.getConstantExpressionValue(lf.getRightOperand().get()));

		filter = new SimpleStringFilter(TestData.ID, "test", false, true);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.ID, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof StringMatchFilter);
		lf = (StringMatchFilter) qf;
		assertFalse(lf.isIgnoreCase());
		assertEquals("test", QueryUtils.getConstantExpressionValue(lf.getRightOperand().get()));

		filter = new SimpleStringFilter(TestData.ID, "test", true, true);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.ID, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof StringMatchFilter);
		lf = (StringMatchFilter) qf;
		assertTrue(lf.isIgnoreCase());
		assertEquals("test", QueryUtils.getConstantExpressionValue(lf.getRightOperand().get()));

		filter = new Like(TestData.ID, "%test%", false);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.ID, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof StringMatchFilter);
		lf = (StringMatchFilter) qf;
		assertTrue(lf.isIgnoreCase());
		assertEquals("test", QueryUtils.getConstantExpressionValue(lf.getRightOperand().get()));

		filter = new Equal(TestData.ID, "test");
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.ID, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof EqualFilter);
		assertEquals("test", QueryUtils.getConstantExpressionValue(((EqualFilter<?>) qf).getRightOperand().get()));

		filter = new Compare.Greater(TestData.SEQUENCE, 1);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.SEQUENCE, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof GreaterFilter);
		GreaterFilter<?> cf = (GreaterFilter<?>) qf;
		assertFalse(cf.isIncludeEquals());

		filter = new Compare.GreaterOrEqual(TestData.SEQUENCE, 1);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.SEQUENCE, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof GreaterFilter);
		cf = (GreaterFilter<?>) qf;
		assertTrue(cf.isIncludeEquals());

		filter = new Compare.Less(TestData.SEQUENCE, 1);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.SEQUENCE, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof LessFilter);

		filter = new Compare.LessOrEqual(TestData.SEQUENCE, 1);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.SEQUENCE, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof LessFilter);

		filter = new Between(TestData.SEQUENCE, 1, 2);
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertEquals(TestData.SEQUENCE, ((OperationQueryFilter<?>) qf).getLeftOperand());
		assertTrue(qf instanceof BetweenFilter);
		BetweenFilter<?> bf = (BetweenFilter<?>) qf;
		assertEquals(1, bf.getFromValue());
		assertEquals(2, bf.getToValue());

		filter = new Not(new IsNull(TestData.DESCRIPTION));
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertTrue(qf instanceof NotFilter);
		NotFilter nf = (NotFilter) qf;
		assertNotNull(nf.getComposition());
		assertTrue(nf.getComposition().get(0) instanceof NullFilter);

		filter = new And(new IsNull(TestData.DESCRIPTION), new Equal(TestData.SEQUENCE, 1));
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertTrue(qf instanceof AndFilter);
		CompositeQueryFilter af = (CompositeQueryFilter) qf;
		assertNotNull(af.getComposition());
		assertTrue(af.getComposition().get(0) instanceof NullFilter);
		assertTrue(af.getComposition().get(1) instanceof EqualFilter);

		filter = new Or(new IsNull(TestData.DESCRIPTION), new Equal(TestData.SEQUENCE, 1));
		qf = ContainerUtils.convertContainerFilter(CFG, filter).orElse(null);
		assertNotNull(qf);
		assertTrue(qf instanceof OrFilter);
		af = (CompositeQueryFilter) qf;
		assertNotNull(af.getComposition());
		assertTrue(af.getComposition().get(0) instanceof NullFilter);
		assertTrue(af.getComposition().get(1) instanceof EqualFilter);

		List<Filter> filters = new LinkedList<>();
		filters.add(new IsNull(TestData.DESCRIPTION));
		filters.add(new Equal(TestData.SEQUENCE, 1));
		qf = ContainerUtils.convertContainerFilters(CFG, filters).orElse(null);
		assertNotNull(qf);
		assertTrue(qf instanceof AndFilter);
		af = (CompositeQueryFilter) qf;
		assertNotNull(af.getComposition());
		assertTrue(af.getComposition().get(0) instanceof NullFilter);
		assertTrue(af.getComposition().get(1) instanceof EqualFilter);

		assertFalse(ContainerUtils.convertContainerFilter(CFG, null).isPresent());
		assertFalse(ContainerUtils.convertContainerFilters(CFG, null).isPresent());
		assertFalse(ContainerUtils.convertContainerFilters(CFG, Collections.emptyList()).isPresent());
	}

	@Test
	public void testIdsList() {
		NaturalNumberIdsList lst = new NaturalNumberIdsList(3);
		assertEquals(3, lst.size());

		assertEquals(new Integer(1), lst.get(1));
		assertEquals(-1, lst.indexOf(4));
	}

	@Test
	public void testItemCache() {
		ItemCacheMap<TestDataItem> c = new ItemCacheMap<>(10);
		c.putItem(0, new TestDataItem("c", null, null, 0));

		assertTrue(c.containsItem(0));
		assertFalse(c.containsItem(1));
	}

}
