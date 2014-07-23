package de.lehrbaum.keycounter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final String TAG = DatabaseHandler.class
		.getCanonicalName();
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
	
	private List<Category> cats;//temporarily saving the categories
	
	public DatabaseHandler(final Context context) {
		super(context, DatabaseHandler.NAME, null,
			DatabaseHandler.VERSION);
	}
	
	/**
	 * Inserts a category to the database.
	 * 
	 * @param name The name of the category
	 * @return The id of the inserted category or -1 if the insertion failed
	 */
	public synchronized int addCategory(final String name) {
		final SQLiteDatabase db = getWritableDatabase();
		final ContentValues cv = new ContentValues(1);
		cv.put(DatabaseHandler.NAME_COLUMN, name);
		final int res = (int) db.insert(DatabaseHandler.CAT_TABLE,
			null, cv);
		Log.d(DatabaseHandler.TAG, "Inserted cat " + name + " row id "
			+ res);
		db.close();
		cats = null;
		return res;
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
		cv.put(DatabaseHandler.NAME_COLUMN, name);
		final long id = (int) db.insert(DatabaseHandler.PORT_TABLE,
			null, cv);
		Log.d(DatabaseHandler.TAG, "Inserted portal " + name
			+ " row id " + id);
		if (id == -1)
			return id;//unsuccessful
		cv = new ContentValues(2);
		cv.put(DatabaseHandler.MAP_CAT, cat);
		cv.put(DatabaseHandler.MAP_PORT, id);
		final long res = (int) db.insert(DatabaseHandler.MAP_TABLE,
			null, cv);
		Log.d(DatabaseHandler.TAG, "Inserted map entry " + res
			+ " values: " + cv.toString());
		db.close();
		return id;
	}
	
	public void addPortalToCategory(long portalId, long categoryId) {
		final SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues(1);
		cv = new ContentValues(2);
		cv.put(DatabaseHandler.MAP_CAT, categoryId);
		cv.put(DatabaseHandler.MAP_PORT, portalId);
		final long res = (int) db.insert(DatabaseHandler.MAP_TABLE,
										 null, cv);
		Log.d(DatabaseHandler.TAG, "Inserted map entry " + res
			  + " values: " + cv.toString());
		db.close();
	}
	
	public void deletePortal(long portalId, long catId) {
		final SQLiteDatabase db = getWritableDatabase();
		int res = db.delete(MAP_TABLE, MAP_PORT + " == " + portalId
				  + " and " + MAP_CAT + " == " + catId, null);
		Log.d(TAG, "Deleted mapping entries " + res);
		//TODO: add limit clause 1
		Cursor c = db.rawQuery("select * from "
					+ DatabaseHandler.MAP_TABLE
					+ " where " + MAP_PORT + " == "
					+ portalId, null);
		if(c.getCount() == 0) {
			db.delete(DatabaseHandler.PORT_TABLE,
					DatabaseHandler.ID_COLUMN + " == " + portalId, null);
			Log.d(TAG, "Deleted portal because no more mappings");
		}
		c.close();
		db.close();
	}
	
	//TODO: Add sql functions for deleting and removing of garbage
	public synchronized void deleteCategory(long id) {
		final SQLiteDatabase db = getWritableDatabase();
		int res = db.delete(DatabaseHandler.CAT_TABLE,
			DatabaseHandler.ID_COLUMN + " == " + id, null);
		db.close();
		cats = null;
		Log.d(DatabaseHandler.TAG, "cat deleted " + res + " lines");
	}
	
	public synchronized List<Category> getCategories() {
		if(cats != null)
			return cats;
		SQLiteDatabase db = getReadableDatabase();
		//the names of all categories ordered by the id in asc order.
		final Cursor c = db.query(DatabaseHandler.CAT_TABLE,
			new String[] { DatabaseHandler.ID_COLUMN,
					DatabaseHandler.NAME_COLUMN }, null, null, null, null,
			null);
		c.moveToFirst();
		final int count = c.getCount();
		final List<Category> result = new ArrayList<Category>(count);
		for (int i = 0; i < count; i++) {
			result.add(new Category(c));
			c.moveToNext();
		}
		c.close();
		db.close();
		Log.d(DatabaseHandler.TAG, "Read categories " + count);
		cats = result;
		return result;
	}
	
	/**
	 * Finds all portals associated with this category.
	 * 
	 * @param cat The current category.
	 * @return A list of all portals associated with this category.
	 */
	public List<Portal> getPortals(Category cat) {
		final SQLiteDatabase db = getReadableDatabase();
		//all portals that belong to that category
		final Cursor c = db.rawQuery("select * from "
			+ DatabaseHandler.PORT_TABLE
			+ " where exists (select * from "
			+ DatabaseHandler.MAP_TABLE + " where "
			+ DatabaseHandler.ID_COLUMN + " == "
			+ DatabaseHandler.MAP_PORT + " and "
			+ DatabaseHandler.MAP_CAT + " == "
			+ cat.getId() + ") order by "
			+ DatabaseHandler.NAME_COLUMN, null);
		c.moveToFirst();
		final int count = c.getCount();
		final ArrayList<Portal> result = new ArrayList<Portal>(count);
		for (int i = 0; i < count; i++) {
			result.add(new Portal(c, cat));
			c.moveToNext();
		}
		c.close();
		db.close();
		Log.d(DatabaseHandler.TAG, "Read portals for cat " + cat
			+ " found " + count);
		return result;
	}
	
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DatabaseHandler.CAT_TABLE + " ("
			+ DatabaseHandler.ID_COLUMN
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DatabaseHandler.NAME_COLUMN
			+ " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE);");
		db.execSQL("CREATE TABLE " + DatabaseHandler.PORT_TABLE + " ("
			+ DatabaseHandler.ID_COLUMN
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DatabaseHandler.NAME_COLUMN + " TEXT NOT NULL, "
			+ DatabaseHandler.PORT_KEYS + " INTEGER DEFAULT 0);");
		db.execSQL("CREATE TABLE " + DatabaseHandler.MAP_TABLE + " ("
			+ DatabaseHandler.MAP_CAT
			+ " INTEGER REFERENCES categories(id) ON DELETE CASCADE, "
			+ DatabaseHandler.MAP_PORT
			+ " INTEGER REFERENCES portals(id) ON DELETE CASCADE, "
			+ "PRIMARY KEY(" + DatabaseHandler.MAP_CAT + ", "
			+ DatabaseHandler.MAP_PORT + ") ON CONFLICT IGNORE);");
		ContentValues cv = new ContentValues(2);
		cv.put(DatabaseHandler.ID_COLUMN, 0);
		cv.put(DatabaseHandler.NAME_COLUMN, "DEFAULT");
		db.insert(DatabaseHandler.CAT_TABLE, null, cv);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		db.execSQL("PRAGMA foreign_keys = ON;");
	}
	
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldV,
		final int newV) {
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
		cv.put(DatabaseHandler.PORT_KEYS, keys);
		db.update(DatabaseHandler.PORT_TABLE, cv,
			DatabaseHandler.ID_COLUMN + " == " + id, null);
		db.close();
	}
	
	public static void deleteSelf(Context c) {
		c.deleteDatabase(DatabaseHandler.NAME);
	}
}
