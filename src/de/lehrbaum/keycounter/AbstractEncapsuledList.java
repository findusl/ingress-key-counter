package de.lehrbaum.keycounter;

import java.util.AbstractList;
import java.util.List;

/**
 * A list class encapsulating a list and enabling subclasses to react to remove or add operations.
 */
public abstract class AbstractEncapsuledList<E> extends
	AbstractList<E> {
	protected List<E> internalList;
	
	protected DatabaseHandler dh;
	
	protected AbstractEncapsuledList(DatabaseHandler dh,
		List<E> elements) {
		this.dh = dh;
		internalList = elements;
	}
	
	@Override
	public void add(int location, E object) {
		internalList.add(location, object);
		onAdd(object);
	}
	
	@Override
	public E remove(int location) {
		E e = internalList.remove(location);
		onRemove(e);
		return e;
	}
	
	@Override
	public E set(int location, E object) {
		E e = internalList.set(location, object);
		onRemove(e);
		onAdd(object);
		return e;
	}
	
	@Override
	public boolean remove(Object object) {
		boolean b = false;
		try {
			//can't check because is generic
			@SuppressWarnings("unchecked")
			E e = (E) object;
			b = internalList.remove(e);
			onRemove(e);
		} catch (ClassCastException e) {}
		return b;
	}
	
	/**
	 * Called if an item is added to the list.
	 * 
	 * @param item The item added to the List.
	 */
	protected abstract void onAdd(E item);
	
	/**
	 * Called if an item is removed from the list.
	 * 
	 * @param item The item removed from the list.
	 */
	protected abstract void onRemove(E item);
	
	@Override
	public E get(int location) {
		return internalList.get(location);
	}
	
	@Override
	public int size() {
		return internalList.size();
	}
}