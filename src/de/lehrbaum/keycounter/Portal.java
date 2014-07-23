package de.lehrbaum.keycounter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Portal implements Comparable<Portal> {
	private static final String TAG = Portal.class.getCanonicalName();
	
	private long id;
	private String name;
	private short keys;
	private Category cat;
	
	/**
	 * Create a new portal and writes it to the database.
	 * 
	 * @param c The applications context for writing to the database.
	 * @param category The current category.
	 * @param name The name of the portal.
	 */
	public Portal(final Context c, Category cat, final String name) {
		//creates the portal in the database and gets the unique id
		final DatabaseHandler dh = new DatabaseHandler(c);
		final long id = dh.addPortal(cat.getId(), name);
		if (id == -1) {
			Log.d(Portal.TAG, "Could not create portal " + name);
			return;
		}
		//initialize the values of this class
		this.id = id;
		this.name = name;
		keys = 0;
		this.cat = cat;
	}
	
	public Portal(final Cursor c, Category cat) {
		id = c.getLong(0);
		name = c.getString(1);
		keys = c.getShort(2);
		this.cat = cat;
	}
	
	@Override
	public int compareTo(final Portal another) {
		return name.compareTo(another.name);
	}
	
	public void decreaseCount(final Context c) {
		if (keys > 0) {
			keys--;
			keysChanged(c);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Portal) {
			Portal p = (Portal) o;
			return id == p.id && keys == p.keys && p.name.equals(name);
		}
		return false;
	}

	/**
	 * @return The number of keys for this portal.
	 */
	public short getKeyCount() {
		return keys;
	}
	
	/**
	 * @return The name of this portal.
	 */
	public String getName() {
		return name;
	}
	
	public void increaseCount(final Context c) {
		if (keys < MainFragment.MAX_KEYS) {
			keys++;
			keysChanged(c);
		}
	}
	
	private void keysChanged(final Context c) {
		//write the new key value to the database
		final DatabaseHandler dh = new DatabaseHandler(c);
		dh.updatePortal(id, keys);
	}
	
	public void copyTo(Context c, Category cat) {
		DatabaseHandler dh = new DatabaseHandler(c);
		dh.addPortalToCategory(id, cat.getId());
	}
	
	public void delete(Context c) {
		final DatabaseHandler dh = new DatabaseHandler(c);
		dh.deletePortal(id, cat.getId());
	}
}
