package de.lehrbaum.keycounter;

import android.database.sqlite.*;
import android.content.*;

public class DatabaseHandler extends SQLiteOpenHelper
{
	private static final String NAME = "portals";
	private static final int VERSION = 1;
	
	public DatabaseHandler(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY,"
                   + "name TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV)
	{
		// TODO: Implement this method
	}
}
