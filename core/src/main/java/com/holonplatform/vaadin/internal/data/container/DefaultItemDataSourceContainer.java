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
package com.holonplatform.vaadin.internal.data.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.Path;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.DefaultParameterSet;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.data.container.ItemAdapter;
import com.holonplatform.vaadin.data.container.ItemDataSourceContainer;
import com.holonplatform.vaadin.internal.data.DefaultItemSort;
import com.holonplatform.vaadin.internal.data.DefaultItemStore;
import com.holonplatform.vaadin.internal.data.ItemStore;
import com.holonplatform.vaadin.internal.data.ItemStore.ItemActionListener;
import com.vaadin.data.Container;
import com.vaadin.data.ContainerHelpers;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.filter.UnsupportedFilterException;

/**
 * Default {@link ItemDataSourceContainer} implementation.
 * 
 * @param <ITEM> Item type
 * @param <PROPERTY> Item property type
 * 
 * @since 5.0.0
 */
public class DefaultItemDataSourceContainer<ITEM, PROPERTY>
		implements ItemDataSourceContainer<ITEM, PROPERTY>, Configuration<PROPERTY>, ItemActionListener<Item> {

	private static final long serialVersionUID = 5690427592609021861L;

	/**
	 * Batch size (&lt;=0 means no paging)
	 */
	private int batchSize = DEFAULT_BATCH_SIZE;
	/**
	 * Max results (&lt;=0 means no limit)
	 */
	private int maxSize = 0;

	/**
	 * Auto refresh
	 */
	private boolean autoRefresh = true;

	/**
	 * Property ids
	 */
	private List<PROPERTY> propertyIds = new LinkedList<>();

	/**
	 * Property types
	 */
	private final Map<PROPERTY, Class<?>> propertyTypes = new HashMap<>();
	/**
	 * Property default values
	 */
	private Map<PROPERTY, Object> defaultValues;

	/**
	 * Read-only properties
	 */
	private Collection<PROPERTY> readOnlyPropertyIds = new LinkedList<>();
	/**
	 * Sortable properties
	 */
	private final Collection<PROPERTY> sortablePropertyIds = new LinkedList<>();

	/**
	 * Item sorts
	 */
	private final List<ItemSort<PROPERTY>> itemSorts = new LinkedList<>();

	/**
	 * Item store
	 */
	private ItemStore<Item> itemStore;

	/**
	 * Item data provider
	 */
	private ItemDataProvider<ITEM> dataProvider;

	/**
	 * Item adapter
	 */
	private ItemAdapter<ITEM> itemAdapter;

	/**
	 * Item identifier provider
	 */
	private ItemIdentifierProvider<ITEM, ?> itemIdentifierProvider;

	/**
	 * Item commit handler
	 */
	private CommitHandler<ITEM> commitHandler;

	/**
	 * List of registered ItemSetChangeListener
	 */
	private List<ItemSetChangeListener> itemSetChangeListeners;
	/**
	 * List of registered PropertySetChangeListeners
	 */
	private List<PropertySetChangeListener> propertySetChangeListeners;

	/**
	 * Container filters
	 */
	private List<Container.Filter> containerFilters;

	/**
	 * Buffered by default
	 */
	private boolean buffered = true;

	/**
	 * Additional QueryConfigurationProviders
	 */
	private List<QueryConfigurationProvider> queryConfigurationProviders;

	/**
	 * Fixed query filter: if not null, it will always be added to query filters
	 */
	private QueryFilter fixedFilter;

	/**
	 * Fixed query sort: if not null, it will always be added to query sorts
	 */
	private QuerySort fixedSort;

	/**
	 * Default query sort: if no other sort is provided, this sort will be used
	 */
	private QuerySort defaultSort;

	/**
	 * {@link PropertySortGenerator}s bound to properties
	 */
	private Map<PROPERTY, PropertySortGenerator<PROPERTY>> propertySortGenerators;

	/**
	 * Query parameters
	 */
	private final DefaultParameterSet queryParameters = new DefaultParameterSet();

	/**
	 * ValueChangeListeners to track item property modifications
	 */
	private final Map<Item, ItemPropertyValueChangeListener> itemPropertyValueChangeListeners = new WeakHashMap<>(8);

	/**
	 * Constructor.
	 * @param dataProvider {@link ItemDataProvider} to be used as items data source
	 * @param itemIdentifierProvider Item identifier provider
	 * @param itemAdapter Item adapter
	 */
	public DefaultItemDataSourceContainer(ItemDataProvider<ITEM> dataProvider,
			ItemIdentifierProvider<ITEM, Object> itemIdentifierProvider, ItemAdapter<ITEM> itemAdapter) {
		this();
		this.dataProvider = dataProvider;
		this.itemIdentifierProvider = itemIdentifierProvider;
		this.itemAdapter = itemAdapter;
		init();
	}

	/**
	 * Constructor which do not perform internal initialization. The container initialization must be performed later
	 * using the {@link #init()} method.
	 */
	protected DefaultItemDataSourceContainer() {
		super();
		// include container filters
		addQueryConfigurationProvider(new QueryConfigurationProvider() {

			@Override
			public QueryFilter getQueryFilter() {
				// Container filters
				return ContainerUtils.convertContainerFilters(getConfiguration(), getContainerFilters()).orElse(null);
			}
		});
	}

	/**
	 * Init the data source container, configuring the internal {@link ItemStore}.
	 */
	protected void init() {
		this.itemStore = new DefaultItemStore<>(this, new ContainerItemDataProvider<>(
				getDataProvider().orElseThrow(
						() -> new IllegalStateException("Invalid data source configuration: missing ItemDataProvider")),
				getItemAdapter().orElseThrow(
						() -> new IllegalStateException("Invalid data source configuration: missing ItemAdapter"))),
				getItemIdentifierProvider().map(i -> new ContainerItemIdentifierProvider<>(i,
						getItemAdapter().orElseThrow(() -> new IllegalStateException(
								"Invalid data source configuration: missing ItemAdapter")),
						this)).orElse(null),
				determineMaxCacheSize(getBatchSize()));
		this.itemStore.setFreezed(!isAutoRefresh());
		this.itemStore.addItemActionListener(this);
	}

	/**
	 * Get the items store.
	 * @return the items store, empty if not setted
	 */
	protected Optional<ItemStore<Item>> getItemStore() {
		return Optional.ofNullable(itemStore);
	}

	/**
	 * Get the items store.
	 * @return the items store
	 * @throws IllegalStateException If the items store is not available
	 */
	protected ItemStore<Item> requireItemStore() {
		return getItemStore()
				.orElseThrow(() -> new IllegalStateException("Missing ItemStore: check container configuration"));
	}

	/**
	 * Get the item adapter.
	 * @return the item adapter, empty if not setted
	 */
	protected Optional<ItemAdapter<ITEM>> getItemAdapter() {
		return Optional.ofNullable(itemAdapter);
	}

	/**
	 * Get the item adapter.
	 * @return the item adapter
	 * @throws IllegalStateException If the item adapter is not available
	 */
	protected ItemAdapter<ITEM> requireItemAdapter() {
		return getItemAdapter()
				.orElseThrow(() -> new IllegalStateException("Missing ItemAdapter: check container configuration"));
	}

	/**
	 * Set the item adapter
	 * @param itemAdapter the item adapter to set
	 */
	public void setItemAdapter(ItemAdapter<ITEM> itemAdapter) {
		this.itemAdapter = itemAdapter;
	}

	/**
	 * Get the {@link ItemIdentifierProvider}.
	 * @return the item identifier provider
	 */
	protected Optional<ItemIdentifierProvider<ITEM, ?>> getItemIdentifierProvider() {
		return Optional.ofNullable(itemIdentifierProvider);
	}

	/**
	 * Set the {@link ItemIdentifierProvider}.
	 * @param <ID> Item id type
	 * @param itemIdentifierProvider the item identifier provider to set
	 */
	public <ID> void setItemIdentifierProvider(ItemIdentifierProvider<ITEM, ID> itemIdentifierProvider) {
		this.itemIdentifierProvider = itemIdentifierProvider;
	}

	/**
	 * Get the item data provider.
	 * @return the item data provider
	 */
	public Optional<ItemDataProvider<ITEM>> getDataProvider() {
		return Optional.ofNullable(dataProvider);
	}

	/**
	 * Set the item data provider.
	 * @param dataProvider the item data provider to set
	 */
	public void setDataProvider(ItemDataProvider<ITEM> dataProvider) {
		this.dataProvider = dataProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#getConfiguration()
	 */
	@Override
	public Configuration<PROPERTY> getConfiguration() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryDefinition#getBatchSize()
	 */
	@Override
	public int getBatchSize() {
		return batchSize;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#setBatchSize( int)
	 */
	@Override
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
		// setup cache size
		setMaxCacheSize(determineMaxCacheSize(batchSize));
	}

	/**
	 * Calculate max item store cache size using batch size, if positive or default
	 * 
	 * @param batchSize Batch size
	 * @return Item store max cache size
	 */
	protected int determineMaxCacheSize(int batchSize) {
		if (batchSize > 0) {
			return batchSize * 10;
		} else {
			return ItemStore.DEFAULT_MAX_CACHE_SIZE;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryDefinition#getMaxSize()
	 */
	@Override
	public int getMaxSize() {
		return maxSize;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#setMaxSize(int)
	 */
	@Override
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.querycontainer.ItemQueryContainer# setMaxCacheSize(int)
	 */
	@Override
	public void setMaxCacheSize(int maxCacheSize) {
		getItemStore().ifPresent(s -> {
			s.setMaxCacheSize(maxCacheSize);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#isAutoRefresh()
	 */
	@Override
	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#setAutoRefresh( boolean)
	 */
	@Override
	public void setAutoRefresh(boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
		// if auto refresh not enabled, freeze the item store
		getItemStore().ifPresent(i -> i.setFreezed(!autoRefresh));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getProperties()
	 */
	@Override
	public Iterable<PROPERTY> getProperties() {
		return propertyIds;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryDefinition# isPropertyReadOnly(java.lang.Object)
	 */
	@Override
	public boolean isPropertyReadOnly(Object propertyId) {
		return (propertyId != null && readOnlyPropertyIds.contains(propertyId));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryDefinition# isPropertySortable(java.lang.Object)
	 */
	@Override
	public boolean isPropertySortable(Object propertyId) {
		return (propertyId != null && sortablePropertyIds.contains(propertyId));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.querycontainer.ItemQueryDefinition# getPropertyDefaultValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyDefaultValue(Object propertyId) {
		if (defaultValues != null && propertyId != null) {
			return defaultValues.get(propertyId);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.querycontainer.ItemQueryContainer# addContainerProperty(java.lang.Object,
	 * java.lang.Class, boolean, boolean, java.lang.Object)
	 */
	@Override
	public <T> boolean addContainerProperty(PROPERTY propertyId, Class<T> type, boolean readOnly, boolean sortable,
			T defaultValue) {
		if (propertyId != null) {
			// remove any previous property with same id
			if (propertyIds.contains(propertyId)) {
				propertyIds.remove(propertyId);
			}
			propertyIds.add(propertyId);
			Class<?> pt = (type != null) ? type : Object.class;
			propertyTypes.put(propertyId, pt);
			if (readOnly) {
				readOnlyPropertyIds.add(propertyId);
			}
			if (sortable) {
				sortablePropertyIds.add(propertyId);
			}
			if (defaultValue != null) {
				if (defaultValues == null) {
					defaultValues = new HashMap<>();
				}
				defaultValues.put(propertyId, defaultValue);
			}
			// event
			notifyPropertySetChanged();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer# addContainerProperty(java.lang.Object,
	 * java.lang.Class, boolean, boolean)
	 */
	@Override
	public boolean addContainerProperty(PROPERTY propertyId, Class<?> type, boolean readOnly, boolean sortable) {
		return addContainerProperty(propertyId, type, readOnly, sortable, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer# setPropertySortable(java.lang.Object, boolean)
	 */
	@Override
	public void setPropertySortable(PROPERTY propertyId, boolean sortable) {
		if (propertyId != null) {
			if (sortable) {
				if (!sortablePropertyIds.contains(propertyId)) {
					sortablePropertyIds.add(propertyId);
				}
			} else {
				sortablePropertyIds.remove(propertyId);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer# setPropertyReadOnly(java.lang.Object, boolean)
	 */
	@Override
	public void setPropertyReadOnly(PROPERTY propertyId, boolean readOnly) {
		if (propertyId != null) {
			if (readOnly) {
				if (!readOnlyPropertyIds.contains(propertyId)) {
					readOnlyPropertyIds.add(propertyId);
				}
			} else {
				readOnlyPropertyIds.remove(propertyId);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.querycontainer.ItemQueryContainer# setPropertyDefaultValue(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void setPropertyDefaultValue(PROPERTY propertyId, Object defaultValue) {
		if (propertyId != null) {
			if (defaultValues == null) {
				defaultValues = new HashMap<>();
			}
			defaultValues.put(propertyId, defaultValue);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#refresh()
	 */
	@Override
	public void refresh() throws DataAccessException {
		requireItemStore().reset(true, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#clear()
	 */
	@Override
	public void clear() {
		requireItemStore().reset(true, !isAutoRefresh());
	}

	/*
	 * Container
	 */

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#getContainerPropertyIds()
	 */
	@Override
	public Collection<?> getContainerPropertyIds() {
		return ConversionUtils.iterableAsList(getProperties());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#getItemIds()
	 */
	@Override
	public Collection<?> getItemIds() {
		return requireItemStore().getItemIds();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		final Item item = getItem(itemId);
		if (item != null) {
			return item.getItemProperty(propertyId);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#addContainerProperty(java.lang.Object, java.lang.Class, java.lang.Object)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean addContainerProperty(Object propertyId, Class type, Object defaultValue)
			throws UnsupportedOperationException {
		try {
			return addContainerProperty((PROPERTY) propertyId, type, false, false, defaultValue);
		} catch (Exception e) {
			throw new UnsupportedFilterException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object)
	 */
	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		if (propertyId != null && propertyIds.contains(propertyId)) {
			propertyIds.remove(propertyId);
			if (readOnlyPropertyIds.contains(propertyId)) {
				readOnlyPropertyIds.remove(propertyId);
			}
			if (sortablePropertyIds.contains(propertyId)) {
				sortablePropertyIds.remove(propertyId);
			}
			if (defaultValues != null && defaultValues.containsKey(propertyId)) {
				defaultValues.remove(propertyId);
			}
			// event
			notifyPropertySetChanged();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#getType(java.lang.Object)
	 */
	@Override
	public Class<?> getType(Object propertyId) {
		if (propertyId != null) {
			return propertyTypes.get(propertyId);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getPropertyType(java.lang.Object)
	 */
	@Override
	public Class<?> getPropertyType(PROPERTY property) {
		return getType(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#size()
	 */
	@Override
	public int size() {
		return requireItemStore().size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#getItem(java.lang.Object)
	 */
	@Override
	public Item getItem(Object itemId) {
		if (itemId != null) {
			int index = requireItemStore().indexOfItem(itemId);
			if (index > -1) {
				return requireItemStore().getItem(index);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#containsId(java.lang.Object)
	 */
	@Override
	public boolean containsId(Object itemId) {
		return requireItemStore().containsItem(itemId);
	}

	/**
	 * Get the commit handler
	 * @return the commit handler
	 */
	public Optional<CommitHandler<ITEM>> getCommitHandler() {
		return Optional.ofNullable(commitHandler);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemQueryContainer#setCommitHandler(com.holonplatform.vaadin.data.
	 * ItemDataSource.CommitHandler)
	 */
	@Override
	public void setCommitHandler(CommitHandler<ITEM> commitHandler) {
		this.commitHandler = commitHandler;
	}

	@Override
	public void addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		if (queryConfigurationProvider != null) {
			if (queryConfigurationProviders == null) {
				queryConfigurationProviders = new LinkedList<>();
			}
			if (!queryConfigurationProviders.contains(queryConfigurationProvider)) {
				queryConfigurationProviders.add(queryConfigurationProvider);
				// reset store
				resetStorePreservingFreezeState();
			}
		}
	}

	@Override
	public void removeQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		if (queryConfigurationProvider != null && queryConfigurationProviders != null) {
			queryConfigurationProviders.remove(queryConfigurationProvider);
			// reset store
			resetStorePreservingFreezeState();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#setFixedQueryFilter(com.holonplatform.core.query.
	 * QueryFilter)
	 */
	@Override
	public void setFixedFilter(QueryFilter filter) {
		this.fixedFilter = filter;
		// reset store
		resetStorePreservingFreezeState();
	}

	@Override
	public void setFixedSort(QuerySort sort) {
		this.fixedSort = sort;
		// reset store
		resetStorePreservingFreezeState();
	}

	@Override
	public void setDefaultSort(QuerySort sort) {
		this.defaultSort = sort;
		// reset store
		resetStorePreservingFreezeState();
	}

	@Override
	public void addQueryParameter(String name, Object value) {
		queryParameters.addParameter(name, value);
		// reset store
		resetStorePreservingFreezeState();
	}

	@Override
	public void removeQueryParameter(String name) {
		queryParameters.removeParameter(name);
		// reset store
		resetStorePreservingFreezeState();
	}

	@Override
	public void setPropertySortGenerator(PROPERTY property, PropertySortGenerator<PROPERTY> propertySortGenerator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(propertySortGenerator, "PropertySortGenerator must be not null");
		if (propertySortGenerators == null) {
			propertySortGenerators = new HashMap<>(4);
		}
		propertySortGenerators.put(property, propertySortGenerator);
	}

	/**
	 * Get the {@link PropertySortGenerator} bound to given property, if any
	 * @param property Property
	 * @return PropertySortGenerator, or <code>null</code> if not available
	 */
	protected PropertySortGenerator<PROPERTY> getPropertySortGenerator(PROPERTY property) {
		if (propertySortGenerators != null) {
			return propertySortGenerators.get(property);
		}
		return null;
	}

	/**
	 * Additional QueryConfigurationProvider
	 * @return QueryConfigurationProviders, or <code>null</code> if none
	 */
	public List<QueryConfigurationProvider> getQueryConfigurationProviders() {
		return queryConfigurationProviders;
	}

	/**
	 * Fixed query filter
	 * @return Fixed filter, or <code>null</code> if not setted
	 */
	public QueryFilter getFixedFilter() {
		return fixedFilter;
	}

	/**
	 * Fixed query sort
	 * @return Fixed sort, or <code>null</code> if not setted
	 */
	public QuerySort getFixedSort() {
		return fixedSort;
	}

	/**
	 * Default query sort
	 * @return Default sort, or <code>null</code> if not setted
	 */
	public QuerySort getDefaultSort() {
		return defaultSort;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#sort(com.holonplatform.vaadin.data.ItemDataSource.ItemSort[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void sort(ItemSort<PROPERTY>... sorts) {
		List<ItemSort<PROPERTY>> itemSorts = (sorts == null) ? null : Arrays.asList(sorts);
		setItemSorts(itemSorts);
		// refresh
		refresh();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getItemSorts()
	 */
	@Override
	public List<ItemSort<PROPERTY>> getItemSorts() {
		return itemSorts;
	}

	/**
	 * Set the item sort directives.
	 * @param sorts Item sorts
	 */
	public void setItemSorts(List<ItemSort<PROPERTY>> sorts) {
		itemSorts.clear();
		if (sorts != null) {
			sorts.forEach(s -> itemSorts.add(s));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQueryFilter()
	 */
	@Override
	public QueryFilter getQueryFilter() {
		final LinkedList<QueryFilter> filters = new LinkedList<>();

		// fixed
		QueryFilter fixed = getFixedFilter();
		if (fixed != null) {
			filters.add(fixed);
		}

		// externally provided
		if (getQueryConfigurationProviders() != null) {
			for (QueryConfigurationProvider provider : getQueryConfigurationProviders()) {
				QueryFilter filter = provider.getQueryFilter();
				if (filter != null) {
					filters.add(filter);
				}
			}
		}

		// return overall filter
		return QueryFilter.allOf(filters).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQuerySort()
	 */
	@Override
	public QuerySort getQuerySort() {
		LinkedList<QuerySort> sorts = new LinkedList<>();

		// sorts
		List<ItemSort<PROPERTY>> itemSorts = getItemSorts();
		if (!itemSorts.isEmpty()) {

			// item sorts
			for (ItemSort<PROPERTY> itemSort : itemSorts) {
				// sort property
				PROPERTY sortId = itemSort.getProperty();
				// check delegate
				PropertySortGenerator<PROPERTY> generator = getPropertySortGenerator(sortId);
				if (generator != null) {
					QuerySort sort = generator.getQuerySort(sortId, itemSort.isAscending());
					if (sort != null) {
						sorts.add(sort);
					}
				} else {
					getPropertyPath(sortId, getProperties()).ifPresent(p -> {
						sorts.add(QuerySort.of(p,
								itemSort.isAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING));
					});
				}
			}

		} else {

			// externally provided
			if (getQueryConfigurationProviders() != null) {
				for (QueryConfigurationProvider provider : getQueryConfigurationProviders()) {
					QuerySort sort = provider.getQuerySort();
					if (sort != null) {
						sorts.add(sort);
					}
				}
			}

		}

		// default sort
		if (sorts.isEmpty()) {
			QuerySort dft = getDefaultSort();
			if (dft != null) {
				sorts.add(dft);
			}
		}

		// fixed
		QuerySort fixed = getFixedSort();
		if (fixed != null) {
			sorts.add(fixed);
		}

		if (!sorts.isEmpty()) {
			if (sorts.size() == 1) {
				return sorts.getFirst();
			}
			return QuerySort.of(sorts);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQueryParameters()
	 */
	@Override
	public ParameterSet getQueryParameters() {
		return queryParameters;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#getId(java.lang.Object)
	 */
	@Override
	public Object getId(ITEM item) {
		if (item != null) {
			return requireItemStore().getItemId(requireItemAdapter().adapt(this, item));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#get(java.lang.Object)
	 */
	@Override
	public Optional<ITEM> get(Object itemId) {
		ObjectUtils.argumentNotNull(itemId, "Item id must be not null");
		int index = requireItemStore().indexOfItem(itemId);
		if (index > -1) {
			Item item = requireItemStore().getItem(index);
			if (item != null) {
				return Optional.ofNullable(requireItemAdapter().restore(this, item));
			}
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#add(java.lang.Object)
	 */
	@Override
	public Object add(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to add must be not null");
		return requireItemStore().addItem(requireItemAdapter().adapt(this, item));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#update(java.lang.Object)
	 */
	@Override
	public void update(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to update must be not null");
		requireItemStore().setItemModified(requireItemAdapter().adapt(this, item));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to remove must be not null");
		return requireItemStore().removeItem(requireItemAdapter().adapt(this, item));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#refresh(java.lang.Object)
	 */
	@Override
	public void refresh(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to refresh must be not null");
		requireItemStore().refreshItem(requireItemAdapter().adapt(this, item));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainer#refreshItem(java.lang.Object)
	 */
	@Override
	public void refreshItem(Object itemId) {
		ObjectUtils.argumentNotNull(itemId, "Item id to refresh must be not null");
		requireItemStore().refreshItem(getItem(itemId));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#addItem(java.lang.Object)
	 */
	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#addItem()
	 */
	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#removeItem(java.lang.Object)
	 */
	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		if (itemId != null) {
			int idx = requireItemStore().indexOfItem(itemId);
			if (idx > -1) {
				requireItemStore().removeItem(idx);
				notifyItemSetChanged(new ItemRemovedEvent(this, itemId, idx));
				return true;
			}
		}
		return false;
	}

	/*
	 * Container.Indexed
	 */

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Indexed#indexOfId(java.lang.Object)
	 */
	@Override
	public int indexOfId(Object itemId) {
		if (itemId != null) {
			return requireItemStore().indexOfItem(itemId);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Indexed#getIdByIndex(int)
	 */
	@Override
	public Object getIdByIndex(int index) {
		return requireItemStore().getItemIds().get(index);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Indexed#getItemIds(int, int)
	 */
	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		return ContainerHelpers.getItemIdsUsingGetIdByIndex(startIndex, numberOfItems, this);
	}

	/*
	 * Container.Ordered
	 */

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
	 */
	@Override
	public Object nextItemId(Object itemId) {
		List<?> ids = requireItemStore().getItemIds();
		int currentIndex = ids.indexOf(itemId);
		if (currentIndex < 0 || currentIndex == (ids.size() - 1)) {
			return null;
		}
		return ids.get(currentIndex + 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Ordered#prevItemId(java.lang.Object)
	 */
	@Override
	public Object prevItemId(Object itemId) {
		List<?> ids = requireItemStore().getItemIds();
		int currentIndex = ids.indexOf(itemId);
		if (currentIndex <= 0) {
			return null;
		}
		return ids.get(currentIndex - 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Ordered#firstItemId()
	 */
	@Override
	public Object firstItemId() {
		List<?> ids = requireItemStore().getItemIds();
		if (!ids.isEmpty()) {
			return ids.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Ordered#lastItemId()
	 */
	@Override
	public Object lastItemId() {
		List<?> ids = requireItemStore().getItemIds();
		if (!ids.isEmpty()) {
			return ids.get(ids.size() - 1);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Ordered#isFirstId(java.lang.Object)
	 */
	@Override
	public boolean isFirstId(Object itemId) {
		return requireItemStore().indexOfItem(itemId) == 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
	 */
	@Override
	public boolean isLastId(Object itemId) {
		return requireItemStore().indexOfItem(itemId) == (size() - 1);
	}

	/*
	 * Container.Buffered
	 */

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Buffered#commit()
	 */
	@Override
	public void commit() throws SourceException, InvalidValueException {
		final CommitHandler<ITEM> handler = getCommitHandler()
				.orElseThrow(() -> new IllegalStateException("Missing CommitHandler"));
		List<ITEM> added = requireItemStore().getAddedItems().stream().map(i -> requireItemAdapter().restore(this, i))
				.collect(Collectors.toList());
		List<ITEM> modified = requireItemStore().getModifiedItems().stream()
				.map(i -> requireItemAdapter().restore(this, i)).collect(Collectors.toList());
		List<ITEM> removed = requireItemStore().getRemovedItems().stream()
				.map(i -> requireItemAdapter().restore(this, i)).collect(Collectors.toList());
		if (!added.isEmpty() || !modified.isEmpty() || !removed.isEmpty()) {
			final List<ITEM> addedItemReversed = new ArrayList<>(added);
			Collections.reverse(addedItemReversed);
			handler.commit(addedItemReversed, modified, removed);
			// reset items store
			requireItemStore().reset(false, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Buffered#discard()
	 */
	@Override
	public void discard() throws SourceException {
		requireItemStore().discard();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Buffered#setBuffered(boolean)
	 */
	@Override
	public void setBuffered(boolean buffered) {
		this.buffered = buffered;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Buffered#isBuffered()
	 */
	@Override
	public boolean isBuffered() {
		return buffered;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Buffered#isModified()
	 */
	@Override
	public boolean isModified() {
		return requireItemStore().isModified();
	}

	@Override
	public void onItemAction(Item item, Object itemId, Item previous, ItemAction action) {

		// setup property value change listeners
		if (action == ItemAction.LOADED) {
			if (previous != null) {
				ItemPropertyValueChangeListener listener = itemPropertyValueChangeListeners.get(item);
				if (listener != null) {
					listener.detach();
				}
			}
			itemPropertyValueChangeListeners.put(item, new ItemPropertyValueChangeListener(item, requireItemStore()));
		} else if (action == ItemAction.SET_CHANGED) {
			for (ItemPropertyValueChangeListener listener : itemPropertyValueChangeListeners.values()) {
				listener.detach();
			}
			itemPropertyValueChangeListeners.clear();
		} else if (action == ItemAction.REFRESHED) {

			if (previous != null) {
				ItemPropertyValueChangeListener listener = itemPropertyValueChangeListeners.get(previous);
				if (listener != null) {
					listener.detach();
					itemPropertyValueChangeListeners.remove(previous);
				}
				itemPropertyValueChangeListeners.put(item,
						new ItemPropertyValueChangeListener(item, requireItemStore()));
			}
		} else if (action == ItemAction.ADDED) {
			// noop
		} else if (action == ItemAction.REMOVED) {
			ItemPropertyValueChangeListener listener = itemPropertyValueChangeListeners.get(item);
			if (listener != null) {
				listener.detach();
			}
		}

		// notify item set changed
		if (action == ItemAction.ADDED || action == ItemAction.REMOVED || action == ItemAction.REFRESHED
				|| action == ItemAction.SET_CHANGED) {
			ItemSetChangeEvent event = null;
			if (action == ItemAction.ADDED) {
				event = new ItemAddedEvent(this, itemId);
			} else if (action == ItemAction.REMOVED) {
				event = new ItemRemovedEvent(this, itemId, indexOfId(itemId));
			}
			notifyItemSetChanged(event);
		}

		// if not buffered, invoke commit
		if (!isBuffered()
				&& (action == ItemAction.ADDED || action == ItemAction.REMOVED || action == ItemAction.MODIFIED)) {
			commit();
		}

	}

	/*
	 * Container.Filterable
	 */

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Filterable#addContainerFilter(com.vaadin.data. Container.Filter)
	 */
	@Override
	public void addContainerFilter(Filter filter) throws UnsupportedFilterException {
		if (filter != null) {
			if (containerFilters == null) {
				containerFilters = new LinkedList<>();
			}
			containerFilters.add(filter);
			// reset store
			resetStorePreservingFreezeState();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Filterable#removeContainerFilter(com.vaadin.data. Container.Filter)
	 */
	@Override
	public void removeContainerFilter(Filter filter) {
		if (filter != null && containerFilters != null) {
			containerFilters.remove(filter);
			// reset store
			resetStorePreservingFreezeState();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Filterable#removeAllContainerFilters()
	 */
	@Override
	public void removeAllContainerFilters() {
		containerFilters = null;
		// reset store
		resetStorePreservingFreezeState();
	}

	/**
	 * Reset item store content preserving the <em>freezed</em> state
	 */
	protected void resetStorePreservingFreezeState() {
		getItemStore().ifPresent(i -> {
			boolean freezed = i.isFreezed();
			try {
				i.reset(false, false);
			} finally {
				i.setFreezed(freezed);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Filterable#getContainerFilters()
	 */
	@Override
	public Collection<Filter> getContainerFilters() {
		if (containerFilters != null) {
			return containerFilters;
		}
		return Collections.emptyList();
	}

	/*
	 * Container.Sortable
	 */

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[], boolean[])
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void sort(Object[] propertyId, boolean[] ascending) {
		ItemSort<PROPERTY>[] itemSorts = null;
		if (propertyId != null && propertyId.length > 0) {
			itemSorts = new ItemSort[propertyId.length];
			for (int i = 0; i < propertyId.length; i++) {
				itemSorts[i] = new DefaultItemSort(propertyId[i], ascending[i]);
			}
		}
		sort(itemSorts);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds()
	 */
	@Override
	public Collection<?> getSortableContainerPropertyIds() {
		return sortablePropertyIds;
	}

	/*
	 * Container.ItemSetChangeNotifier
	 */

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.ItemSetChangeNotifier#addItemSetChangeListener(
	 * com.vaadin.data.Container.ItemSetChangeListener)
	 */
	@Override
	public void addItemSetChangeListener(ItemSetChangeListener listener) {
		if (listener != null) {
			if (itemSetChangeListeners == null) {
				itemSetChangeListeners = new LinkedList<>();
			}
			if (!itemSetChangeListeners.contains(listener)) {
				itemSetChangeListeners.add(listener);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.ItemSetChangeNotifier#addListener(com.vaadin.data
	 * .Container.ItemSetChangeListener)
	 */
	@Override
	public void addListener(ItemSetChangeListener listener) {
		addItemSetChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.ItemSetChangeNotifier#removeItemSetChangeListener
	 * (com.vaadin.data.Container.ItemSetChangeListener)
	 */
	@Override
	public void removeItemSetChangeListener(ItemSetChangeListener listener) {
		if (listener != null && itemSetChangeListeners != null) {
			itemSetChangeListeners.remove(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.ItemSetChangeNotifier#removeListener(com.vaadin.
	 * data.Container.ItemSetChangeListener)
	 */
	@Override
	public void removeListener(ItemSetChangeListener listener) {
		removeItemSetChangeListener(listener);
	}

	/**
	 * Notifies that item set has been changed.
	 * @param event Item set change event
	 */
	@SuppressWarnings("serial")
	protected void notifyItemSetChanged(ItemSetChangeEvent event) {
		if (itemSetChangeListeners != null) {
			ItemSetChangeEvent evt = (event != null) ? event : new ItemSetChangeEvent() {

				@Override
				public Container getContainer() {
					return DefaultItemDataSourceContainer.this;
				}
			};
			for (ItemSetChangeListener listener : itemSetChangeListeners) {
				listener.containerItemSetChange(evt);
			}
		}
	}

	/**
	 * Event to notify an item was added to container
	 * <p>
	 * Added items will be always inserted at index 0 in this container.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes", "serial" })
	protected static final class ItemAddedEvent implements ItemAddEvent {

		private final DefaultItemDataSourceContainer container;
		private final Object itemId;

		public ItemAddedEvent(DefaultItemDataSourceContainer container, Object itemId) {
			super();
			this.container = container;
			this.itemId = itemId;
		}

		@Override
		public Container getContainer() {
			return container;
		}

		@Override
		public Object getFirstItemId() {
			return itemId;
		}

		@Override
		public int getFirstIndex() {
			return 0;
		}

		@Override
		public int getAddedItemsCount() {
			return 1;
		}

	}

	/**
	 * Event to notify an item was removed from container
	 */
	@SuppressWarnings({ "rawtypes", "serial" })
	protected static final class ItemRemovedEvent implements ItemRemoveEvent {

		private final DefaultItemDataSourceContainer container;
		private final Object itemId;
		private final int itemIndex;

		public ItemRemovedEvent(DefaultItemDataSourceContainer container, Object itemId, int itemIndex) {
			super();
			this.container = container;
			this.itemId = itemId;
			this.itemIndex = itemIndex;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.Container.ItemSetChangeEvent#getContainer()
		 */
		@Override
		public Container getContainer() {
			return container;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.Container.Indexed.ItemRemoveEvent#getFirstItemId()
		 */
		@Override
		public Object getFirstItemId() {
			return itemId;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.Container.Indexed.ItemRemoveEvent#getFirstIndex()
		 */
		@Override
		public int getFirstIndex() {
			return itemIndex;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.Container.Indexed.ItemRemoveEvent#getRemovedItemsCount()
		 */
		@Override
		public int getRemovedItemsCount() {
			return 1;
		}

	}

	/*
	 * Container.PropertySetChangeNotifier
	 */

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.PropertySetChangeNotifier# addPropertySetChangeListener(com.vaadin.data.Container.
	 * PropertySetChangeListener)
	 */
	@Override
	public void addPropertySetChangeListener(PropertySetChangeListener listener) {
		if (listener != null) {
			if (propertySetChangeListeners == null) {
				propertySetChangeListeners = new LinkedList<>();
			}
			if (!propertySetChangeListeners.contains(listener)) {
				propertySetChangeListeners.add(listener);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.PropertySetChangeNotifier#addListener(com.vaadin.
	 * data.Container.PropertySetChangeListener)
	 */
	@Deprecated
	@Override
	public void addListener(PropertySetChangeListener listener) {
		addPropertySetChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.PropertySetChangeNotifier#
	 * removePropertySetChangeListener(com.vaadin.data.Container. PropertySetChangeListener)
	 */
	@Override
	public void removePropertySetChangeListener(PropertySetChangeListener listener) {
		if (listener != null && propertySetChangeListeners != null) {
			propertySetChangeListeners.remove(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.PropertySetChangeNotifier#removeListener(com.
	 * vaadin.data.Container.PropertySetChangeListener)
	 */
	@Deprecated
	@Override
	public void removeListener(PropertySetChangeListener listener) {
		removePropertySetChangeListener(listener);
	}

	/**
	 * Notifies that property set has been changed.
	 */
	@SuppressWarnings("serial")
	protected void notifyPropertySetChanged() {
		if (propertySetChangeListeners != null) {
			PropertySetChangeEvent event = new PropertySetChangeEvent() {

				@Override
				public Container getContainer() {
					return DefaultItemDataSourceContainer.this;
				}
			};
			for (PropertySetChangeListener listener : propertySetChangeListeners) {
				listener.containerPropertySetChange(event);
			}
		}
	}

	/*
	 * Unsupported
	 */

	/**
	 * <b>Not supported</b>
	 */
	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * <b>Not supported</b>
	 * 
	 * @param index Index
	 * @return Added item
	 * @throws UnsupportedOperationException This operation is not supported by this container
	 */
	@Override
	public Object addItemAt(int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * <b>Not supported</b>
	 * 
	 * @param index index Index
	 * @param newItemId New item id
	 * @return Added item
	 * @throws UnsupportedOperationException This operation is not supported by this container
	 */
	@Override
	public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * <b>Not supported</b>
	 * 
	 * @param previousItemId Previous id
	 * @return Added item id
	 * @throws UnsupportedOperationException This operation is not supported by this container
	 */
	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * <b>Not supported</b>
	 * 
	 * @param previousItemId Previous id
	 * @param newItemId New id
	 * @return Added item
	 * @throws UnsupportedOperationException This operation is not supported by this container
	 */
	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	private static Optional<Path<?>> getPropertyPath(Object propertyId, Iterable<?> properties) {
		if (propertyId != null) {
			if (propertyId instanceof Path) {
				return Optional.of((Path<?>) propertyId);
			} else {
				Path<?> property = getPathByName(propertyId.toString(), properties);
				if (property != null) {
					return Optional.of(property);
				}
			}
		}
		return Optional.empty();
	}

	private static Path<?> getPathByName(String propertyName, Iterable<?> properties) {
		if (propertyName != null && properties != null) {
			for (Object property : properties) {
				if (property instanceof Path && propertyName.equals(((Path<?>) property).getName())) {
					return (Path<?>) property;
				}
			}
		}
		return null;
	}

}
