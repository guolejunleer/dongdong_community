package com.dongdong.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DynamicItemContainView extends RelativeLayout {

	private boolean mDragState = true;
	private String name;

	public DynamicItemContainView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public DynamicItemContainView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

	}

	public DynamicItemContainView(Context context) {
		super(context);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDragState(boolean dragState) {
		mDragState = dragState;
	}

	public boolean getDragState() {
		return mDragState;
	}

}
