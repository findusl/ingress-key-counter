package de.lehrbaum.keycounter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final String TAG = DatabaseHandler.class.getCanonicalName();
	private static final String NAME = "keyCounter";//database name
	//columns common to all tables
	private static final String ID_COLUMN = "id";//the name of the id colum
	private static final String NAME_COLUMN = "name";
	//tables and their specific columns
	private static final String CAT_TABLE = "categories";
	private static final String PORT_TABLE = "portals";
	private static final String PORT_KEYS = "keys";
	private static final String MAP_TABLE = "mapping";
	private static final String MAP_CAT = "category";
	private static final String MAP_PORT = "portal";
	
	private static final int VERSION = 1;
	
	public DatabaseHandler(final Context context) {
		super(context, NAME, null, VERSION);
	}
	
	/**
	 * Inserts a category to the database.
	 * 
	 * @param name The name of the category
	 * @return The id of the inserted category or -1 if the insertion failed
	 */
	public int addCategory(final String name) {
		final SQLiteDatabase db = getWritableDatabase();
		final ContentValues cv = new ContentValues(1);
		cv.put(NAME_COLUMN, name);
		final int res = (int) db.insert(CAT_TABLE, null, cv);
		Log.d(TAG, "Inserted cat " + name + " row id " + res);
		return res;
	}
	
	private static final String PREFS_NAME = "Portals";
	private static final String PORT_LENGTH = "lengt";
	
	public static void convertOldPortals(MainActivity act) {
		SharedPreferences settings = act.getSharedPreferences(
			PREFS_NAME, 0);
		//the number of portals
		int size = settings.getInt(PORT_LENGTH, 0);
		if (size == 0)
			return;
		DatabaseHandler dh = new DatabaseHandler(act);
		SQLiteDatabase db = dh.getWritableDatabase();
		for (int i = 0; i < size; i++) {
			//loading each portal
			String name = settings.getString(i + "_name", "No Name");
			int keys = settings.getInt(i + "_count", 0);
			ContentValues cv = new ContentValues(2);
			cv.put(NAME_COLUMN, name);
			cv.put(PORT_KEYS, keys);
			db.insert(PORT_TABLE, null, cv);
		}
		Editor edit = settings.edit();
		edit.remove(PORT_LENGTH);
		edit.commit();
	}

	/**
	 * Inserts a new portal to the database.
	 * 
	 * @param cat The id of the category to insert the portal in.
	 * @param name The name of the portal.
	 * @return The id of the inserted portal or -1 if the insertion failed.
	 */
	public long addPortal(final int cat, final String name) {
		final SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues(1);
		cv.put(NAME_COLUMN, name);
		final long id = (int) db.insert(PORT_TABLE, null, cv);
		Log.d(TAG, "Inserted portal " + name + " row id " + id);
		if (id == -1)
			return id;//unsuccessful
		cv = new ContentValues(2);
		cv.put(MAP_CAT, cat);
		cv.put(MAP_PORT, id);
		final long res = (int) db.insert(MAP_TABLE, null, cv);
		Log.d(TAG,
			"Inserted map entry " + res + " values: " + cv.toString());
		return id;
	}
	
	public void deletePortal(long id) {
		final SQLiteDatabase db = getWritableDatabase();
		int res = db.delete(PORT_TABLE, ID_COLUMN
			+ " == " + id, null);
		Log.d(TAG, "port deleted " + res + " lines");
	}
	
	public void deleteCategory(long id) {
		final SQLiteDatabase db = getWritableDatabase();
		int res = db.delete(CAT_TABLE, ID_COLUMN
			+ " == " + id, null);
		Log.d(TAG, "cat deleted " + res + " lines");
	}

	public SparseArray<String> getCategories() {
		final SQLiteDatabase db = getReadableDatabase();
		//the names of all categories ordered by the id in asc order.
		final Cursor c = db.query(CAT_TABLE, new String[] {
				ID_COLUMN, NAME_COLUMN }, null,
			null, null, null, null);
		c.moveToFirst();
		final int count = c.getCount();
		final SparseArray<String> result = new SparseArray<String>(count);
		for (int i = 0; i < count; i++) {
			final int key = c.getInt(0);
			final String name = c.getString(1);
			result.put(key, name);
			c.moveToNext();
		}
		Log.d(TAG, "Read categories " + count);
		return result;
	}
	
	/**
	 * Finds all portals associated with this category.
	 * 
	 * @param cat The current category.
	 * @return A list of all portals associated with this category.
	 */
	public List<Portal> getPortals(final int cat) {
		final SQLiteDatabase db = getReadableDatabase();
		//all portals that belong to that category
		final Cursor c = db.rawQuery("select * from " + PORT_TABLE
			+ " where exists (select * from " + MAP_TABLE + " where " + ID_COLUMN
			+ " == " + MAP_PORT + " and " + MAP_CAT + " == " + cat + ") order by "
			+ NAME_COLUMN + "", null);
		c.moveToFirst();
		final int count = c.getCount();
		final ArrayList<Portal> result = new ArrayList<Portal>(count);
		for (int i = 0; i < count; i++) {
			result.add(new Portal(c));
			c.moveToNext();
		}
		Log.d(TAG, "Read portals for cat " + cat + " found "
			+ count);
		return result;
	}
	
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + CAT_TABLE + " ("
			+ ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ NAME_COLUMN
			+ " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE);");
		db.execSQL("CREATE TABLE " + PORT_TABLE + " ("
			+ ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ NAME_COLUMN + " TEXT NOT NULL, "
			+ PORT_KEYS + " INTEGER DEFAULT 0);");
		db.execSQL("CREATE TABLE " + MAP_TABLE + " ("
			+ MAP_CAT
			+ " INTEGER REFERENCES categories(id) ON DELETE CASCADE, "
			+ MAP_PORT
			+ " INTEGER REFERENCES portals(id) ON DELETE CASCADE, "
			+ "PRIMARY KEY(" + MAP_CAT + ", "
			+ MAP_PORT + "));");
		ContentValues cv = new ContentValues(2);
		cv.put(ID_COLUMN, 0);
		cv.put(NAME_COLUMN, "DEFAULT");
		db.insert(CAT_TABLE, null, cv);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		db.execSQL("PRAGMA foreign_keys = ON;"); 
	}
	
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldV, final int newV) {
		// no older version yet
	}
	
	/**
	 * Updates the key count of the portal with this id.
	 * 
	 * @param id The id of the portal.
	 * @param keys The new key count.
	 */
	public void updatePortal(final long id, final short keys) {
		final SQLiteDatabase db = getWritableDatabase();
		final ContentValues cv = new ContentValues(1);
		cv.put(PORT_KEYS, keys);
		db.update(PORT_TABLE, cv, ID_COLUMN
			+ " == " + id, null);
	}
	
	public static void deleteSelf(Context c) {
		c.deleteDatabase(NAME);
	}
}