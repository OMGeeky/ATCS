package com.gpl.rpg.atcontentstudio.ui.tools;

import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.utils.lambda.CallWithReturn;
import com.gpl.rpg.atcontentstudio.utils.lambda.CallWithSingleArg;
import com.gpl.rpg.atcontentstudio.utils.lambda.CallWithThreeArgs;
import com.jidesoft.swing.JideBoxLayout;

import javax.swing.*;

public final class CommonEditor {


    public static <E> CollapsiblePanel createListPanel(String title,
                                                       ListCellRenderer<? super E> cellRenderer,
                                                       ListModel<E> listModel,
                                                       boolean isCollapsed,
                                                       boolean writable,
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
            E selectedReply =  repliesList.getSelectedValue();
            selectedValueSetter.call(selectedReply);
            if (selectedReply != null) {
                deleteReply.setEnabled(true);
                moveReplyUp.setEnabled(repliesList.getSelectedIndex() > 0);
                moveReplyDown.setEnabled(repliesList.getSelectedIndex() < (listModel.getSize() - 1));
            } else {
                deleteReply.setEnabled(false);
                moveReplyUp.setEnabled(false);
                moveReplyDown.setEnabled(false);
            }
            updateRepliesEditorPane.call(repliesEditorPane, selectedReply, listener);
        });
        if (writable) {
            JPanel listButtonsPane = new JPanel();
            listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
            createReply.addActionListener(e -> {
                E created = createNew.call();
//                    listModel.addItem(created);//TODO
                repliesList.setSelectedValue(created, true);
                listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
            });
            deleteReply.addActionListener(e -> {
                E selected = selectedValueGetter.call();
                if (selected != null) {
//                        listModel.removeItem(selected);//TODO
                    selected = null;
                    selectedValueSetter.call(selected);
                    repliesList.clearSelection();
                    listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                }
            });
            moveReplyUp.addActionListener(e -> {
                E selected = selectedValueGetter.call();
                if (selected != null) {
//                        listModel.moveUp(selected);//TODO
                    repliesList.setSelectedValue(selected, true);
                    listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                }
            });
            moveReplyDown.addActionListener(e -> {
                E selected = selectedValueGetter.call();
                if (selected != null) {
//                        listModel.moveDown(selected);//TODO
                    repliesList.setSelectedValue(selected, true);
                    listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
                }
            });
            listButtonsPane.add(createReply, JideBoxLayout.FIX);
            listButtonsPane.add(deleteReply, JideBoxLayout.FIX);
            listButtonsPane.add(moveReplyUp, JideBoxLayout.FIX);
            listButtonsPane.add(moveReplyDown, JideBoxLayout.FIX);
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
}
