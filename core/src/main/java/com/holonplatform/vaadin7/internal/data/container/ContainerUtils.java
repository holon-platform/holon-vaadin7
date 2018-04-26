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
package com.holonplatform.vaadin7.internal.data.container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.query.PathExpression;
import com.holonplatform.core.query.QueryExpression;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin7.data.ItemDataSource.Configuration;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;

/**
 * Container-related utility class
 * 
 * @since 3.2.0
 */
public final class ContainerUtils implements Serializable {

	private static final long serialVersionUID = 2795000649734188846L;

	/*
	 * Empty private constructor: this class is intended only to provide constants ad utility methods.
	 */
	private ContainerUtils() {
	}

	/**
	 * Convert a {@link Container} filters collection into a {@link QueryFilter}.
	 * @param configuration Container configuration
	 * @param filters Filters to convert
	 * @return QueryFilter
	 * @throws InvalidExpressionException Failed to convert a filter into a QueryFilter
	 */
	public static Optional<QueryFilter> convertContainerFilters(Configuration<?> configuration,
			Collection<Filter> filters) throws InvalidExpressionException {
		if (filters != null && !filters.isEmpty()) {
			List<QueryFilter> qfs = new ArrayList<>(filters.size());
			for (Filter filter : filters) {
				QueryFilter qf = convertContainerFilter(configuration, filter).orElseThrow(
						() -> new InvalidExpressionException("Failed to convert Container Filter: unknown filter type: "
								+ filter.getClass().getName()));
				qfs.add(qf);
			}
			return QueryFilter.allOf(qfs);
		}
		return Optional.empty();
	}

	/**
	 * Convert a {@link Container} filter into a {@link QueryFilter}.
	 * @param configuration Container configuration
	 * @param filter Filter to convert
	 * @return QueryFilter
	 * @throws InvalidExpressionException Failed to convert given filter into a QueryFilter
	 */
	public static Optional<QueryFilter> convertContainerFilter(Configuration<?> configuration, Filter filter)
			throws InvalidExpressionException {
		if (filter != null) {
			if (filter instanceof IsNull) {
				Object propertyId = ((IsNull) filter).getPropertyId();
				return Optional.of(QueryFilter.isNull(getQueryExpression(propertyId, configuration)));
			} else if (filter instanceof SimpleStringFilter) {
				SimpleStringFilter sf = (SimpleStringFilter) filter;
				Object propertyId = sf.getPropertyId();
				String value = sf.getFilterString();
				if (sf.isOnlyMatchPrefix()) {
					return Optional.of(QueryFilter.startsWith(getQueryExpression(propertyId, configuration), value,
							sf.isIgnoreCase()));
				} else {
					return Optional.of(QueryFilter.contains(getQueryExpression(propertyId, configuration), value,
							sf.isIgnoreCase()));
				}
			} else if (filter instanceof Equal) {
				return Optional.of(QueryFilter.eq(getQueryExpression(((Equal) filter).getPropertyId(), configuration),
						((Equal) filter).getValue()));
			} else if (filter instanceof Greater) {
				return Optional.of(QueryFilter.gt(getQueryExpression(((Greater) filter).getPropertyId(), configuration),
						((Greater) filter).getValue()));
			} else if (filter instanceof GreaterOrEqual) {
				return Optional.of(
						QueryFilter.goe(getQueryExpression(((GreaterOrEqual) filter).getPropertyId(), configuration),
								((GreaterOrEqual) filter).getValue()));
			} else if (filter instanceof Less) {
				return Optional.of(QueryFilter.lt(getQueryExpression(((Less) filter).getPropertyId(), configuration),
						((Less) filter).getValue()));
			} else if (filter instanceof LessOrEqual) {
				return Optional
						.of(QueryFilter.loe(getQueryExpression(((LessOrEqual) filter).getPropertyId(), configuration),
								((LessOrEqual) filter).getValue()));
			} else if (filter instanceof Like) {
				String value = ((Like) filter).getValue();
				if (value == null)
					value = "";
				if (value.startsWith("%") && value.endsWith("%")) {
					return Optional
							.of(QueryFilter.contains(getQueryExpression(((Like) filter).getPropertyId(), configuration),
									value.substring(1, value.length() - 1), !((Like) filter).isCaseSensitive()));
				} else if (value.startsWith("%")) {
					return Optional.of(
							QueryFilter.startsWith(getQueryExpression(((Like) filter).getPropertyId(), configuration),
									value.substring(1), !((Like) filter).isCaseSensitive()));
				} else if (value.endsWith("%")) {
					return Optional
							.of(QueryFilter.endsWith(getQueryExpression(((Like) filter).getPropertyId(), configuration),
									value.substring(0, value.length() - 1), !((Like) filter).isCaseSensitive()));
				}
			} else if (filter instanceof Between) {
				Object from = ((Between) filter).getStartValue();
				Object to = ((Between) filter).getEndValue();
				return Optional.of(QueryFilter
						.between(getQueryExpression(((Between) filter).getPropertyId(), configuration), from, to));
			} else if (filter instanceof Not) {
				return convertContainerFilter(configuration, ((Not) filter).getFilter()).map(f -> QueryFilter.not(f));
			} else if (filter instanceof And) {
				final List<QueryFilter> qfs = new ArrayList<>();
				((And) filter).getFilters().forEach(c -> {
					convertContainerFilter(configuration, c).ifPresent(f -> qfs.add(f));
				});
				return QueryFilter.allOf(qfs);
			} else if (filter instanceof Or) {
				final List<QueryFilter> qfs = new ArrayList<>();
				((Or) filter).getFilters().forEach(c -> {
					convertContainerFilter(configuration, c).ifPresent(f -> qfs.add(f));
				});
				return QueryFilter.anyOf(qfs);
			}
		}
		return Optional.empty();
	}

	/**
	 * Validate given <code>propertyId</code> checking if suitable to participate in a filter expression.
	 * @param <T> Expression type
	 * @param propertyId Property id
	 * @param configuration Container configuration
	 * @return Query expression
	 * @throws InvalidExpressionException If property id is not a {@link QueryExpression} or no QueryExpression can be
	 *         retrieved by name using container properties
	 */
	@SuppressWarnings("unchecked")
	public static <T> QueryExpression<T> getQueryExpression(Object propertyId, Configuration<?> configuration)
			throws InvalidExpressionException {
		if (propertyId != null) {
			if (propertyId instanceof QueryExpression) {
				return (QueryExpression<T>) propertyId;
			} else {
				String propertyName = (propertyId instanceof String) ? (String) propertyId : propertyId.toString();
				PathExpression<T> property = getPathByName(propertyName, configuration);
				if (property != null) {
					return property;
				}
				throw new InvalidExpressionException("Invalid query clause property: " + propertyId);
			}
		}
		return null;
	}

	/**
	 * Try to get a {@link PathExpression} using path name from container properties.
	 * @param <T> Expression type
	 * @param propertyName Property name
	 * @param configuration Container configuration
	 * @return {@link PathExpression} with given name, or <code>null</code> if not found
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> PathExpression<T> getPathByName(String propertyName, Configuration configuration) {
		if (propertyName != null && configuration != null) {
			for (Object property : configuration.getProperties()) {
				String name = (property instanceof Path) ? ((Path<?>) property).getName() : property.toString();
				if (propertyName.equals(name)) {
					if (property instanceof PathExpression) {
						return (PathExpression<T>) property;
					} else {
						return PathProperty.create(name, configuration.getPropertyType(property));
					}
				}
			}
		}
		return null;
	}

}
