package com.gpl.rpg.atcontentstudio.ui;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class OrderedListenerListModel<S, E> implements ListenerCollectionModel<E> {
    protected S source;

    protected abstract List<E> getItems();
    protected abstract void setItems(List<E> items);

    public OrderedListenerListModel(S source) {
        this.source = source;
    }

    @Override
    public Collection<E> getElements(){
        return getItems();
    }

    @Override
    public E getElementAt(int index) {
        if (getItems() == null) return null;
        return getItems().get(index);
    }

    public E setElementAt(int index, E value) {
        if (getItems() == null) return null;
        return getItems().set(index, value);
    }

    public void addObject(E item) {addItem(item);}
    public void addItem(E item) {
        if (getItems() == null) {
            setItems(new ArrayList<E>());
        }
        getItems().add(item);
        int index = getItems().indexOf(item);
        notifyListeners(ListDataEvent.INTERVAL_ADDED, index, index);
    }

    public void removeObject(E item) {removeItem(item);}
    public void removeItem(E item) {
        int index = getItems().indexOf(item);
        getItems().remove(item);
        if (getSize() == 0) {
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
        E exchanged = getElementAt(index + direction);
        setElementAt(index, exchanged);
        setElementAt(index + direction, item);
        notifyListeners(ListDataEvent.CONTENTS_CHANGED, index + direction, index);
    }

    public void objectChanged(E item) {itemChanged(item);}
    public void itemChanged(E item) {
        int index = getItems().indexOf(item);
        notifyListeners( ListDataEvent.CONTENTS_CHANGED, index, index);
    }

    private List<ListDataListener> listeners = new CopyOnWriteArrayList<ListDataListener>();
    public List<ListDataListener> getListeners() {
        return listeners;
    }
}
