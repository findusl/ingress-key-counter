package de.lehrbaum.keycounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fragment f = new MainFragment();
		FragmentTransaction transaction = getFragmentManager()
			.beginTransaction();
		transaction.add(android.R.id.content, f);
		transaction.commit();
		//deleting all the preferences
		SharedPreferences settings = getSharedPreferences("Portals", 0);
		settings.edit().clear().commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.prefs:
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			Fragment newF = new CategoriesFragment();
			transaction.replace(android.R.id.content, newF);
			transaction.addToBackStack(null);
			transaction.commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static void showTextInputDialog(Context context, int title,
		int message, final OnTextInputSubmitted inputProcessor) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(
			context);
		
		//show input dialog for adding portal
		alert.setTitle(context.getText(title));
		alert.setMessage(context.getText(message));
		// Set an EditText view to get user input 
		final EditText input = new EditText(context);
		alert.setView(input);
		
		alert.setPositiveButton("Ok",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog,
					final int whichButton) {
					//User entered Portal
					final String name = input.getText().toString();
					//extract the rest to new thread
					new Thread() {
						@Override
						public void run() {
							inputProcessor.processInput(name);
						};
					}.start();
				}
			});
		
		alert.setNegativeButton("Cancel",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog,
					final int whichButton) {
					// Canceled.
				}
			});
		//show
		alert.show();
	}
	
	public interface OnTextInputSubmitted {
		/**
		 * Will be called in a background Thread.
		 * 
		 * @param input The users Input.
		 */
		public void processInput(String input);
	}
}
