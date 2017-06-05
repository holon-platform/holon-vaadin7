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
package com.holonplatform.vaadin.internal.data;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;

/**
 * An {@link ItemDataProvider} using a {@link Datastore} to perform item set count and load operations, using
 * {@link Property} as item property type and {@link PropertyBox} as concrete item data container.
 * 
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public class DatastoreItemDataProvider implements ItemDataProvider<PropertyBox> {

	private static final long serialVersionUID = -3647676181555142846L;

	/**
	 * Datastore
	 */
	private final Datastore datastore;

	/**
	 * Data target
	 */
	private final DataTarget<?> target;

	/**
	 * Property set
	 */
	private final PropertySet<?> propertySet;

	/**
	 * Construct a new DatastoreItemDataProvider. Property set is not provided, and will be obtained from data source
	 * {@link Configuration} properties.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 */
	public DatastoreItemDataProvider(Datastore datastore, DataTarget<?> target) {
		this(datastore, target, null);
	}

	/**
	 * Construct a new DatastoreItemDataProvider.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param propertySet Property set to load
	 */
	public DatastoreItemDataProvider(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet) {
		super();
		ObjectUtils.argumentNotNull(datastore, "Datastore must be not null");
		ObjectUtils.argumentNotNull(target, "DataTarget must be not null");
		this.datastore = datastore;
		this.target = target;
		this.propertySet = propertySet;
	}

	/**
	 * Get the {@link Datastore} to use to perform count and load operations.
	 * @return the datastore
	 */
	protected Datastore getDatastore() {
		return datastore;
	}

	/**
	 * Get the data target to use.
	 * @return the data target
	 */
	protected DataTarget<?> getTarget() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.ItemDataSource.ItemSetCounter#size(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration)
	 */
	@Override
	public long size(Configuration<?> configuration) throws DataAccessException {
		try {
			return buildQuery(configuration, false).count();
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.ItemDataSource.ItemSetLoader#load(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration, int, int)
	 */
	@Override
	public Stream<PropertyBox> load(Configuration<?> configuration, int offset, int limit) throws DataAccessException {
		Query q = buildQuery(configuration, true);
		// paging
		if (limit > 0) {
			q.limit(limit);
			q.offset(offset);
		}
		// property set
		PropertySet<?> projection = propertySet;
		if (projection == null) {
			// build from properties
			List<Property> ps = new LinkedList<>();
			configuration.getProperties().forEach(p -> {
				if (Property.class.isAssignableFrom(p.getClass())) {
					ps.add((Property) p);
				}
			});
			if (ps.isEmpty()) {
				throw new DataAccessException("Cannot load PropertyBox items, no explicit property set provided and"
						+ " data source properties are not of [" + Property.class.getName() + "] type");
			}
			projection = PropertySet.of(ps);
		}
		// execute
		return q.stream(projection);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataProvider#refresh(com.holonplatform.vaadin.data.ItemDataSource.
	 * Configuration, java.lang.Object)
	 */
	@Override
	public PropertyBox refresh(Configuration<?> configuration, PropertyBox item)
			throws UnsupportedOperationException, DataAccessException {
		return getDatastore().refresh(getTarget(), item);
	}

	/**
	 * Build a {@link Query} using the Datastore and configuring query filters and sorts.
	 * @param configuration Data source configuration
	 * @param withSorts Whether to apply sorts, if any, to query
	 * @return Query instance
	 */
	protected Query buildQuery(Configuration<?> configuration, boolean withSorts) {
		Query q = getDatastore().query();
		// target
		if (getTarget() != null) {
			q.target(getTarget());
		}
		// filter
		QueryFilter filter = configuration.getQueryFilter();
		if (filter != null) {
			q.filter(filter);
		}
		// sort
		if (withSorts) {
			QuerySort sort = configuration.getQuerySort();
			if (sort != null) {
				q.sort(sort);
			}
		}
		// parameters
		ParameterSet parameters = configuration.getQueryParameters();
		if (parameters != null) {
			parameters.forEachParameter((n, v) -> q.parameter(n, v));
		}
		return q;
	}

}
