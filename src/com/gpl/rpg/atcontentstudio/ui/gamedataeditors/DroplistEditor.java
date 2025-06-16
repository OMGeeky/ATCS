package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.gamedata.Droplist;
import com.gpl.rpg.atcontentstudio.model.gamedata.Droplist.DroppedItem;
import com.gpl.rpg.atcontentstudio.model.gamedata.Item;
import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.ui.tools.CommonEditor;
import com.jidesoft.swing.JideBoxLayout;

public class DroplistEditor extends JSONElementEditor {

	private static final long serialVersionUID = 1139455254096811058L;

	private static final String form_view_id = "Form";
	private static final String json_view_id = "JSON";

	private Droplist.DroppedItem selectedItem;

	private JTextField idField;
	private MyComboBox itemCombo;
	private DroppedItemsListModel droppedItemsListModel;
	private JSpinner qtyMinField;
	private JSpinner qtyMaxField;
	private JComponent chanceField;

	public DroplistEditor(Droplist droplist) {
		super(droplist, droplist.getDesc(), droplist.getIcon());
		addEditorTab(form_view_id, getFormView());
		addEditorTab(json_view_id, getJSONView());
	}

	@Override
	public void insertFormViewDataField(JPanel pane) {

		final Droplist droplist = (Droplist)target;
		final FieldUpdateListener listener = new DroplistFieldUpdater();

		createButtonPane(pane, droplist.getProject(), droplist, Droplist.class, Droplist.getImage(), null, listener);

		idField = addTextField(pane, "Droplist ID: ", droplist.id, droplist.writable, listener);

		String title = "Items in this droplist: ";
		DroppedItemsCellRenderer cellRenderer = new DroppedItemsCellRenderer();
		droppedItemsListModel = new DroppedItemsListModel(droplist);
		final boolean moveUpDownEnabled = false;

		CollapsiblePanel itemsPane = CommonEditor.createListPanel(
				title,
				cellRenderer,
				droppedItemsListModel,
				droplist.writable,
				moveUpDownEnabled,
				(e) -> selectedItem = e,
				() -> selectedItem,
				this::updateDroppedItemsEditorPane,
				listener,
				Droplist.DroppedItem::new).panel;

		if (droplist.dropped_items == null || droplist.dropped_items.isEmpty()) {
			itemsPane.collapse();
		}

		pane.add(itemsPane, JideBoxLayout.FIX);

	}

	public void updateDroppedItemsEditorPane(JPanel pane, DroppedItem di, FieldUpdateListener listener) {
		boolean writable = target.writable;
		Project proj = target.getProject();
		pane.removeAll();
		if (itemCombo != null) {
			removeElementListener(itemCombo);
		}
		if (di != null) {
			itemCombo = addItemBox(pane, proj, "Item: ", di.item, writable, listener);
			qtyMinField = addIntegerField(pane, "Quantity min: ", di.quantity_min, false, writable, listener);
			qtyMaxField = addIntegerField(pane, "Quantity max: ", di.quantity_max, false, writable, listener);
			chanceField = addChanceField(pane, "Chance: ", di.chance, "100", writable, listener);//addDoubleField(pane, "Chance: ", di.chance, writable, listener);
		}
		pane.revalidate();
		pane.repaint();
	}

	public static class DroppedItemsListModel extends CommonEditor.AtListModel<Droplist.DroppedItem, Droplist> {

		public DroppedItemsListModel(Droplist droplist) {
			super(droplist);
		}

		@Override
		protected List<Droplist.DroppedItem> getInner() {
			return source.dropped_items;
		}

		@Override
		protected void setInner(List<Droplist.DroppedItem> value) {
			source.dropped_items = value;
		}
	}

	public static class DroppedItemsCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = ((JLabel)c);
				Droplist.DroppedItem di = (Droplist.DroppedItem)value;
				if (di.item != null) {
					label.setIcon(new ImageIcon(di.item.getIcon()));
					label.setText(di.chance+(di.chance != null && di.chance.contains("/") ? "" : "%")+" to get "+di.quantity_min+"-"+di.quantity_max+" "+di.item.getDesc());
				} else if (!isNull(di)) {
					label.setText(di.chance+(di.chance != null && di.chance.contains("/") ? "" : "%")+" to get "+di.quantity_min+"-"+di.quantity_max+" "+di.item_id);
				} else {
					label.setText("New, undefined, dropped item.");
				}
			}
			return c;
		}

		public boolean isNull(Droplist.DroppedItem item) {
			return ((item == null) || (
					item.item == null &&
					item.item_id == null &&
					item.quantity_min == null &&
					item.quantity_max == null &&
					item.chance == null
					)); 
		}
	}


	public class DroplistFieldUpdater implements FieldUpdateListener {
		@Override
		public void valueChanged(JComponent source, Object value) {
			Droplist droplist = ((Droplist)target);
			if (source == idField) {
				//Events caused by cancel an ID edition. Dismiss.
				if (skipNext) {
					skipNext = false;
					return;
				}
				if (target.id.equals(value)) return;

				if (idChanging()) {
					droplist.id = (String) value;
					DroplistEditor.this.name = droplist.getDesc();
					droplist.childrenChanged(new ArrayList<>());
					ATContentStudio.frame.editorChanged(DroplistEditor.this);
				} else {
					cancelIdEdit(idField);
					return;
				}
			} else if (source == itemCombo) {
				if (selectedItem.item != null) {
					selectedItem.item.removeBacklink(droplist);
				}
				selectedItem.item = (Item) value;
				if (selectedItem.item != null) {
					selectedItem.item_id = selectedItem.item.id;
					selectedItem.item.addBacklink(droplist);
				} else {
					selectedItem.item_id = null;
				}
				droppedItemsListModel.itemChanged(selectedItem);
			} else if (source == qtyMinField) {
				selectedItem.quantity_min = (Integer) value;
				droppedItemsListModel.itemChanged(selectedItem);
			} else if (source == qtyMaxField) {
				selectedItem.quantity_max = (Integer) value;
				droppedItemsListModel.itemChanged(selectedItem);
			} else if (source == chanceField) {
				selectedItem.chance = (String) value;
				droppedItemsListModel.itemChanged(selectedItem);
			}

			if (droplist.state != GameDataElement.State.modified) {
				droplist.state = GameDataElement.State.modified;
				DroplistEditor.this.name = droplist.getDesc();
				droplist.childrenChanged(new ArrayList<>());
				ATContentStudio.frame.editorChanged(DroplistEditor.this);
			}
			updateJsonViewText(droplist.toJsonString());
		}
	}
}
