package de.lehrbaum.keycounter;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends ListActivity {
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	/**
	 * This is the maximum number of keys that can be displayed in the view. Change this value if you
	 * wan't to display more or less keys Saved as double to make devision results double too.
	 */
	public static double MAX_KEYS = 10d;
	
	private List<Portal> portals;
	private CounterListAdapter adapter;
	private int currentCat;//the id of the current category
	
	/**
	 * Called when the menu Item add is clicked.
	 */
	public boolean onAddClicked(final MenuItem m) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		//show input dialog for adding portal
		alert.setTitle("create portal");
		alert.setMessage("Please enter the name of the new portal.");
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int whichButton) {
				//User entered Portal
				final String name = input.getText().toString();
				portals.add(new Portal(getApplicationContext(), currentCat, name));
				//could extract to second thread, to not block the ui thread
				Collections.sort(portals);
				adapter.notifyDataSetChanged();
			}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int whichButton) {
				// Canceled.
			}
		});
		//show
		alert.show();
		return true;
	}
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: Load the categories and load the portals of first cat
		adapter = new CounterListAdapter(this, portals, this);
		setListAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater;
		//an error in older sdk versions...
		if (Build.VERSION.SDK_INT > 15)
			inflater = getMenuInflater();
		else
			inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}
	
	/**
	 * Removes a portal from the portals list.
	 * 
	 * @param p The portal to remove.
	 * @return <code>true</code> if the portal was removed successfully.
	 */
	public boolean removePortal(final Portal p) {
		final boolean r = portals.remove(p);
		adapter.notifyDataSetChanged();
		return r;
	}
	
	/*/**
	 * Loads the saved portals from the shared Preferences.
	 
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
	}*/
}
