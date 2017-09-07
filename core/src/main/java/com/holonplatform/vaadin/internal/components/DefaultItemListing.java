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
package com.holonplatform.vaadin.internal.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyValueProvider;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.Registration;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.Selectable;
import com.holonplatform.vaadin.data.ItemDataSource;
import com.holonplatform.vaadin.data.ItemDataSource.ItemSort;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.holonplatform.vaadin.internal.converters.FontIconPresentationConverter;
import com.holonplatform.vaadin.internal.converters.PropertyPresentationConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.EditorFieldFactory;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.renderers.Renderer;

/**
 * Abstract {@link ItemListing} component implementation.
 *
 * @param <T> Item type
 * @param <P> Item property type
 *
 * @since 5.0.0
 */
public class DefaultItemListing<T, P> extends CustomComponent
		implements ItemListing<T, P>, com.vaadin.ui.Grid.RowStyleGenerator, com.vaadin.ui.Grid.CellStyleGenerator,
		TableFieldFactory, com.vaadin.ui.Table.CellStyleGenerator, com.vaadin.event.SelectionEvent.SelectionListener {

	private static final long serialVersionUID = 9034583855570611499L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = VaadinLogger.create();

	/**
	 * Listing rendering mode
	 */
	public enum RenderingMode {

		/**
		 * Render using a {@link Grid}
		 */
		GRID,

		/**
		 * Render using a {@link Table}
		 */
		TABLE;

	}

	/**
	 * Rendering mode
	 */
	private final RenderingMode renderingMode;

	/**
	 * Concrete content
	 */
	private final Component content;

	/**
	 * Data source
	 */
	private ItemDataSource<T, P> dataSource;

	/**
	 * Selection listeners
	 */
	private final List<SelectionListener<T>> selectionListeners = new LinkedList<>();

	/**
	 * Property column definitions
	 */
	private final Map<P, PropertyColumn<T, P>> propertyColumnDefinitions = new HashMap<>();

	/**
	 * Row style generators
	 */
	private final List<RowStyleGenerator<T>> rowStyleGenerators = new LinkedList<>();

	/**
	 * Auto commit on row save
	 */
	private boolean commitOnSave = false;

	/**
	 * Auto commit on row remove
	 */
	private boolean commitOnRemove = false;

	/**
	 * Selection mode
	 */
	private SelectionMode selectionMode = SelectionMode.NONE;

	/**
	 * Column hiding allowed
	 */
	private boolean columnHidingAllowed = true;

	/**
	 * Initialization state
	 */
	private boolean duringSetup = false;

	@SuppressWarnings("serial")
	public DefaultItemListing(RenderingMode renderingMode) {
		super();
		ObjectUtils.argumentNotNull(renderingMode, "RenderingMode must be not null");
		this.renderingMode = renderingMode;

		// init content
		try {
			duringSetup = true;
			if (renderingMode == RenderingMode.TABLE) {
				content = new Table() {

					@Override
					public void refreshRowCache() {
						if (duringSetup) {
							return;
						}
						super.refreshRowCache();
					}

					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
					protected String formatPropertyValue(Object rowId, Object colId,
							com.vaadin.data.Property<?> itemProperty) {
						if (colId instanceof Property) {
							return ((Property) colId).present(itemProperty.getValue());
						}
						return super.formatPropertyValue(rowId, colId, itemProperty);
					}

				};
				initTable((Table) content);
			} else {
				content = new Grid() {

					@Override
					public void saveEditor() throws CommitException {
						super.saveEditor();
						// check auto commit on save
						if (isCommitOnSave()) {
							requireDataSource().commit();
						}
					}

				};
				initGrid((Grid) content);
			}
		} finally {
			duringSetup = false;
		}

		super.setWidth(100, Unit.PERCENTAGE);
		addStyleName("h-itemlisting", false);

		setCompositionRoot(content);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setHeight(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public void setHeight(float height, Unit unit) {
		super.setHeight(height, unit);
		if (height > -1 && getCompositionRoot() != null) {
			getCompositionRoot().setHeight(100, Unit.PERCENTAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setWidth(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public void setWidth(float width, Unit unit) {
		super.setWidth(width, unit);
		if (width > -1 && getCompositionRoot() != null) {
			getCompositionRoot().setWidth(100, Unit.PERCENTAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#addStyleName(java.lang.String)
	 */
	@Override
	public void addStyleName(String style) {
		addStyleName(style, true);
	}

	/**
	 * Adds one or more style names to this component.
	 * @param styleName Style name to add
	 * @param reflectToContent <code>true</code> to add given <code>styleName</code> to content component too
	 */
	protected void addStyleName(String styleName, boolean reflectToContent) {
		super.addStyleName(styleName);
		if (reflectToContent) {
			getContent().addStyleName(styleName);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#removeStyleName(java.lang.String)
	 */
	@Override
	public void removeStyleName(String style) {
		super.removeStyleName(style);
		getContent().removeStyleName(style);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		// reflect to content
		getContent().setReadOnly(readOnly);
	}

	/**
	 * Get the rendering mode
	 * @return the rendering mode
	 */
	protected RenderingMode getRenderingMode() {
		return renderingMode;
	}

	/**
	 * Grid initialization
	 * @param grid Grid to initialize
	 */
	protected void initGrid(Grid grid) {
		// reset selection model
		grid.setSelectionMode(com.vaadin.ui.Grid.SelectionMode.NONE);

		grid.setRowStyleGenerator(this);
		grid.setCellStyleGenerator(this);

		// editor FieldGroup
		grid.setEditorFieldGroup(new PropertyGridFieldGroup());
	}

	/**
	 * Table initialization
	 * @param table Table to initialize
	 */
	protected void initTable(Table table) {
		table.setTableFieldFactory(this);
		table.setCellStyleGenerator(this);
	}

	/**
	 * Get the content
	 * @return the content
	 */
	protected Component getContent() {
		return content;
	}

	/**
	 * Get the content {@link Grid}.
	 * @return Content grid
	 */
	public Grid getGrid() {
		return (Grid) getContent();
	}

	/**
	 * Get the content {@link Table}.
	 * @return Content table
	 */
	public Table getTable() {
		return (Table) getContent();
	}

	/**
	 * Get the datasource.
	 * @return the datasource
	 */
	protected Optional<ItemDataSource<T, P>> getDataSource() {
		return Optional.ofNullable(dataSource);
	}

	/**
	 * Get the datasource.
	 * @return the datasource
	 * @throws IllegalStateException If data source is not configured
	 */
	protected ItemDataSource<T, P> requireDataSource() {
		return getDataSource().orElseThrow(() -> new IllegalStateException("Missing ItemDataSource"));
	}

	/**
	 * Set the listing data source.
	 * @param <D> Data source type
	 * @param container The data source to set (not null)
	 */
	public <D extends ItemDataSource<T, P> & Indexed> void setDataSource(D container) {
		ObjectUtils.argumentNotNull(container, "Container datasource must be not null");

		this.dataSource = container;

		switch (getRenderingMode()) {
		case GRID:
			getGrid().setContainerDataSource(container);
			break;
		case TABLE:
			getTable().setContainerDataSource(container);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemSetComponent#refresh()
	 */
	@Override
	public void refresh() {
		requireDataSource().refresh();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#clear()
	 */
	@Override
	public void clear() {
		if (getSelectionMode() != Selectable.SelectionMode.NONE) {
			deselectAll();
		}
		requireDataSource().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectionMode()
	 */
	@Override
	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ItemListing#setSelectionMode(com.holonplatform.vaadin.components.Selectable
	 * .SelectionMode)
	 */
	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
		ObjectUtils.argumentNotNull(selectionMode, "SelectionMode must be not null");
		if (this.selectionMode != selectionMode) {
			this.selectionMode = selectionMode;
			switch (getRenderingMode()) {
			case GRID: {
				final Grid grid = getGrid();
				grid.removeSelectionListener(this);
				switch (selectionMode) {
				case MULTI:
					grid.setSelectionMode(Grid.SelectionMode.MULTI);
					grid.addSelectionListener(this);
					break;
				case SINGLE:
					grid.setSelectionMode(Grid.SelectionMode.SINGLE);
					grid.addSelectionListener(this);
					break;
				case NONE:
				default:
					grid.setSelectionMode(Grid.SelectionMode.NONE);
					break;
				}
			}
				break;
			case TABLE: {
				final Table table = getTable();
				table.removeValueChangeListener(tableSelectionChangeListener);
				table.setValue(null);
				if (selectionMode != null && selectionMode != SelectionMode.NONE) {
					table.setSelectable(true);
					table.setMultiSelect(selectionMode == SelectionMode.MULTI);
					table.addValueChangeListener(tableSelectionChangeListener);
				} else {
					table.setSelectable(false);
				}
			}
				break;
			default:
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#selectAllItems()
	 */
	@Override
	public void selectAll() {
		switch (getRenderingMode()) {
		case GRID:
			requireDataSource().getItemIds().forEach(i -> getGrid().select(i));
			break;
		case TABLE:
			getTable().setValue(new HashSet<>(requireDataSource().getItemIds()));
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectedItems()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getSelectedItems() {
		Collection<Object> selectedIds = null;
		switch (getRenderingMode()) {
		case GRID:
			selectedIds = getGrid().getSelectionModel().getSelectedRows();
			break;
		case TABLE: {
			Object value = getTable().getValue();
			if (value != null) {
				if (getTable().isMultiSelect()) {
					selectedIds = (Collection<Object>) value;
				} else {
					selectedIds = Collections.singleton(value);
				}
			}
		}
			break;
		default:
			break;
		}
		if (selectedIds != null) {
			Set<T> selected = new HashSet<>(selectedIds.size());
			selectedIds.forEach(s -> {
				requireDataSource().get(s).ifPresent(i -> selected.add(i));
			});
			return selected;
		}
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	public Optional<T> getFirstSelectedItem() {
		final Set<T> selected = getSelectedItems();
		return selected.isEmpty() ? Optional.empty() : Optional.of(selected.iterator().next());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#selectItem(java.lang.Object)
	 */
	@Override
	public void select(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		switch (getRenderingMode()) {
		case GRID:
			getGrid().select(requireDataSource().getId(item));
			break;
		case TABLE:
			getTable().select(requireDataSource().getId(item));
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselectItem(java.lang.Object)
	 */
	@Override
	public void deselect(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		switch (getRenderingMode()) {
		case GRID:
			getGrid().deselect(requireDataSource().getId(item));
			break;
		case TABLE:
			getTable().unselect(requireDataSource().getId(item));
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselectAllItems()
	 */
	@Override
	public void deselectAll() {
		switch (getRenderingMode()) {
		case GRID:
			getGrid().deselectAll();
			break;
		case TABLE:
			getTable().setValue(null);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#addSelectionListener(com.holonplatform.vaadin.components.
	 * Selectable.SelectionListener)
	 */
	@Override
	public Registration addSelectionListener(SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		selectionListeners.add(selectionListener);
		return () -> selectionListeners.remove(selectionListener);
	}

	/**
	 * Fire registered {@link SelectionListener}s.
	 */
	protected void fireSelectionListeners() {
		for (SelectionListener<T> selectionListener : selectionListeners) {
			selectionListener.onSelectionChange(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.event.SelectionEvent.SelectionListener#select(com.vaadin.event.SelectionEvent)
	 */
	@Override
	public void select(SelectionEvent event) {
		fireSelectionListeners();
	}

	public void setPropertyColumns(Iterable<P> columns) {
		ObjectUtils.argumentNotNull(columns, "Property columns must be not null");

		final Object[] columnsArray = ConversionUtils.iterableAsList(columns).toArray();

		switch (getRenderingMode()) {
		case GRID: {
			final Grid grid = getGrid();
			// set columns
			grid.setColumns(columnsArray);
			// setup columns
			for (P column : columns) {
				Column c = grid.getColumn(column);
				// setup column
				setupGridPropertyColumn(column, c);
				// setup renderer and converter
				setupRendererAndConverter(column, c);
			}
		}
			break;
		case TABLE: {
			final Table table = getTable();
			try {
				duringSetup = true;
				// Setup columns and check Component generated columns
				for (P property : columns) {
					// column settings
					setupTablePropertyColumn(property, table);
					// generated columns
					Class<?> type = getPropertyColumnType(property);
					if (type != null && Component.class.isAssignableFrom(type)) {
						if (table.getColumnGenerator(property) == null) {
							table.addGeneratedColumn(property, new VirtualPropertyGenerator());
						}
					}
				}
			} finally {
				duringSetup = false;
			}
			// set columns
			table.setVisibleColumns(columnsArray);
		}
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#getPropertyColumns()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<P> getPropertyColumns() {
		switch (getRenderingMode()) {
		case GRID: {
			List<Column> columns = getGrid().getColumns();
			if (columns != null && !columns.isEmpty()) {
				List<P> properties = new ArrayList<>(columns.size());
				for (Column column : columns) {
					properties.add((P) column.getPropertyId());
				}
				return properties;
			}
		}
			break;
		case TABLE: {
			Object[] visibleColumns = getTable().getVisibleColumns();
			if (visibleColumns != null && visibleColumns.length > 0) {
				List<P> properties = new ArrayList<>(visibleColumns.length);
				for (Object column : visibleColumns) {
					properties.add((P) column);
				}
				return properties;
			}
		}
			break;
		default:
			break;
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#setPropertyColumnVisible(java.lang.Object, boolean)
	 */
	@Override
	public void setPropertyColumnVisible(P property, boolean visible) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		if (!hasPropertyColumn(property)) {
			throw new IllegalArgumentException("Property " + property + " is not a column of the listing");
		}
		switch (getRenderingMode()) {
		case GRID:
			getGrid().getColumn(property).setHidden(!visible);
			break;
		case TABLE:
			getTable().setColumnCollapsed(property, !visible);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#isFooterVisible()
	 */
	@Override
	public boolean isFooterVisible() {
		switch (getRenderingMode()) {
		case GRID:
			return getGrid().isFooterVisible();
		case TABLE:
			return getTable().isFooterVisible();
		default:
			break;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#setFooterVisible(boolean)
	 */
	@Override
	public void setFooterVisible(boolean visible) {
		switch (getRenderingMode()) {
		case GRID:
			getGrid().setFooterVisible(visible);
			break;
		case TABLE:
			getTable().setFooterVisible(visible);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#isItemDetailsVisible(java.lang.Object)
	 */
	@Override
	public boolean isItemDetailsVisible(T item) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#setItemDetailsVisible(java.lang.Object, boolean)
	 */
	@Override
	public void setItemDetailsVisible(T item, boolean visible) throws UnsupportedOperationException {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		switch (getRenderingMode()) {
		case GRID:
			getGrid().setDetailsVisible(requireDataSource().getId(item), visible);
			break;
		case TABLE:
			throw new UnsupportedOperationException();
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#scrollToTop()
	 */
	@Override
	public void scrollToTop() {
		switch (getRenderingMode()) {
		case GRID:
			getGrid().scrollToStart();
			break;
		case TABLE:
			getTable().setCurrentPageFirstItemIndex(0);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#scrollToItem(java.lang.Object)
	 */
	@Override
	public void scrollToItem(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		switch (getRenderingMode()) {
		case GRID: {
			Object id = requireDataSource().getId(item);
			if (id != null) {
				getGrid().scrollTo(id);
			}
		}
			break;
		case TABLE: {
			Object id = requireDataSource().getId(item);
			if (id != null) {
				getTable().setCurrentPageFirstItemId(id);
			}
		}
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ItemListing#sort(com.holonplatform.vaadin.data.ItemDataSource.ItemSort[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void sort(ItemSort<P>... sorts) {
		if (sorts != null && sorts.length > 0) {
			switch (getRenderingMode()) {
			case GRID: {
				Sort gridSort = null;
				for (ItemSort<P> sort : sorts) {
					if (gridSort == null) {
						gridSort = Sort.by(sort.getProperty(),
								sort.isAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING);
					} else {
						gridSort.then(sort.getProperty(),
								sort.isAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING);
					}
				}
				getGrid().sort(gridSort);
			}
				break;
			case TABLE: {
				Object[] pids = new Object[sorts.length];
				boolean[] states = new boolean[sorts.length];
				for (int i = 0; i < sorts.length; i++) {
					pids[i] = sorts[i].getProperty();
					states[i] = sorts[i].isAscending();
				}
				getTable().sort(pids, states);
			}
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void repaintRows(T... items) {
		if (items != null) {
			switch (getRenderingMode()) {
			case GRID: {
				List<Object> ids = new LinkedList<>();
				for (T item : items) {
					Object id = requireDataSource().getId(item);
					if (id != null) {
						ids.add(id);
					}
				}
				if (!ids.isEmpty()) {
					getGrid().refreshRows(ids.toArray(new Object[ids.size()]));
				}
			}
				break;
			case TABLE: {
				getTable().refreshRowCache();
			}
				break;
			default:
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#getItemById(java.lang.Object)
	 */
	@Override
	public Optional<T> getItem(Object itemId) {
		return requireDataSource().get(itemId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#addItem(java.lang.Object)
	 */
	@Override
	public Object addItem(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		return requireDataSource().add(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#removeItem(java.lang.Object)
	 */
	@Override
	public boolean removeItem(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		final Object id = requireDataSource().getId(item);
		boolean removed = requireDataSource().remove(item);

		// check commit on remove
		if (isCommitOnRemove()) {
			commit();
		}

		if (removed && id != null && getSelectionMode() != Selectable.SelectionMode.NONE) {
			switch (getRenderingMode()) {
			case GRID:
				getGrid().deselect(id);
				break;
			case TABLE:
				getTable().unselect(id);
				break;
			default:
				break;

			}
		}
		return removed;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#refreshItem(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void refreshItem(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		requireDataSource().refresh(item);
		repaintRows(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#commit()
	 */
	@Override
	public void commit() {
		requireDataSource().commit();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#discard()
	 */
	@Override
	public void discard() {
		requireDataSource().discard();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#isBuffered()
	 */
	@Override
	public boolean isBuffered() {
		return requireDataSource().isBuffered();
	}

	/**
	 * Add a {@link RowStyleGenerator}
	 * @param rowStyleGenerator Generator to add (not null)
	 */
	public void addRowStyleGenerator(RowStyleGenerator<T> rowStyleGenerator) {
		ObjectUtils.argumentNotNull(rowStyleGenerator, "RowStyleGenerator must be not null");
		rowStyleGenerators.add(rowStyleGenerator);
	}

	/**
	 * Remove a {@link RowStyleGenerator}.
	 * @param rowStyleGenerator Generator to remove (not null)
	 */
	public void removeRowStyleGenerator(RowStyleGenerator<T> rowStyleGenerator) {
		ObjectUtils.argumentNotNull(rowStyleGenerator, "RowStyleGenerator must be not null");
		rowStyleGenerators.remove(rowStyleGenerator);
	}

	/**
	 * Set the buffered mode
	 * @param buffered Buffered mode
	 */
	public void setBuffered(boolean buffered) {
		switch (getRenderingMode()) {
		case GRID:
			getGrid().setEditorBuffered(buffered);
			break;
		case TABLE:
			getTable().setBuffered(buffered);
			break;
		default:
			break;
		}
	}

	/**
	 * Set whether the listing is editable.
	 * @param editable whether the listing is editable
	 */
	public void setEditable(boolean editable) {
		switch (getRenderingMode()) {
		case GRID:
			getGrid().setEditorEnabled(editable);
			break;
		case TABLE:
			getTable().setEditable(true);
			break;
		default:
			break;
		}
	}

	/**
	 * Set whether the listing column headers are visible.
	 * @param headersVisible whether the listing column headers are visible
	 */
	public void setHeadersVisible(boolean headersVisible) {
		switch (getRenderingMode()) {
		case GRID:
			getGrid().setHeaderVisible(headersVisible);
			break;
		case TABLE:
			getTable().setColumnHeaderMode(
					headersVisible ? ColumnHeaderMode.EXPLICIT_DEFAULTS_ID : ColumnHeaderMode.HIDDEN);
			break;
		default:
			break;
		}
	}

	/**
	 * Add an {@link ItemClickListener} to be notified when user clicks on an item row.
	 * @param listener Listener to add (not null)
	 */
	@SuppressWarnings("unchecked")
	public void addItemClickListener(final ItemClickListener<T, P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		switch (getRenderingMode()) {
		case GRID:
			getGrid().addItemClickListener(e -> getItem(e.getItemId()).ifPresent(i -> {
				listener.onItemClick(i, (P) e.getPropertyId(), fromClickEvent(e));
			}));
			break;
		case TABLE:
			getTable().addItemClickListener(e -> getItem(e.getItemId()).ifPresent(i -> {
				listener.onItemClick(i, (P) e.getPropertyId(), fromClickEvent(e));
			}));
			break;
		default:
			break;
		}
	}

	private static MouseEventDetails fromClickEvent(ItemClickEvent event) {
		MouseEventDetails d = new MouseEventDetails();
		d.setButton(event.getButton());
		d.setAltKey(event.isAltKey());
		d.setCtrlKey(event.isCtrlKey());
		d.setShiftKey(event.isShiftKey());
		d.setType(event.isDoubleClick() ? 0x00002 : 0);
		d.setClientX(event.getClientX());
		d.setClientY(event.getClientY());
		d.setRelativeX(event.getRelativeX());
		d.setRelativeY(event.getRelativeY());
		return d;
	}

	/**
	 * Adds a {@link PropertyReorderListener} that gets notified when property columns order changes.
	 * @param listener Listener to add (not null)
	 */
	public void addPropertyReorderListener(final PropertyReorderListener<P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		switch (getRenderingMode()) {
		case GRID:
			getGrid().addColumnReorderListener(
					e -> listener.onPropertyReordered(getPropertyColumns(), e.isUserOriginated()));
			break;
		case TABLE:
			getTable().addColumnReorderListener(e -> listener.onPropertyReordered(getPropertyColumns(), true));
			break;
		default:
			break;
		}
	}

	/**
	 * Adds a {@link PropertyResizeListener} that gets notified when a property column is resized.
	 * @param listener Listener to add (not null)
	 */
	@SuppressWarnings("unchecked")
	public void addPropertyResizeListener(final PropertyResizeListener<P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		switch (getRenderingMode()) {
		case GRID:
			getGrid().addColumnResizeListener(e -> listener.onPropertyResized((P) e.getColumn().getPropertyId(),
					(int) e.getColumn().getWidth(), e.isUserOriginated()));
			break;
		case TABLE:
			getTable().addColumnResizeListener(
					e -> listener.onPropertyResized((P) e.getPropertyId(), e.getCurrentWidth(), true));
			break;
		default:
			break;
		}
	}

	/**
	 * Adds a {@link PropertyVisibilityListener} that gets notified when a property column is hidden or shown.
	 * @param listener Listener to add (not null)
	 */
	@SuppressWarnings("unchecked")
	public void addPropertyVisibilityListener(final PropertyVisibilityListener<P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		switch (getRenderingMode()) {
		case GRID:
			getGrid().addColumnVisibilityChangeListener(e -> listener.onPropertyVisibilityChanged(
					(P) e.getColumn().getPropertyId(), e.isHidden(), e.isUserOriginated()));
			break;
		case TABLE:
			getTable().addColumnCollapseListener(e -> listener.onPropertyVisibilityChanged((P) e.getPropertyId(),
					getTable().isColumnCollapsed(e.getPropertyId()), true));
			break;
		default:
			break;
		}
	}

	/**
	 * Set the {@link ItemDescriptionGenerator} to use to generate item descriptions (tooltips).
	 * @param rowDescriptionGenerator Generator to set (not null)
	 */
	public void setDescriptionGenerator(final ItemDescriptionGenerator<T> rowDescriptionGenerator) {
		ObjectUtils.argumentNotNull(rowDescriptionGenerator, "Generator must be not null");
		switch (getRenderingMode()) {
		case GRID:
			getGrid().setRowDescriptionGenerator(row -> getItem(row.getItemId())
					.map(i -> rowDescriptionGenerator.getItemDescription(i)).orElse(null));
			break;
		case TABLE:
			getTable().setItemDescriptionGenerator(
					(c, id, p) -> getItem(id).map(i -> rowDescriptionGenerator.getItemDescription(i)).orElse(null));
			break;
		default:
			break;
		}
	}

	/**
	 * Sets whether column hiding by user is allowed or not.
	 * @param columnHidingAllowed <code>true</code> if column hiding is allowed
	 */
	public void setColumnHidingAllowed(boolean columnHidingAllowed) {
		switch (getRenderingMode()) {
		case GRID:
			propertyColumnDefinitions.values().forEach(c -> c.setHidable(false));
			break;
		case TABLE:
			getTable().setColumnCollapsingAllowed(columnHidingAllowed);
			break;
		default:
			break;
		}
	}

	/**
	 * Sets whether column reordering is allowed or not.
	 * @param columnReorderingAllowed <code>true</code> if column reordering is allowed
	 */
	public void setColumnReorderingAllowed(boolean columnReorderingAllowed) {
		switch (getRenderingMode()) {
		case GRID:
			getGrid().setColumnReorderingAllowed(columnReorderingAllowed);
			break;
		case TABLE:
			getTable().setColumnReorderingAllowed(columnReorderingAllowed);
			break;
		default:
			break;
		}
	}

	/**
	 * Check whether to call commit() on data source container when Grid editor save action is triggered
	 * @return <code>true</code> if should call commit() on data source container when Grid editor save action is
	 *         triggered
	 */
	public boolean isCommitOnSave() {
		return commitOnSave;
	}

	/**
	 * Sets whether to call commit() on data source container when Grid editor save action is triggered
	 * @param commitOnSave <code>true</code> to call commit() on data source container when Grid editor save action is
	 *        triggered
	 */
	public void setCommitOnSave(boolean commitOnSave) {
		this.commitOnSave = commitOnSave;
	}

	/**
	 * Check whether to call commit() on data source container when a row is removed using
	 * {@link ItemDataSource#remove(Object)}.
	 * @return <code>true</code> if should call commit() on data source container when a row is removed using
	 *         {@link ItemDataSource#remove(Object)}
	 */
	public boolean isCommitOnRemove() {
		return commitOnRemove;
	}

	/**
	 * Sets to whether call commit() on data source container when a row is removed using
	 * {@link ItemDataSource#remove(Object)}.
	 * @param commitOnRemove <code>true</code> to call commit() on data source container when a row is removed using
	 *        {@link ItemDataSource#remove(Object)}
	 */
	public void setCommitOnRemove(boolean commitOnRemove) {
		this.commitOnRemove = commitOnRemove;
	}

	// ------- Exposed for builders

	/**
	 * Get or create the {@link PropertyColumn} definition bound to given property.
	 * @param property Property to get the definition for (not null)
	 * @return Property column definition
	 */
	public PropertyColumn<T, P> getPropertyColumn(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		PropertyColumn<T, P> propertyColumn = propertyColumnDefinitions.get(property);
		if (propertyColumn == null) {
			propertyColumn = buildPropertyColumn(property);
			propertyColumnDefinitions.put(property, propertyColumn);
		}
		return propertyColumn;
	}

	/**
	 * Build a {@link PropertyColumn} definition.
	 * @param property Property id
	 * @return a new {@link PropertyColumn} definition
	 */
	protected PropertyColumn<T, P> buildPropertyColumn(P property) {
		return new DefaultPropertyColumn<>(property);
	}

	/**
	 * Adds an {@link ItemSetChangeListener} to internal component container.
	 * @param listener Listener to add
	 */
	public void addItemSetChangeListener(Container.ItemSetChangeListener listener) {
		Container container = null;
		switch (getRenderingMode()) {
		case GRID:
			container = getGrid().getContainerDataSource();
			break;
		case TABLE:
			container = getTable().getContainerDataSource();
			break;
		default:
			break;
		}
		if (container != null && container instanceof ItemSetChangeNotifier) {
			((ItemSetChangeNotifier) container).addItemSetChangeListener(listener);
		}
	}

	// ------- Internal

	/**
	 * Check whether given property is present as a colummn
	 * @param property Property to check
	 * @return <code>true</code> if present
	 */
	protected boolean hasPropertyColumn(P property) {
		return property != null && getPropertyColumns().contains(property);
	}

	/**
	 * Get the property column type
	 * @param property Property
	 * @return Property type
	 */
	protected Class<?> getPropertyColumnType(P property) {
		if (property != null) {
			return requireDataSource().getConfiguration().getPropertyType(property);
		}
		return null;
	}

	/**
	 * Generate the row style names for given <code>item</code> using registered row style generators.
	 * @param item Item
	 * @return Row styles
	 */
	protected String generateRowStyle(T item) {
		StringBuilder sb = new StringBuilder();
		if (item != null && !rowStyleGenerators.isEmpty()) {
			for (RowStyleGenerator<T> rowStyleGenerator : rowStyleGenerators) {
				String style = rowStyleGenerator.getRowStyle(item);
				if (style != null && !style.trim().equals("")) {
					if (sb.length() > 0) {
						sb.append(" ");
					}
					sb.append(style);
				}
			}
		}
		return (sb.length() > 0) ? sb.toString() : null;
	}

	/**
	 * Generate cell style names for given <code>property</code> and <code>item</code> using column definition.
	 * @param property Column property
	 * @param item Item
	 * @return Cell style names
	 */
	protected String generatePropertyStyle(P property, T item) {
		PropertyColumn<T, P> column = getPropertyColumn(property);
		if (column != null && (column.getStyle() != null || column.getAlignment() != null)) {
			final StringBuilder sb = new StringBuilder();
			if (column.getAlignment() != null) {
				if (ColumnAlignment.CENTER.equals(column.getAlignment())) {
					sb.append("v-align-center");
				} else if (ColumnAlignment.RIGHT.equals(column.getAlignment())) {
					sb.append("v-align-right");
				}
			}
			if (column.getStyle() != null) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				String cellStyle = column.getStyle().getCellStyle(property, item);
				if (cellStyle != null) {
					sb.append(cellStyle);
				}
			}
			return (sb.length() > 0) ? sb.toString() : null;
		}
		return null;
	}

	/**
	 * Try to render given property as a {@link Field} using a suitable {@link PropertyRenderer}, if available
	 * @param property Property to render as Field
	 * @return Optional Field
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Optional<Field> renderField(P property) {
		if (Property.class.isAssignableFrom(property.getClass())) {
			return ((Property) property).renderIfAvailable(Field.class);
		}
		return Optional.empty();
	}

	/**
	 * Setup a renderer field, setting up validators if available.
	 * @param property Property
	 * @param field Field
	 */
	protected void setupField(P property, Field<?> field) {
		PropertyColumn<T, P> column = getPropertyColumn(property);
		if (column != null) {
			if (column.isRequired()) {
				field.setRequired(true);
				if (column.getRequiredMessage() != null) {
					field.setRequiredError(LocalizationContext.translate(column.getRequiredMessage(), true));
				}
			}
			column.getValidators().forEach(v -> {
				field.addValidator(v);
			});
		}
	}

	// ------- Grid

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.Grid.RowStyleGenerator#getStyle(com.vaadin.ui.Grid.RowReference)
	 */
	@Override
	public String getStyle(RowReference row) {
		if (!rowStyleGenerators.isEmpty()) {
			return generateRowStyle(requireDataSource().get(row.getItemId()).orElse(null));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.Grid.CellStyleGenerator#getStyle(com.vaadin.ui.Grid.CellReference)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getStyle(CellReference cell) {
		return generatePropertyStyle((P) cell.getPropertyId(), requireDataSource().get(cell.getItemId()).orElse(null));
	}

	/**
	 * Set the grid row details generator
	 * @param detailsGenerator Generator to set (not null)
	 */
	public void setDetailsGenerator(final ItemDetailsGenerator<T> detailsGenerator) {
		ObjectUtils.argumentNotNull(detailsGenerator, "Generator must be not null");
		getGrid().setDetailsGenerator(
				row -> getItem(row.getItemId()).map(i -> detailsGenerator.getItemDetails(i)).orElse(null));
	}

	/**
	 * Setup column configuration for given <code>property</code> using its {@link PropertyColumn} definition.
	 * @param property Property to which the column is bound
	 * @param column Column to setup
	 */
	protected void setupGridPropertyColumn(P property, Column column) {
		PropertyColumn<T, P> propertyColumn = getPropertyColumn(property);
		if (propertyColumn != null) {
			// header
			if (propertyColumn.getCaption() != null) {
				String header = LocalizationContext.translate(propertyColumn.getCaption(), true);
				if (header != null) {
					column.setHeaderCaption(header);
				}
			}
			// sortable
			column.setSortable(requireDataSource().getConfiguration().isPropertySortable(property));
			// width
			if (propertyColumn.getWidth() > -1) {
				column.setWidth(propertyColumn.getWidth());
			}
			if (propertyColumn.getMinWidth() > -1) {
				column.setMinimumWidth(propertyColumn.getMinWidth());
			}
			if (propertyColumn.getMaxWidth() > -1) {
				column.setMaximumWidth(propertyColumn.getMinWidth());
			}
			// expand
			if (propertyColumn.getGridExpandRatio() > -1) {
				column.setExpandRatio(propertyColumn.getGridExpandRatio());
			}
			// editing
			if (requireDataSource().getConfiguration().isPropertyReadOnly(property)) {
				column.setEditable(false);
			} else {
				column.setEditable(propertyColumn.isEditable());
				if (propertyColumn.isEditable()) {
					propertyColumn.getEditor().ifPresent(e -> column.setEditorField(e));
				}
			}
			// hiding
			if (columnHidingAllowed && propertyColumn.isHidable()) {
				column.setHidable(true);
				if (propertyColumn.isHidden()) {
					column.setHidden(true);
				}
				if (propertyColumn.getHidingToggleCaption() != null) {
					column.setHidingToggleCaption(
							LocalizationContext.translate(propertyColumn.getHidingToggleCaption()));
				}
			} else {
				column.setHidable(false);
			}
		}
	}

	/**
	 * Setup {@link Renderer}s and {@link Converter}s for given <code>property</code> column, using
	 * {@link PropertyColumn} definition and falling back to defaults.
	 * @param property Property to which the column is bound
	 * @param column Column to setup
	 */
	protected void setupRendererAndConverter(P property, Column column) {
		PropertyColumn<T, P> propertyColumn = getPropertyColumn(property);
		if (propertyColumn != null) {

			// Converter
			if (propertyColumn.getConverter() != null) {
				column.setConverter(propertyColumn.getConverter());
			} else {
				// ue default, if available
				getDefaultPropertyConverter(property).ifPresent((c) -> column.setConverter(c));
			}

			// Renderer
			if (propertyColumn.getRenderer() != null) {
				column.setRenderer(propertyColumn.getRenderer());
			} else {
				// use default
				getDefaultPropertyRenderer(property).ifPresent(r -> column.setRenderer(r));
			}

		}
	}

	/**
	 * Get the default {@link Converter} for given <code>property</code>.
	 * @param property Property
	 * @return The default {@link Converter}, if available
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Optional<Converter<?, ?>> getDefaultPropertyConverter(final P property) {
		Converter<?, ?> converter = null;
		Class<?> type = getPropertyColumnType(property);
		// FontIcons
		if (type != null && FontIcon.class.isAssignableFrom(type)) {
			converter = new FontIconPresentationConverter();
		} else {
			// Use default property presentation converter
			if (Property.class.isAssignableFrom(property.getClass())) {
				converter = new PropertyPresentationConverter<>((Property) property);
			}
		}
		return Optional.ofNullable(converter);
	}

	/**
	 * Get the default {@link Renderer} for given <code>property</code>.
	 * @param property Property
	 * @return The default {@link Renderer}, if available
	 */
	protected Optional<Renderer<?>> getDefaultPropertyRenderer(P property) {
		Class<?> type = getPropertyColumnType(property);
		// Images
		if (type != null
				&& (ExternalResource.class.isAssignableFrom(type) || ThemeResource.class.isAssignableFrom(type))) {
			return Optional.of(new ImageRenderer());
		}
		if (type != null && FontIcon.class.isAssignableFrom(type)) {
			return Optional.of(new HtmlRenderer(""));
		}
		return Optional.empty();
	}

	/**
	 * Grid {@link FieldGroup} extension for {@link Property} support in row editing.
	 */
	@SuppressWarnings("serial")
	private final class PropertyGridFieldGroup extends FieldGroup {

		public PropertyGridFieldGroup() {
			super();
			setFieldFactory(EditorFieldFactory.get());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Class<?> getPropertyType(Object propertyId) throws BindException {
			// If property is a Property, return its type
			if (propertyId != null && Property.class.isAssignableFrom(propertyId.getClass())) {
				return ((Property<?>) propertyId).getType();
			}
			if (getItemDataSource() == null) {
				return requireDataSource().getConfiguration().getPropertyType((P) propertyId);
			} else {
				return super.getPropertyType(propertyId);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.fieldgroup.FieldGroup#buildAndBind(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Field<?> buildAndBind(Object propertyId) throws BindException {
			// If property is a Property, try to render Field using UIContext
			if (propertyId != null && Property.class.isAssignableFrom(propertyId.getClass())) {
				Field<?> field = renderField((P) propertyId).map((f) -> {
					setupField((P) propertyId, f);
					if (f instanceof CheckBox)
						f.setCaption(null);
					bind(f, propertyId);
					return f;
				}).orElse(null);
				if (field != null) {
					return field;
				}
			}
			return super.buildAndBind(propertyId);
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected <F extends Field> F build(String caption, Class<?> dataType, Class<F> fieldType)
				throws BindException {
			F field = super.build(caption, dataType, fieldType);
			if (field instanceof CheckBox) {
				field.setCaption(null);
			}
			return field;
		}

		@Override
		protected void bindFields() {
			List<Field<?>> fields = new ArrayList<>(getFields());
			Item itemDataSource = getItemDataSource();
			if (itemDataSource == null) {
				unbindFields(fields);
			} else {
				bindFields(fields, itemDataSource);
			}
		}

		private void unbindFields(List<Field<?>> fields) {
			for (Field<?> field : fields) {
				clearField(field);
				unbind(field);
				field.setParent(null);
			}
		}

		private void bindFields(List<Field<?>> fields, Item itemDataSource) {
			for (Field<?> field : fields) {
				if (itemDataSource.getItemProperty(getPropertyId(field)) != null) {
					bind(field, getPropertyId(field));
				}
			}
		}

	}

	// ------- Table

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.Table.CellStyleGenerator#getStyle(com.vaadin.ui.Table, java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getStyle(Table source, final Object itemId, final Object propertyId) {
		return requireDataSource().get(itemId).map(i -> {
			if (propertyId == null) {
				// row style
				if (!rowStyleGenerators.isEmpty()) {
					return generateRowStyle(i);
				}
			} else {
				// property (cell) style
				return generatePropertyStyle((P) propertyId, i);
			}
			return null;
		}).orElse(null);
	}

	/**
	 * Setup column configuration for given <code>property</code> using its {@link PropertyColumn} definition.
	 * @param property Property to which the column is bound
	 * @param table Table to setup
	 */
	protected void setupTablePropertyColumn(P property, Table table) {
		PropertyColumn<T, P> propertyColumn = getPropertyColumn(property);
		if (propertyColumn != null) {
			// header
			if (propertyColumn.getCaption() != null) {
				String header = LocalizationContext.translate(propertyColumn.getCaption(), true);
				if (header != null) {
					table.setColumnHeader(property, header);
				}
			}
			// alignment
			if (propertyColumn.getAlignment() != null) {
				switch (propertyColumn.getAlignment()) {
				case CENTER:
					table.setColumnAlignment(property, Align.CENTER);
					break;
				case LEFT:
					table.setColumnAlignment(property, Align.LEFT);
					break;
				case RIGHT:
					table.setColumnAlignment(property, Align.RIGHT);
					break;
				default:
					break;
				}
			}
			// width
			if (propertyColumn.getWidth() > -1) {
				table.setColumnWidth(property, propertyColumn.getWidth());
			}
			// expand
			if (propertyColumn.getTableExpandRatio() > -1) {
				table.setColumnExpandRatio(property, propertyColumn.getTableExpandRatio());
			}
			// hiding
			if (propertyColumn.isHidable()) {
				table.setColumnCollapsible(property, true);
				if (propertyColumn.isHidden()) {
					table.setColumnCollapsed(property, true);
				}
			} else {
				table.setColumnCollapsible(property, false);
			}
			// icon
			if (propertyColumn.getIcon() != null) {
				table.setColumnIcon(property, propertyColumn.getIcon());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.TableFieldFactory#createField(com.vaadin.data.Container, java.lang.Object, java.lang.Object,
	 * com.vaadin.ui.Component)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
		P property = (P) propertyId;

		// check read-only property
		if (requireDataSource().getConfiguration().isPropertyReadOnly(property)) {
			return null;
		}

		// check editable and custom editor
		PropertyColumn<T, P> propertyColumn = getPropertyColumn(property);
		if (propertyColumn != null) {
			if (!propertyColumn.isEditable()) {
				return null;
			}

			if (propertyColumn.getEditor().isPresent()) {
				return propertyColumn.getEditor().get();
			}
		}

		// check PropertyRenderer or use DefaultFieldFactory
		return renderField(property)
				.orElse(DefaultFieldFactory.get().createField(container, itemId, propertyId, uiContext));

	}

	/**
	 * A {@link ColumnGenerator} using a {@link VirtualProperty} as content source.
	 */
	@SuppressWarnings("serial")
	private class VirtualPropertyGenerator implements ColumnGenerator {

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.ui.Table.ColumnGenerator#generateCell(com.vaadin.ui.Table, java.lang.Object,
		 * java.lang.Object)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Object generateCell(Table source, Object itemId, Object columnId) {
			if (!VirtualProperty.class.isAssignableFrom(columnId.getClass())) {
				throw new IllegalArgumentException("VirtualPropertyGenerator has to be bound to "
						+ "a VirtualProperty column id, got " + columnId.getClass().getName());
			}

			// get virtual property value provider
			final VirtualProperty<?> property = (VirtualProperty<?>) columnId;

			final PropertyValueProvider valueProvider = property.getValueProvider();
			if (valueProvider == null) {
				throw new IllegalArgumentException("Missing PropertyValueProvider for property " + property);
			}

			// get property box
			final PropertyBox propertyBox = (PropertyBox) requireDataSource().get(itemId).orElse(null);

			try {
				return valueProvider.getPropertyValue(propertyBox);
			} catch (Exception e) {
				LOGGER.error("Failed to generate column for property " + property, e);
				throw new RuntimeException(e);
			}

		}

	}

	/**
	 * Default selection listener to convert value change events into {@link SelectionListener}s calls.
	 */
	@SuppressWarnings("serial")
	private final ValueChangeListener tableSelectionChangeListener = new ValueChangeListener() {

		@Override
		public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
			fireSelectionListeners();
		}
	};

}
