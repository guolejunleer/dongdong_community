package com.dongdong.app.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ApplyKeyAdapter extends PagerAdapter {

	private List<View> mListViews;

	public ApplyKeyAdapter(List<View> mListViews) {
		this.mListViews = mListViews;
	}

	@Override
	public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
		(arg0).removeView(mListViews.get(arg1));
	}

	@Override
	public Object instantiateItem(ViewGroup arg0, int arg1) {
		(arg0).addView(mListViews.get(arg1), 0);
		return mListViews.get(arg1);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

}
