package de.lehrbaum.keycounter;

import java.util.ArrayList;
import java.util.Collections;
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
	/**
	 * This is the maximum number of keys that can be displayed in the view. Change this value if you
	 * wan't to display more or less keys Saved as double to make devision results double too.
	 */
	public static double MAX_KEYS = 10d;
	
	private List<Portal> portals;
	private CounterListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Load the portals
		loadPortals();
		adapter = new CounterListAdapter(getApplicationContext(), portals, this);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onPause() {
		persistPortals();
		super.onPause();
	}
	
	/**
	 * Removes a portal from the portals list.
	 * 
	 * @param p The portal to remove.
	 * @return <code>true</code> if the portal was removed successfully.
	 */
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
	
	/**
	 * Called when the menu Item add is clicked.
	 */
	public boolean onAddClicked(MenuItem m) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		//show input dialog for adding portal
		alert.setTitle("create portal");
		alert.setMessage("Please enter the name of the new portal.");
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
				//User entered Portal
				String name = input.getText().toString();
				portals.add(new Portal(name, 0));
				//could extract to second thread, to not block the ui thread
				Collections.sort(portals);
				adapter.notifyDataSetChanged();
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  @Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});
		//show
		alert.show();
		return true;
	}
	
	/**
	 * Loads the saved portals from the shared Preferences.
	 */
	private void loadPortals() {
		SharedPreferences settings = getSharedPreferences(
			MainActivity.PREFS_NAME, 0);
		//the number of portals
		int size = settings.getInt(MainActivity.PORT_LENGTH, 0);
		portals = new ArrayList<Portal>(size);
		for (int i = 0; i < size; i++) {
			//loading each portal
			String name = settings.getString(i + "_name", "No Name");
			int count = settings.getInt(i + "_count", 0);
			Portal p = new Portal(name, count);
			portals.add(p);
		}
		//sorting
		Collections.sort(portals);
	}
	
	/**
	 * Writes all portals to the saved Preferences.
	 */
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
