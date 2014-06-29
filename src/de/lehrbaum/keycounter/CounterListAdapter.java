package de.lehrbaum.keycounter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * An adapter showing the portals in a list view.
 * 
 * @author Sebastian Lehrbaum
 * @version 1.0
 */
public class CounterListAdapter extends BaseAdapter {
	
	private List<Portal> portals;
	
	private LayoutInflater inflater;
	private MenuInflater menuInflater;
	private MainActivity activity;
	
	public CounterListAdapter(Context context, List<Portal> portals, MainActivity activity) {
		this.portals = portals;
		inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menuInflater = new MenuInflater(context);
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		return portals.size();
	}
	
	@Override
	public Object getItem(int position) {
		return portals.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Portal item = portals.get(position);
		View view;
		if (convertView == null) {
			//create new view
			view = inflater.inflate(R.layout.list_element, parent, false);
		} else {
			//reuse old view
			view = convertView;
		}
		//get the portal view element in the view
		PortalView pView = (PortalView) view.findViewById(R.id.portalView);
		//set it's portal
		pView.init(item, menuInflater, activity);
		return view;
	}
}
