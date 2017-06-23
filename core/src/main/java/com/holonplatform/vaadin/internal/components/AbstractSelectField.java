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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ItemSetComponent;
import com.holonplatform.vaadin.components.Selectable;
import com.holonplatform.vaadin.components.builders.BaseSelectFieldBuilder.RenderingMode;
import com.holonplatform.vaadin.data.ItemDataSource;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;

/**
 * Abstract select {@link Field} implementation.
 * 
 * @param <T> Actual field type
 * @param <S> Internal select type
 * @param <ITEM> Selection items type
 */
public abstract class AbstractSelectField<T, S, ITEM> extends AbstractCustomField<T, AbstractSelect>
		implements Selectable<S>, ItemSetComponent, Container.Viewer {

	private static final long serialVersionUID = -2069614658878818456L;

	/**
	 * Extract to obtain selection item data from container item
	 * @param <ITEM> Selection item data type
	 */
	@FunctionalInterface
	interface ItemDataExtractor<ITEM> {

		/**
		 * Get selection item data from container item
		 * @param configuration Data source configuration
		 * @param itemId Item id
		 * @param item Container item
		 * @return Itemd data
		 */
		ITEM extractItemData(Configuration<?> configuration, Object itemId, Item item);

	}

	/**
	 * Item data extractor
	 */
	private ItemDataExtractor<ITEM> itemDataExtractor;

	/**
	 * Rendering mode
	 */
	private final RenderingMode renderingMode;

	/**
	 * Selection listeners
	 */
	private final List<SelectionListener<S>> selectionListeners = new LinkedList<>();

	/**
	 * Item caption generator
	 */
	private ItemCaptionGenerator<ITEM> itemCaptionGenerator;

	/**
	 * Item icon generator
	 */
	private ItemIconGenerator<ITEM> itemIconGenerator;

	/**
	 * Explicit item captions
	 */
	protected final Map<ITEM, Localizable> explicitItemCaptions = new HashMap<>(8);

	/**
	 * Explicit item icons
	 */
	protected final Map<ITEM, Resource> explicitItemIcons = new HashMap<>(8);

	/**
	 * Constructor
	 * @param type Select field type
	 * @param renderingMode UI rendering mode
	 */
	public AbstractSelectField(Class<? extends T> type, RenderingMode renderingMode) {
		super(type, false);
		this.renderingMode = renderingMode;

		addStyleName("h-select", false);

		init();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("serial")
	protected AbstractSelect buildInternalField(Class<? extends T> type) {

		boolean multiSelect = getSelectionMode() == SelectionMode.MULTI;

		RenderingMode mode = getRenderingMode();
		if (mode == null) {
			mode = multiSelect ? RenderingMode.OPTIONS : RenderingMode.SELECT;
		}
		if (multiSelect && mode == RenderingMode.NATIVE_SELECT) {
			mode = RenderingMode.SELECT;
		}

		AbstractSelect field = null;

		switch (mode) {
		case NATIVE_SELECT: {
			field = new NativeSelect() {

				@Override
				public String getItemCaption(Object itemId) {
					return generateItemCaption(extractItemData(itemId, getItem(itemId)));
				}

				@Override
				public Resource getItemIcon(Object itemId) {
					return generateItemIcon(extractItemData(itemId, getItem(itemId)));
				}

			};
		}
			break;
		case OPTIONS: {
			field = new OptionGroup() {

				@Override
				public String getItemCaption(Object itemId) {
					return generateItemCaption(extractItemData(itemId, getItem(itemId)));
				}

				@Override
				public Resource getItemIcon(Object itemId) {
					return generateItemIcon(extractItemData(itemId, getItem(itemId)));
				}

			};
			((OptionGroup) field).setMultiSelect(multiSelect);
		}
			break;
		case SELECT:
		default: {
			if (multiSelect) {
				field = new ListSelect() {

					@Override
					public String getItemCaption(Object itemId) {
						return generateItemCaption(extractItemData(itemId, getItem(itemId)));
					}

					@Override
					public Resource getItemIcon(Object itemId) {
						return generateItemIcon(extractItemData(itemId, getItem(itemId)));
					}

				};
			} else {
				field = new ComboBox() {

					@Override
					public String getItemCaption(Object itemId) {
						return generateItemCaption(extractItemData(itemId, getItem(itemId)));
					}

					@Override
					public Resource getItemIcon(Object itemId) {
						return generateItemIcon(extractItemData(itemId, getItem(itemId)));
					}

				};
			}
		}
			break;
		}

		// selection notifier
		field.addValueChangeListener(e -> fireSelectionListeners());

		return field;
	}

	/**
	 * Get the item data extractor
	 * @return Item data extractor
	 */
	protected Optional<ItemDataExtractor<ITEM>> getItemDataExtractor() {
		return Optional.ofNullable(itemDataExtractor);
	}

	/**
	 * Set the item data extractor
	 * @param itemDataExtractor the item data extractor to set
	 */
	protected void setItemDataExtractor(ItemDataExtractor<ITEM> itemDataExtractor) {
		this.itemDataExtractor = itemDataExtractor;
	}

	/**
	 * Extract selection item data from container item
	 * @param itemId Item id
	 * @param item Container item
	 * @return Selection item data
	 */
	protected ITEM extractItemData(Object itemId, Item item) {
		return getItemDataExtractor().map(e -> e.extractItemData(getDataSourceConfiguration(), itemId, item))
				.orElseThrow(() -> new IllegalStateException("Missing ItemDataExtractor"));
	}
	
	/**
	 * Get data source configuration
	 * @return data source configuration
	 */
	protected Configuration<?> getDataSourceConfiguration() {
		if (getContainerDataSource() instanceof ItemDataSource) {
			return ((ItemDataSource<?,?>)getContainerDataSource()).getConfiguration();
		}
		return null;
	}

	/**
	 * Get the select rendering mode
	 * @return Rendering mode
	 */
	protected RenderingMode getRenderingMode() {
		return renderingMode;
	}

	/**
	 * Get the item caption generator
	 * @return the ItemCaptionGenerator
	 */
	public Optional<ItemCaptionGenerator<ITEM>> getItemCaptionGenerator() {
		return Optional.ofNullable(itemCaptionGenerator);
	}

	/**
	 * Set the item caption generator
	 * @param itemCaptionGenerator the ItemCaptionGenerator to set
	 */
	public void setItemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
		this.itemCaptionGenerator = itemCaptionGenerator;
	}

	/**
	 * Get the item icon generator
	 * @return the ItemIconGenerator
	 */
	public Optional<ItemIconGenerator<ITEM>> getItemIconGenerator() {
		return Optional.ofNullable(itemIconGenerator);
	}

	/**
	 * Set the item icon generator
	 * @param itemIconGenerator the ItemIconGenerator to set
	 */
	public void setItemIconGenerator(ItemIconGenerator<ITEM> itemIconGenerator) {
		this.itemIconGenerator = itemIconGenerator;
	}

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for
	 * @param caption Caption to set (not null)
	 */
	public void setItemCaption(ITEM item, Localizable caption) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		if (caption != null) {
			explicitItemCaptions.put(item, caption);
		} else {
			explicitItemCaptions.remove(item);
		}
	}

	/**
	 * Set an explicit icon for given item.
	 * @param item Item to set the caption for
	 * @param icon Icon to set (not null)
	 */
	public void setItemIcon(ITEM item, Resource icon) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		if (icon != null) {
			explicitItemIcons.put(item, icon);
		} else {
			explicitItemIcons.remove(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Viewer#setContainerDataSource(com.vaadin.data.Container)
	 */
	@Override
	public void setContainerDataSource(Container newDataSource) {
		getInternalField().setContainerDataSource(newDataSource);
		// by default, disable ComboBox scrolling to selected item, which can be expensive in many large lazy loading
		// containers
		if (getInternalField() instanceof ComboBox) {
			((ComboBox) getInternalField()).setScrollToSelectedItem(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container.Viewer#getContainerDataSource()
	 */
	@Override
	public Container getContainerDataSource() {
		return getInternalField().getContainerDataSource();
	}

	/**
	 * Gets the number of visible Items in the Container.
	 * @return number of Items in the Container
	 */
	@Override
	public int size() {
		return getInternalField().size();
	}

	/**
	 * Gets the item Id collection from the container.
	 * @return the Collection of item ids.
	 */
	@SuppressWarnings("unchecked")
	public Collection<S> getItemIds() {
		return (Collection<S>) getInternalField().getItemIds();
	}

	/**
	 * Create a new selection item.
	 * @param item the item to add
	 */
	public void addItem(S item) throws UnsupportedOperationException {
		getInternalField().addItem(item);
	}

	/**
	 * Removes the Item identified by <code>ItemId</code> from the Container.
	 * @param itemId ID of the Item to remove
	 * @return <code>true</code> if the operation succeeded, <code>false</code> if not
	 * @throws UnsupportedOperationException if the container does not support removing individual items
	 */
	public boolean removeItem(S itemId) throws UnsupportedOperationException {
		return getInternalField().removeItem(itemId);
	}

	/**
	 * Removes all items from the container.
	 * @return True if the operation succeeded.
	 */
	public boolean removeAllItems() throws UnsupportedOperationException {
		return getInternalField().removeAllItems();
	}

	/**
	 * Adds an handler for new items
	 * @param newItemHandler New items handler
	 */
	public void setNewItemHandler(NewItemHandler newItemHandler) {
		getInternalField().setNewItemHandler(newItemHandler);
	}

	/**
	 * Gets the handler for new items
	 * @return the new items handler
	 */
	public NewItemHandler getNewItemHandler() {
		return getInternalField().getNewItemHandler();
	}

	/**
	 * Does the select allow adding new options by the user. If true, the new options can be added to the Container. The
	 * text entered by the user is used as id. Note that data-source must allow adding new items.
	 * @return True if additions are allowed.
	 */
	public boolean isNewItemsAllowed() {
		return getInternalField().isNewItemsAllowed();
	}

	/**
	 * Enables or disables possibility to add new options by the user.
	 * @param allowNewOptions the New value of property allowNewOptions.
	 */
	public void setNewItemsAllowed(boolean allowNewOptions) {
		getInternalField().setNewItemsAllowed(allowNewOptions);
	}

	/**
	 * Allow or disallow empty selection by the user. If the select is in single-select mode, you can make an item
	 * represent the empty selection by calling <code>setNullSelectionItemId()</code>. This way you can for instance set
	 * an icon and caption for the null selection item.
	 * @param nullSelectionAllowed whether or not to allow empty selection
	 */
	public void setNullSelectionAllowed(boolean nullSelectionAllowed) {
		getInternalField().setNullSelectionAllowed(nullSelectionAllowed);
	}

	/**
	 * Checks if null empty selection is allowed by the user.
	 * @return whether or not empty selection is allowed
	 */
	public boolean isNullSelectionAllowed() {
		return getInternalField().isNullSelectionAllowed();
	}

	/**
	 * Returns the item id that represents null value of this select in single select mode.
	 * <p>
	 * Data interface does not support nulls as item ids. Selecting the item identified by this id is the same as
	 * selecting no items at all. This setting only affects the single select mode.
	 * </p>
	 * @return the Object Null value item id.
	 */
	@SuppressWarnings("unchecked")
	public S getNullSelectionItemId() {
		return (S) getInternalField().getNullSelectionItemId();
	}

	/**
	 * Sets the item id that represents null value of this select.
	 * <p>
	 * Data interface does not support nulls as item ids. Selecting the item identified by this id is the same as
	 * selecting no items at all. This setting only affects the single select mode.
	 * </p>
	 * @param nullSelectionItemId the nullSelectionItemId to set.
	 */
	public void setNullSelectionItemId(S nullSelectionItemId) {
		getInternalField().setNullSelectionItemId(nullSelectionItemId);
	}

	/**
	 * Checks that the current selection is valid, i.e. the selected item ids exist in the container. Updates the
	 * selection if one or several selected item ids are no longer available in the container.
	 */
	public void sanitizeSelection() {
		getInternalField().sanitizeSelection();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemSetComponent#refresh()
	 */
	@Override
	public void refresh() {
		Container dataSource = getContainerDataSource();
		if (dataSource instanceof ItemDataSource) {
			((ItemDataSource<?, ?>) dataSource).refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#addSelectionListener(com.holonplatform.vaadin.components.
	 * Selectable.SelectionListener)
	 */
	@Override
	public void addSelectionListener(SelectionListener<S> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		selectionListeners.add(selectionListener);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.Selectable#removeSelectionListener(com.holonplatform.vaadin.components.
	 * Selectable.SelectionListener)
	 */
	@Override
	public void removeSelectionListener(SelectionListener<S> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		selectionListeners.remove(selectionListener);
	}

	/**
	 * Triggers registered {@link SelectionListener}s.
	 */
	protected void fireSelectionListeners() {
		selectionListeners.forEach(l -> l.onSelectionChange(this));
	}

	/**
	 * Generate the select item icon for given <code>item</code>.
	 * @param item Item to generate the icon for
	 * @return Item icon (may be null)
	 */
	protected Resource generateItemIcon(ITEM item) {
		if (item != null) {
			return getItemIconGenerator().map(g -> g.getItemIcon(item)).orElse(getDefaultItemIcon(item));
		}
		return null;
	}

	/**
	 * Get the default select item icon for given <code>item</code>.
	 * @param item Item
	 * @return Default item icon
	 */
	protected Resource getDefaultItemIcon(ITEM item) {
		if (item != null) {
			return explicitItemIcons.get(item);
		}
		return null;
	}

	/**
	 * Generate the select item caption for given <code>item</code>.
	 * @param item Item to generate the caption for
	 * @return Item caption (not null)
	 */
	protected String generateItemCaption(ITEM item) {
		if (item != null) {
			return getItemCaptionGenerator().map(g -> g.getItemCaption(item)).orElse(getDefaultItemCaption(item));
		}
		return "";
	}

	/**
	 * Get the default select item caption for given <code>item</code>.
	 * @param item Item
	 * @return Default item caption
	 */
	protected String getDefaultItemCaption(ITEM item) {
		if (item != null) {
			// check explicit caption
			Localizable caption = explicitItemCaptions.get(item);
			if (caption != null) {
				return LocalizationContext.translate(caption, true);
			}
			// check Localizable
			Localizable lv = null;
			if (Localizable.class.isAssignableFrom(item.getClass())) {
				lv = (Localizable) item;
			} else {
				// check Caption annotation on enums
				if (item.getClass().isEnum()) {
					Enum<?> enm = (Enum<?>) item;
					try {
						final java.lang.reflect.Field fld = item.getClass().getField(enm.name());
						if (fld.isAnnotationPresent(Caption.class)) {
							lv = Localizable.builder().message(fld.getAnnotation(Caption.class).value()).messageCode(
									AnnotationUtils.getStringValue(fld.getAnnotation(Caption.class).messageCode()))
									.build();
						}
					} catch (@SuppressWarnings("unused") Exception e) {
						// ignore
					}
				}
			}
			if (lv != null) {
				return LocalizationContext.translate(lv, true);
			}
			// ID toString
			return item.toString();
		}
		return "";
	}

}
