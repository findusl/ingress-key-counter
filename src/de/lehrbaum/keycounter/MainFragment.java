package de.lehrbaum.keycounter;

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
	
	private PortalList portals;
	private CounterListAdapter adapter;
	private Category currentCat;//the id of the current category
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
		ViewGroup container, Bundle savedInstanceState) {
		DatabaseHandler dh = new DatabaseHandler(getActivity());
		List<Category> cats = dh.getCategories();
		if (cats.size() == 0)//add default category
			cats.add(new Category(getActivity(), "Default"));
		//take the first category as current one
		currentCat = cats.get(0);
		portals = new PortalList(dh, currentCat);
		adapter = new CounterListAdapter(getActivity(), portals);
		setListAdapter(adapter);
		return super.onCreateView(inflater, container,
			savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(MainFragment.TAG, "On create options menu");
		inflater.inflate(R.menu.menu_main, menu);
		List<Category> cats = new DatabaseHandler(getActivity())
			.getCategories();
		for (Category c : cats) {
			MenuItem item = menu.add(R.id.group_cats, c.getId(),
				Menu.NONE, c.getName());
			menu.setGroupCheckable(R.id.group_cats, true, true);
			item.setCheckable(true);
			if (currentCat.equals(c))
				item.setChecked(true);
		}
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
			DatabaseHandler dh = new DatabaseHandler(getActivity());
			List<Category> cats = dh.getCategories();
			for (Category c : cats) {
				if (item.getItemId() == c.getId()) {
					if (currentCat.equals(c))
						return false;
					//category was changed
					currentCat = c;
					portals.changeCategory(currentCat);
					adapter.notifyDataSetChanged();
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
				portals.add(new Portal(getActivity(), input));
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
}