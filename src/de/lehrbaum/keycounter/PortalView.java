package de.lehrbaum.keycounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class PortalView extends TextView implements
	MenuItem.OnMenuItemClickListener, OnLongClickListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = PortalView.class.getCanonicalName();
	
	private Portal portal;
	/*rectangles used for showing the number of keys
	 * All of the same size positioned just to the right
	 * side of the previous one.
	 */
	private Rect[] rects;
	private Paint lgreen, dgreen, red;//colors for the painting
	private boolean longClick;//true if there has been a long click
	private MenuInflater inflater;
	private MainActivity activity;
	
	public PortalView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PortalView(final Context context, final AttributeSet attrs,
		final int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public Portal getPortal() {
		return portal;
	}
	
	public void init(final Portal portal, final MenuInflater inflater,
		final MainActivity activity) {
		this.portal = portal;
		setText(portal.getName());
		this.inflater = inflater;
		this.activity = activity;
	}
	
	@Override
	protected void onCreateContextMenu(final ContextMenu menu) {
		//showing the context menu with the delete button
		inflater.inflate(R.menu.menu_element, menu);
		final MenuItem item = menu.findItem(R.id.delete);
		item.setOnMenuItemClickListener(this);
		super.onCreateContextMenu(menu);
	}
	
	@Override
	protected void onDraw(final Canvas canvas) {
		//draw this portal view. Visualize the number of keys by coloring the rectangles.
		//		Log.d(PortalView.TAG, "onDraw called");
		for (int i = 0; i < portal.getKeyCount(); i++)
			canvas.drawRect(rects[i], i % 2 == 0 ? lgreen : dgreen);
		for (int i = portal.getKeyCount(); i < MainActivity.MAX_KEYS; i++)
			canvas.drawRect(rects[i], red);
		super.onDraw(canvas);
	}
	
	@Override
	protected void onFinishInflate() {
		//initialize the stuff used for painting. This will save time when painting.
		lgreen = new Paint();
		dgreen = new Paint();
		red = new Paint();
		lgreen
			.setColor(getResources().getColor(android.R.color.holo_green_light));
		dgreen.setColor(getResources().getColor(android.R.color.holo_green_dark));
		red.setColor(getResources().getColor(android.R.color.darker_gray));
		setClickable(true);
		setLongClickable(true);
		setOnLongClickListener(this);
		longClick = false;
		super.onFinishInflate();
	}
	
	@Override
	public boolean onLongClick(final View p1) {
		showContextMenu();
		longClick = true;//set the flag so it won't be interpreted as short click
		return true;
	}
	
	@Override
	public boolean onMenuItemClick(final MenuItem p1) {
		//delete clicked
		activity.removePortal(portal);
		return true;
	}
	
	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw,
		final int oldh) {
		//resize the rectangles fitting the new size.
		final double width = w / MainActivity.MAX_KEYS;
		double current = 0;
		rects = new Rect[(int) MainActivity.MAX_KEYS];
		for (int i = 0; i < MainActivity.MAX_KEYS; i++) {
			rects[i] = new Rect((int) current, 0, (int) (current + width), h);
			current = current + width;
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP)
			//user removed his finger
			if (!longClick) {
				//simple click
				final int middle = getWidth() / 2;
				if (event.getX() < middle)
					portal.decreaseCount(getContext());
				else
					portal.increaseCount(getContext());
				invalidate();
			} else
				//long click. reset the flag and don't use this touch.
				longClick = false;
		super.onTouchEvent(event);
		return true; //Interested in following events
	}
}
