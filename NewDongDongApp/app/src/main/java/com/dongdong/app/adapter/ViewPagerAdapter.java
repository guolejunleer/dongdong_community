package com.dongdong.app.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {

	private List<View> mList;

	public ViewPagerAdapter(Context mContext, List<View> mList) {
		this.mList = mList;
	}

	public int getCount() {
		return mList.size();
	}

	public boolean isViewFromObject(View v, Object arg1) {
		return v == arg1;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mList.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mList.get(position), 0);
		return mList.get(position);
	}
}
