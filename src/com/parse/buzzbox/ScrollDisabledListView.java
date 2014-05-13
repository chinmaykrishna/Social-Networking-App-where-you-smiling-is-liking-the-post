package com.parse.buzzbox;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class ScrollDisabledListView extends ListView {
	 
	private int mPosition;
 
	public ScrollDisabledListView(Context context) {
	super(context);
	}
 
	public ScrollDisabledListView(Context context, AttributeSet attrs) {
	super(context, attrs);
	}
 
	public ScrollDisabledListView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	}
 
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		
		   if(ev.getAction()==MotionEvent.ACTION_MOVE)
		   {
			   return true; 
		   }
		   return false;
	}
}