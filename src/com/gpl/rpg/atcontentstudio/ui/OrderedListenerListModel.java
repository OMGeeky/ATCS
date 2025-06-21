package com.gpl.rpg.atcontentstudio.ui;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class OrderedListenerListModel<S, E> implements ListenerListModel<E> {
    protected S source;

    protected abstract List<E> getItems();
    protected abstract void setItems(List<E> items);

    public OrderedListenerListModel(S source) {
        this.source = source;
    }

    public List<ListDataListener> getListeners() {
        return listeners;
    }


    @Override
    public int getSize() {
        if (getItems() == null) return 0;
        return getItems().size();
    }

    @Override
    public E getElementAt(int index) {
        if (getItems() == null) return null;
        return getItems().get(index);
    }

    public void addObject(E item) {addItem(item);}
    public void addItem(E item) {
        if (getItems() == null) {
            setItems(new ArrayList<E>());
        }
        getItems().add(item);
        int index = getItems().indexOf(item);
        notifyListeners( ListDataEvent.INTERVAL_ADDED, index, index);
    }

    public void removeObject(E item) {removeItem(item);}
    public void removeItem(E item) {
        int index = getItems().indexOf(item);
        getItems().remove(item);
        if (getItems().isEmpty()) {
            setItems(null);
        }
        notifyListeners(ListDataEvent.INTERVAL_REMOVED, index, index);
    }


    public void moveUp(E item) {
        moveUpOrDown(item, -1);
    }

    public void moveDown(E item) {
        moveUpOrDown(item, 1);
    }

    private void moveUpOrDown(E item, int direction) {
        int index = getItems().indexOf(item);
        E exchanged = getItems().get(index + direction);
        getItems().set(index, exchanged);
        getItems().set(index + direction, item);
        notifyListeners(ListDataEvent.CONTENTS_CHANGED, index + direction, index);
    }

    public void objectChanged(E item) {itemChanged(item);}
    public void itemChanged(E item) {
        int index = getItems().indexOf(item);
        notifyListeners( ListDataEvent.CONTENTS_CHANGED, index, index);
    }

    private List<ListDataListener> listeners = new CopyOnWriteArrayList<ListDataListener>();
}
