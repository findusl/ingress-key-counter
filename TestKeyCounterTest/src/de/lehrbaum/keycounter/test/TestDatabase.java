package de.lehrbaum.keycounter.test;

import java.util.List;

import junit.framework.Assert;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import android.util.SparseArray;
import de.lehrbaum.keycounter.DatabaseHandler;
import de.lehrbaum.keycounter.Portal;

public class TestDatabase extends
	AndroidTestCase {
	private static final String TAG = TestDatabase.class.getCanonicalName();
	
	public TestDatabase() {
		super();
	}

	public void testIt() {
		/*
		 * Just a single method testing the database.
		 * If you make more, make nice set up and tear down. for me this was enough ;)
		 */
		Context c = getContext();
		DatabaseHandler dh = new DatabaseHandler(c);
		int hi = dh.addCategory("Hi");
		int bye = dh.addCategory("bye");
		Portal first = new Portal(c, hi, "first portal");
		Portal second = new Portal(c, hi, "second portal");
		Portal third = new Portal(c, bye, "third portal");
		SparseArray<String> cats = dh.getCategories();
		Log.d(TAG, "Cats: " + cats);
		Assert.assertTrue("Hi category is not there", cats.get(hi).equals("Hi"));
		Assert.assertTrue("Bye category is not there",
			cats.get(bye).equals("bye"));
		List<Portal> portals = dh.getPortals(hi);
		Assert.assertTrue("first portal is missing", portals.contains(first));
		Assert.assertTrue("second portal is missing", portals.contains(second));
		Assert.assertFalse("third portal is contained", portals.contains(third));
		portals = dh.getPortals(bye);
		Assert.assertFalse("first portal is contained", portals.contains(first));
		Assert.assertFalse(
			"second portal is contained", portals.contains(second));
		Assert.assertTrue("third portal is missing", portals.contains(third));
		first.increaseCount(c);
		portals = dh.getPortals(hi);
		Assert.assertTrue("first portal is wrong or missing",
			portals.contains(first));
		dh.deleteCategory(bye);
		portals = dh.getPortals(bye);
		Assert.assertTrue("More than one portal in this list",
			0 == portals.size());
		//clean up
		dh.deleteCategory(hi);
		first.delete(c);
		second.delete(c);
		third.delete(c);
	}
}