package de.lehrbaum.keycounter;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.lehrbaum.keycounter.MainActivity.OnTextInputSubmitted;

public class CategoriesFragment extends ListFragment {
	@SuppressWarnings("unused")
	private static final String TAG = CategoriesFragment.class
		.getCanonicalName();
	private ArrayAdapter<Category> adapter;
	
	public CategoriesFragment() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List <Category> cats = 
			new DatabaseHandler(getActivity()).getCategories();
		adapter = new ArrayAdapter<Category>(getActivity(),
			android.R.layout.simple_list_item_1, cats);
		setListAdapter(adapter);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		v.showContextMenu();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
		MenuInflater inflater = new MenuInflater(v.getContext());
		inflater.inflate(R.menu.menu_element, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
			Category cat = adapter.getItem(info.position);
			cat.delete(getActivity());
			List <Category> cats = 
					new DatabaseHandler(getActivity()).getCategories();
			adapter = new ArrayAdapter<Category>(getActivity(),
						 android.R.layout.simple_list_item_1, cats);
			setListAdapter(adapter);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_categories, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			onAddClicked();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void onAddClicked() {
		OnTextInputSubmitted textListener = new OnTextInputSubmitted() {
			@Override
			public void processInput(String input) {
				Category cat = new Category(getActivity(), input);
				List <Category> cats = 
					new DatabaseHandler(getActivity()).getCategories();
				adapter = new ArrayAdapter<Category>(getActivity(),
							android.R.layout.simple_list_item_1, cats);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setListAdapter(adapter);
					}
				});
			}
		};
		MainActivity.showTextInputDialog(getActivity(), R.string.newCatTitle,
			R.string.newCatMessage, textListener);
	}
}
