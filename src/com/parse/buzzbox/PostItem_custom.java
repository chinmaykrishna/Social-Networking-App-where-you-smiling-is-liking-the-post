package com.parse.buzzbox;

import android.content.Context;
import android.widget.LinearLayout;

public class PostItem_custom extends LinearLayout{

	Context con;
	int he;
	public PostItem_custom(Context context, int h) {
	    super(context);
	    con = context;
	    he = h;
	    inflate(context, R.layout.buzzbox_post_item, this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
	    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(he-100, MeasureSpec.EXACTLY));
	}
	
}
