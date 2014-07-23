package de.lehrbaum.keycounter;

import java.util.Collections;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import de.lehrbaum.keycounter.MainActivity.OnTextInputSubmitted;

public class MainFragment extends ListFragment {
	private static final String TAG = MainFragment.class
		.getCanonicalName();
	/**
	 * This is the maximum number of keys that can be displayed in the view. Change this value if you
	 * want to display more or less keys Saved as double to make devision results double too.
	 */
	public static double MAX_KEYS = 10d;
	
	private List<Portal> portals;
	private CounterListAdapter adapter;
	private Category currentCat;//the id of the current category
	private Menu menu;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
		ViewGroup container, Bundle savedInstanceState) {
		List<Category> cats = 
			new DatabaseHandler(getActivity()).getCategories();
		if (cats.size() == 0)//add default category
			cats.add(new Category(getActivity(), "Default"));
		//take the first category as current one
		currentCat = cats.get(0);
		DatabaseHandler dh = new DatabaseHandler(getActivity());
		portals = dh.getPortals(currentCat);
		adapter = new CounterListAdapter(getActivity(), portals);
		setListAdapter(adapter);
		return super.onCreateView(inflater, container,
			savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "On create options menu");
		inflater.inflate(R.menu.menu_main, menu);
		List<Category> cats = 
			new DatabaseHandler(getActivity()).getCategories();
		for (Category c : cats) {
			MenuItem item = menu.add(R.id.group_cats, c.getId(),
				Menu.NONE, c.getName());
			menu.setGroupCheckable(R.id.group_cats, true, true);
			item.setCheckable(true);
			if (currentCat.equals(c))
				item.setChecked(true);
		}
		this.menu = menu;
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add) {
			onAddClicked();
			return true;
		}
		if (item.getGroupId() == R.id.group_cats) {
			//The user selected one of the categories
			List<Category> cats = 
				new DatabaseHandler(getActivity()).getCategories();
			for (Category c : cats) {
				if (item.getItemId() == c.getId()) {
					if (currentCat.equals(c))
						return false;
					//category was changed
					DatabaseHandler dh = new DatabaseHandler(getActivity());
					currentCat = c;
					portals = dh.getPortals(currentCat);
					adapter = new CounterListAdapter(getActivity(),
						portals);
					setListAdapter(adapter);
					item.setChecked(true);
					return true;
				}
			}
			Log.d(MainFragment.TAG, "The id of the selected menu item "
				+ item.getItemId() + " did not match any cat");
			return false;//did not match the categories. ackward
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Called when the menu Item add is clicked.
	 */
	public void onAddClicked() {
		OnTextInputSubmitted inputProcessor = new OnTextInputSubmitted() {
			@Override
			public void processInput(String input) {
				portals.add(new Portal(getActivity(), currentCat, input));
				Collections.sort(portals);
				MainFragment.this.getActivity().runOnUiThread(
					new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
			}
		};
		MainActivity.showTextInputDialog(getActivity(),
			R.string.newPortalTitle, R.string.newPortalMessage,
			inputProcessor);
	}
	
/*	@Override
	public void onAdd(final Category item) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				menu.add(R.id.group_cats, item.getId(), Menu.NONE,
					item.getName()).setCheckable(true);
			}
		});
	}
	
	@Override
	public void onDestroy(final Category item) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				menu.removeItem(item.getId());
			}
		});
	}*/
}
