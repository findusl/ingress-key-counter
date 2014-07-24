package de.lehrbaum.keycounter;

import android.content.Context;

/**
 * This list encapsulates a list of portals and updates changes to the database.
 */
public class PortalList extends AbstractEncapsuledList<Portal> {
	protected Category cat;
	
	public PortalList(Context c, Category cat) {
		this(new DatabaseHandler(c), cat);
	}
	
	public PortalList(DatabaseHandler dh, Category cat) {
		super(dh, dh.getPortals(cat.getId()));
		this.cat = cat;
	}
	
	@Override
	protected void onAdd(Portal p) {
		dh.addPortalToCategory(p.getId(), cat.getId());
	}
	
	@Override
	protected void onRemove(Portal p) {
		dh.deletePortal(p.getId(), cat.getId());
	}
	
	public void changeCategory(Category cat) {
		internalList = dh.getPortals(cat.getId());
		this.cat = cat;
	}
}