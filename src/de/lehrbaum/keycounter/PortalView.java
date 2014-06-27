package de.lehrbaum.keycounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;


public class PortalView extends TextView implements MenuItem.OnMenuItemClickListener, OnLongClickListener {
	
	private static final String TAG = PortalView.class.getCanonicalName();

	private Portal portal;
	private Rect [] rects;
	private Paint lgreen, dgreen, red;
	private boolean longClick;
	private MenuInflater inflater;
	private MainActivity activity;
	
	public PortalView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PortalView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void init(MenuInflater inflater, MainActivity activity) {
		this.inflater = inflater;
		this.activity = activity;
	}
	
	@Override
	protected void onFinishInflate() {
		lgreen = new Paint();
		dgreen = new Paint();
		red = new Paint();
		lgreen.setColor(getResources().getColor(android.R.color.holo_green_light));
		dgreen.setColor(getResources().getColor(android.R.color.holo_green_dark));
		red.setColor(getResources().getColor(android.R.color.holo_red_light));
		setClickable(true);
		setLongClickable(true);
		setOnLongClickListener(this);
		longClick = false;
		super.onFinishInflate();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		double width = w/MainActivity.MAX_KEYS;
		double current = 0;
		rects = new Rect[(int) MainActivity.MAX_KEYS];
		for(int i = 0; i < MainActivity.MAX_KEYS; i++) {
			rects[i] = new Rect((int) current, 0, (int)(current+width),h);
			current = current+width;
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public Portal getPortal() {
		return portal;
	}
	
	public void setPortal(Portal portal) {
		this.portal = portal;
		setText(portal.getName());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if(!longClick) {
				//simple click
				int middle = getWidth() / 2;
				if (event.getX() < middle) {
					portal.decreaseCount();
				} else {
					portal.increaseCount();
				}
				invalidate();
			}
			else{
				//long click
				longClick = false;
			}
		}
		super.onTouchEvent(event);
		return true; //interessted in following events
	}

	@Override
	public boolean onLongClick(View p1)
	{
		showContextMenu();
		longClick = true;
		return true;
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu)
	{
		inflater.inflate(R.menu.menu_element, menu);
		MenuItem item = menu.findItem(R.id.delete);
		item.setOnMenuItemClickListener(this);
		super.onCreateContextMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem p1)
	{
		activity.removePortal(portal);
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(PortalView.TAG, "onDraw called");
		for(int i = 0; i < portal.getKeyCount(); i++) {
			canvas.drawRect(rects[i], i%2 == 0 ? lgreen : dgreen);
		}
		for(int i = portal.getKeyCount(); i < MainActivity.MAX_KEYS; i++) {
			canvas.drawRect(rects[i], red);
		}
		super.onDraw(canvas);
	}
}
