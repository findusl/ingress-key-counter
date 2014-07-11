package de.lehrbaum.keycounter;

public interface OnChangeListener<T> {
	public void onAdd(T item);
	
	public void onDestroy(T item);
}