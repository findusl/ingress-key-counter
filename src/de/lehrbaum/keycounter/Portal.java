package de.lehrbaum.keycounter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Portal implements Comparable<Portal> {
	private static final String TAG = Portal.class.getCanonicalName();
	
	private final long id;
	private String name;
	private short keys;
	
	/**
	 * Create a new portal and writes it to the database.
	 * 
	 * @param c The applications context for writing to the database.
	 * @param name The name of the portal.
	 */
	public Portal(final Context c, final String name) {
		//creates the portal in the database and gets the unique id
		final DatabaseHandler dh = new DatabaseHandler(c);
		id = dh.addPortal(name);
		if (id == -1) {
			Log.d(Portal.TAG, "Could not create portal " + name);
			return;
		}
		//initialize the values of this class
		this.name = name;
		keys = 0;
	}
	
	public Portal(final Cursor c) {
		id = c.getLong(0);
		name = c.getString(1);
		keys = c.getShort(2);
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
			return id == p.id;
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
	 * @return The Unique Id of this portal.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @return The name of this portal.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param The new name of the portal.
	 */
	public void setName(Context c, String name) {
		DatabaseHandler dh = new DatabaseHandler(c);
		dh.updatePortal(id, name);
		this.name = name;
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
	
	/**
	 * Used to copy a Portal to another Category. The other Category should currently not be in use!
	 * Otherwise the changes may not directly apply to the GUI.
	 */
	public void copyTo(Context c, Category cat) {
		DatabaseHandler dh = new DatabaseHandler(c);
		dh.addPortalToCategory(id, cat.getId());
	}
}