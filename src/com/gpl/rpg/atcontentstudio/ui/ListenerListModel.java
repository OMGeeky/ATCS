package com.gpl.rpg.atcontentstudio.ui;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.List;

public interface ListenerListModel<E> extends ListModel<E> {
    List<ListDataListener> getListeners();

    default void notifyListeners(ChangeType event, int index0, int index1) {
        notifyListeners(this, event, index0, index1);
    }

    default void notifyListeners(Object source, ChangeType event, int index0, int index1) {
        int eventCode = switch (event) {
            case CHANGED -> ListDataEvent.CONTENTS_CHANGED;
            case ADDED -> ListDataEvent.INTERVAL_ADDED;
            case REMOVED -> ListDataEvent.INTERVAL_REMOVED;
        };

        for (ListDataListener l : getListeners()) {
            ListDataEvent e = new ListDataEvent(source, eventCode, index0, index1);
            switch (event) {
                case CHANGED -> l.contentsChanged(e);
                case ADDED -> l.intervalAdded(e);
                case REMOVED -> l.intervalRemoved(e);
            }
        }
    }

    default void addListDataListener(ListDataListener l) {
        getListeners().add(l);
    }

    default void removeListDataListener(ListDataListener l) {
        getListeners().remove(l);
    }

    default void fireListChanged() {
        notifyListeners(this, ChangeType.CHANGED, 0, getSize() - 1);
    }

    enum ChangeType {
        CHANGED,
        ADDED,
        REMOVED,
    }
}
