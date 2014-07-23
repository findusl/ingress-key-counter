package de.lehrbaum.keycounter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Category {
	private static final String TAG = Category.class
		.getCanonicalName();
	
	private int id;
	private String name;
	
	public Category(Context context, String name) {
		DatabaseHandler dh = new DatabaseHandler(context);
		if (id < 0) {
			Log.d(Category.TAG, "Could not add Category: " + name);
			return;//something went wrong
		}
		id = dh.addCategory(name);
		this.name = name;
	}
	
	public Category(Cursor c) {
		id = c.getInt(0);
		name = c.getString(1);
	}
	
	/**
	 * @return the unique id of the category
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the name of the category.
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void delete(Context c) {
		DatabaseHandler dh = new DatabaseHandler(c);
		dh.deleteCategory(id);
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Category) {
			return ((Category) o).id == id;
		}
		return false;
	}
}
