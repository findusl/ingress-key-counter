package de.lehrbaum.keycounter;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends ListActivity {
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	private static final String PREFS_NAME = "Portals";
	private static final String PORT_LENGTH = "lengt";
	public static double MAX_KEYS = 10d;
	
	private List<Portal> portals;
	private CounterListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadPortals();
		adapter = new CounterListAdapter(getApplicationContext(), portals, this);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onPause() {
		persistPortals();
		super.onPause();
	}
	
	public boolean removePortal(Portal p) {
		boolean r = portals.remove(p);
		adapter.notifyDataSetChanged();
		return r;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}
	
	public void onAddClicked(MenuItem m) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("create portal");
		alert.setMessage("Please enter the name of the new portal.");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
				String name = input.getText().toString();
				portals.add(new Portal(name, 0));
				adapter.notifyDataSetChanged();
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  @Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		input.requestFocus();
	}

	private void loadPortals() {
		SharedPreferences settings = getSharedPreferences(
			MainActivity.PREFS_NAME, 0);
		int size = settings.getInt(MainActivity.PORT_LENGTH, 0);
		portals = new ArrayList<Portal>(size);
		for (int i = 0; i < size; i++) {
			String name = settings.getString(i + "_name", "No Name");
			int count = settings.getInt(i + "_count", 0);
			Portal p = new Portal(name, count);
			portals.add(p);
		}
		//		portals.add(new Portal("test", 2));
		//		portals.add(new Portal("Test", 5));
	}
	
	private void persistPortals() {
		SharedPreferences settings = getSharedPreferences(
			MainActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		int size = portals.size();
		editor.putInt(MainActivity.PORT_LENGTH, size);
		for (int i = 0; i < size; i++) {
			Portal p = portals.get(i);
			editor.putString(i + "_name", p.getName());
			editor.putInt(i + "_count", p.getKeyCount());
		}
		
		editor.commit();
	}
}
