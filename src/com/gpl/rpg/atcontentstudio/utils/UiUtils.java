package com.gpl.rpg.atcontentstudio.utils;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.gamedata.Requirement;
import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.CustomListModel;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.jidesoft.swing.JideBoxLayout;

import javax.swing.*;
import java.awt.event.*;
import java.util.function.Supplier;

public class UiUtils {
    public static class CollapsibleItemListCreation<E> {
        public CollapsiblePanel collapsiblePanel;
        public JList<E> list;
    }

    public static <S, E, M extends CustomListModel<S, E>> CollapsibleItemListCreation<E> getCollapsibleItemList(FieldUpdateListener listener,
                                                                                                  M itemsListModel,
                                                                                                  BasicLambda selectedItemReset,
                                                                                                  BasicLambdaWithArg<E> setSelectedItem,
                                                                                                  BasicLambdaWithReturn<E> selectedItem,
                                                                                                  BasicLambdaWithArg<E> valueChanged,
                                                                                                  BasicLambdaWithArg<JPanel> updateEditorPane,
                                                                                                  boolean writable,
                                                                                                  Supplier<E> tempSupplier,
                                                                                                  DefaultListCellRenderer cellRenderer,
                                                                                                  String title,
                                                                                                  boolean withMoveButtons) {
        CollapsiblePanel itemsPane = new CollapsiblePanel(title);
        itemsPane.setLayout(new JideBoxLayout(itemsPane, JideBoxLayout.PAGE_AXIS));
        final JList<E> itemsList = new JList<>(itemsListModel);
        itemsList.setCellRenderer(cellRenderer);
        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsPane.add(new JScrollPane(itemsList), JideBoxLayout.FIX);
        final JPanel editorPane = new JPanel();
        final JButton createBtn = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
        final JButton deleteBtn = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
        final JButton moveUpBtn = new JButton(new ImageIcon(DefaultIcons.getArrowUpIcon()));
        final JButton moveDownBtn = new JButton(new ImageIcon(DefaultIcons.getArrowDownIcon()));
        deleteBtn.setEnabled(false);
        moveUpBtn.setEnabled(false);
        moveDownBtn.setEnabled(false);
        itemsList.addListSelectionListener(e -> {
            E selectedValue = itemsList.getSelectedValue();
            valueChanged.doIt(selectedValue);
            setSelectedItem.doIt(selectedValue);
            if (selectedValue == null) {
                deleteBtn.setEnabled(false);
                if (withMoveButtons) {
                    moveUpBtn.setEnabled(false);
                    moveDownBtn.setEnabled(false);
                }
            } else {
                deleteBtn.setEnabled(true);
                if (withMoveButtons) {
                    moveUpBtn.setEnabled(itemsList.getSelectedIndex() > 0);
                    moveDownBtn.setEnabled(itemsList.getSelectedIndex() < (itemsListModel.getSize() - 1));
                }
            }
            updateEditorPane.doIt(editorPane);
        });
        if (writable) {
            JPanel listButtonsPane = new JPanel();
            listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));

            createBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    E tempItem = tempSupplier.get();
                    itemsListModel.addItem(tempItem);
                    itemsList.setSelectedValue(tempItem, true);
                    listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                }
            });
            listButtonsPane.add(createBtn, JideBoxLayout.FIX);

            deleteBtn.addActionListener(e -> {
                if (selectedItem.doIt() != null) {
                    itemsListModel.removeItem(selectedItem.doIt());
                    selectedItemReset.doIt();
                    itemsList.clearSelection();
                    listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                }
            });
            listButtonsPane.add(deleteBtn, JideBoxLayout.FIX);
            if(withMoveButtons) {
                moveUpBtn.addActionListener(e -> {
                    if (selectedItem.doIt() != null) {
                        itemsListModel.moveUp(selectedItem.doIt());
                        itemsList.setSelectedValue(selectedItem.doIt(), true);
                        listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                    }
                });
                listButtonsPane.add(moveUpBtn, JideBoxLayout.FIX);

                moveDownBtn.addActionListener(e -> {
                    if (selectedItem.doIt() != null) {
                        itemsListModel.moveDown(selectedItem.doIt());
                        itemsList.setSelectedValue(selectedItem.doIt(), true);
                        listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                    }
                });
                listButtonsPane.add(moveDownBtn, JideBoxLayout.FIX);
            }
            listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
            itemsPane.add(listButtonsPane, JideBoxLayout.FIX);
        }
        //TODO: add double click to navigate to the item in the editor pane.
        // TODO: figure out what ID is needed here
//        itemsList.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() == 2) {
//                    if (itemsList.getSelectedValue() != null && ((E)itemsList.getSelectedValue()).required_obj != null) {
//                        ATContentStudio.frame.openEditor(((E)itemsList.getSelectedValue()).required_obj);
//                        ATContentStudio.frame.selectInTree(((E)itemsList.getSelectedValue()).required_obj);
//                    }
//                }
//            }
//        });
//        itemsList.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyReleased(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    ATContentStudio.frame.openEditor(((E)itemsList.getSelectedValue()).required_obj);
//                    ATContentStudio.frame.selectInTree(((E)itemsList.getSelectedValue()).required_obj);
//                }
//            }
//        });

        editorPane.setLayout(new JideBoxLayout(editorPane, JideBoxLayout.PAGE_AXIS));
        itemsPane.add(editorPane, JideBoxLayout.FIX);

        return new CollapsibleItemListCreation<E>() {
            {
                collapsiblePanel = itemsPane;
                list = itemsList;}
        };
    }

}