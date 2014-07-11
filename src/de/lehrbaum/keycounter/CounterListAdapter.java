package de.lehrbaum.keycounter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * An adapter showing the portals in a list view.
 * 
 * @author Sebastian Lehrbaum
 * @version 1.0
 */
public class CounterListAdapter extends ArrayAdapter<Portal> {
	
	
	private final LayoutInflater inflater;
	private final MenuInflater menuInflater;
	
	public CounterListAdapter(final Context context, final List<Portal> portals) {
		super(context, 0, portals);
		inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menuInflater = new MenuInflater(context);
	}
	
	@Override
	public View getView(final int position, final View convertView,
		final ViewGroup parent) {
		final Portal item = getItem(position);
		View view;
		if (convertView == null)
			//create new view
			view = inflater.inflate(R.layout.list_element, parent, false);
		else
			//reuse old view
			view = convertView;
		//get the portal view element in the view
		final PortalView pView = (PortalView) view.findViewById(R.id.portalView);
		//set it's portal
		pView.init(item, menuInflater, this);
		return view;
	}
}
