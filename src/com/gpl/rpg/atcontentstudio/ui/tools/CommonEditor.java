package com.gpl.rpg.atcontentstudio.ui.tools;

import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.utils.lambda.CallWithReturn;
import com.gpl.rpg.atcontentstudio.utils.lambda.CallWithSingleArg;
import com.gpl.rpg.atcontentstudio.utils.lambda.CallWithThreeArgs;
import com.jidesoft.swing.JideBoxLayout;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CommonEditor {


    public static <E, S> CollapsiblePanel createListPanel(String title,
                                                          ListCellRenderer<? super E> cellRenderer,
                                                          AtListModel<E, S> listModel,
                                                          boolean isCollapsed,
                                                          boolean writable,
                                                          boolean moveUpDownEnabled,
                                                          CallWithSingleArg<E> selectedValueSetter,
                                                          CallWithReturn<E> selectedValueGetter,
                                                          CallWithThreeArgs<JPanel, E, FieldUpdateListener> updateRepliesEditorPane,
                                                          FieldUpdateListener listener,
                                                          CallWithReturn<E> createNew) {
        CollapsiblePanel replies = new CollapsiblePanel(title);
        replies.setLayout(new JideBoxLayout(replies, JideBoxLayout.PAGE_AXIS));
        JList<E> repliesList = new JList<>(listModel);
        repliesList.setCellRenderer(cellRenderer);
        repliesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        replies.add(new JScrollPane(repliesList), JideBoxLayout.FIX);
        final JPanel repliesEditorPane = new JPanel();
        final JButton createReply = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
        final JButton deleteReply = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
        final JButton moveReplyUp = new JButton(new ImageIcon(DefaultIcons.getArrowUpIcon()));
        final JButton moveReplyDown = new JButton(new ImageIcon(DefaultIcons.getArrowDownIcon()));
        deleteReply.setEnabled(false);
        moveReplyUp.setEnabled(false);
        moveReplyDown.setEnabled(false);
        repliesList.addListSelectionListener(e -> {
            E selectedReply = repliesList.getSelectedValue();
            selectedValueSetter.call(selectedReply);
            if (selectedReply != null) {
                deleteReply.setEnabled(true);
                if(moveUpDownEnabled){
                    moveReplyUp.setEnabled(repliesList.getSelectedIndex() > 0);
                    moveReplyDown.setEnabled(repliesList.getSelectedIndex() < (listModel.getSize() - 1));
                }
            } else {
                deleteReply.setEnabled(false);
                if(moveUpDownEnabled){
                    moveReplyUp.setEnabled(false);
                    moveReplyDown.setEnabled(false);
                }
            }
            updateRepliesEditorPane.call(repliesEditorPane, selectedReply, listener);
        });
        if (writable) {
            JPanel listButtonsPane = new JPanel();
            listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
            createReply.addActionListener(e -> {
                E created = createNew.call();
                listModel.addItem(created);
                repliesList.setSelectedValue(created, true);
                listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
            });
            deleteReply.addActionListener(e -> {
                E selected = selectedValueGetter.call();
                if (selected != null) {
                    listModel.removeItem(selected);
                    selected = null;
                    selectedValueSetter.call(selected);
                    repliesList.clearSelection();
                    listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                }
            });
            if(moveUpDownEnabled){

            moveReplyUp.addActionListener(e -> {
                E selected = selectedValueGetter.call();
                if (selected != null) {
                    listModel.moveUp(selected);
                    repliesList.setSelectedValue(selected, true);
                    listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                }
            });
            moveReplyDown.addActionListener(e -> {
                E selected = selectedValueGetter.call();
                if (selected != null) {
                    listModel.moveDown(selected);
                    repliesList.setSelectedValue(selected, true);
                    listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                }
            });
            }
            listButtonsPane.add(createReply, JideBoxLayout.FIX);
            listButtonsPane.add(deleteReply, JideBoxLayout.FIX);
            if(moveUpDownEnabled){
                listButtonsPane.add(moveReplyUp, JideBoxLayout.FIX);
                listButtonsPane.add(moveReplyDown, JideBoxLayout.FIX);
            }
            listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
            replies.add(listButtonsPane, JideBoxLayout.FIX);
        }
        if (isCollapsed) {
            replies.collapse();
        }
        repliesEditorPane.setLayout(new JideBoxLayout(repliesEditorPane, JideBoxLayout.PAGE_AXIS));
        replies.add(repliesEditorPane, JideBoxLayout.FIX);
        return replies;
    }


    public static String wordWrap(String in, int length) {
        if (in == null) return null;
        final String newline = "\n";
        //:: Trim
        while (!in.isEmpty() && (in.charAt(0) == '\t' || in.charAt(0) == ' ')) in = in.substring(1);
        //:: If Small Enough Already, Return Original
        if (in.length() < length) return in;
        //:: If Next length Contains Newline, Split There
        if (in.substring(0, length).contains(newline))
            return in.substring(0, in.indexOf(newline)).trim() + newline + wordWrap(in.substring(in.indexOf("\n") + 1), length);
        //:: Otherwise, Split Along Nearest Previous Space/Tab/Dash
        int spaceIndex = Math.max(Math.max(in.lastIndexOf(" ", length), in.lastIndexOf("\t", length)), in.lastIndexOf("-", length));
        //:: If No Nearest Space, Split At length
        if (spaceIndex == -1) spaceIndex = length;
        //:: Split
        return in.substring(0, spaceIndex).trim() + newline + wordWrap(in.substring(spaceIndex), length);
    }


    public abstract static class AtListModel<E, S> implements ListModel<E> {

        protected S source;

        protected abstract List<E> getInner();

        protected abstract void setInner(List<E> value);

        public AtListModel(S source) {
            this.source = source;
        }


        @Override
        public int getSize() {
            if (getInner() == null) return 0;
            return getInner().size();
        }

        @Override
        public E getElementAt(int index) {
            if (getInner() == null) return null;
            return getInner().get(index);
        }

        public void addItem(E item) {
            if (getInner() == null) {
                setInner(new ArrayList<>());
            }
            getInner().add(item);
            int index = getInner().indexOf(item);
            for (ListDataListener l : listeners) {
                l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
            }
        }

        public void removeItem(E item) {
            int index = getInner().indexOf(item);
            getInner().remove(item);
            if (getInner().isEmpty()) {
                setInner(null);
            }
            for (ListDataListener l : listeners) {
                l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
            }
        }

        public void itemChanged(E item) {
            int index = getInner().indexOf(item);
            for (ListDataListener l : listeners) {
                l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
            }
        }

        public void moveUp(E item) {
            int index = getInner().indexOf(item);
            E exchanged = getInner().get(index - 1);
            getInner().set(index, exchanged);
            getInner().set(index - 1, item);
            for (ListDataListener l : listeners) {
                l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index - 1, index));
            }
        }

        public void moveDown(E item) {
            int index = getInner().indexOf(item);
            E exchanged = getInner().get(index + 1);
            getInner().set(index, exchanged);
            getInner().set(index + 1, item);
            for (ListDataListener l : listeners) {
                l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index + 1));
            }
        }


        List<ListDataListener> listeners = new CopyOnWriteArrayList<ListDataListener>();

        @Override
        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }
    }
}
