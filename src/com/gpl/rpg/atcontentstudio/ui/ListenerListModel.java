package com.gpl.rpg.atcontentstudio.ui;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.List;

public interface ListenerListModel<E> extends ListModel<E> {
    List<ListDataListener> getListeners();

    default void notifyListeners(int event, int index0, int index1) {
        notifyListeners(this, event, index0, index1);
    }

    default void notifyListeners(Object source, int event, int index0, int index1) {
        for (ListDataListener l : getListeners()) {
            l.intervalRemoved(new ListDataEvent(source, event, index0, index1));
        }
    }

    default void addListDataListener(ListDataListener l) {
        getListeners().add(l);
    }

    default void removeListDataListener(ListDataListener l) {
        getListeners().remove(l);
    }

    default void fireListChanged() {
        notifyListeners(ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1);
    }
}
