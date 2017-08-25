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
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.data.ItemDataProvider;

/**
 * An {@link ItemDataProvider} using a {@link Datastore} to perform item set count and load operations, using
 * {@link Property} as item property type and {@link PropertyBox} as concrete item data container.
 * 
 * @since 5.0.0
 */
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
	 * Property set provider
	 */
	private final PropertySet<?> propertySet;

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
		ObjectUtils.argumentNotNull(propertySet, "PropertySet supplier must be not null");
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
	 * @see com.holonplatform.vaadin.data.ItemSetCounter#size(com.holonplatform.core.query.QueryConfigurationProvider)
	 */
	@Override
	public long size(QueryConfigurationProvider configuration) throws DataAccessException {
		try {
			return buildQuery(configuration, false).count();
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetLoader#load(com.holonplatform.core.query.QueryConfigurationProvider,
	 * int, int)
	 */
	@Override
	public Stream<PropertyBox> load(QueryConfigurationProvider configuration, int offset, int limit)
			throws DataAccessException {
		Query q = buildQuery(configuration, true);
		// paging
		if (limit > 0) {
			q.limit(limit);
			q.offset(offset);
		}
		// execute
		return q.stream(propertySet);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataProvider#refresh(java.lang.Object)
	 */
	@Override
	public PropertyBox refresh(PropertyBox item) throws UnsupportedOperationException, DataAccessException {
		return getDatastore().refresh(getTarget(), item);
	}

	/**
	 * Build a {@link Query} using the Datastore and configuring query filters and sorts.
	 * @param configuration Query configuration
	 * @param withSorts Whether to apply sorts, if any, to query
	 * @return Query instance
	 */
	protected Query buildQuery(QueryConfigurationProvider configuration, boolean withSorts) {
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
