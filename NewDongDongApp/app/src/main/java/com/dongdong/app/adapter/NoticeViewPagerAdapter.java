package com.dongdong.app.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dd121.louyu.R;

public class NoticeViewPagerAdapter extends PagerAdapter implements
		OnPageChangeListener {

	private ViewPager mViewPager;
	private int[] mImgIdArray = new int[] { R.mipmap.notice1,
			R.mipmap.notice2, R.mipmap.notice3 };
	private ImageView[] mImageViews;
	private ImageView[] mTips;
	private boolean mShouldShowPoint;

	@SuppressWarnings("deprecation")
	public NoticeViewPagerAdapter(Context context, ViewPager viewPager,
			List<String> datas, ViewGroup points) {
		mViewPager = viewPager;

		mImageViews = new ImageView[mImgIdArray.length];
		for (int i = 0; i < mImageViews.length; i++) {
			ImageView imageView = new ImageView(context);
			mImageViews[i] = imageView;
			imageView.setBackgroundResource(mImgIdArray[i]);
		}

		mViewPager.setOnPageChangeListener(this);

		mTips = new ImageView[mImgIdArray.length];
		for (int i = 0; i < mTips.length; i++) {
			ImageView imageView = new ImageView(context);
			imageView.setLayoutParams(new LayoutParams(10, 10));
			mTips[i] = imageView;
			if (i == 0) {
				mTips[i].setBackgroundResource(R.mipmap.checked);
			} else {
				mTips[i].setBackgroundResource(R.mipmap.unchecked);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			points.addView(imageView, layoutParams);
		}
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// ((ViewPager) container).removeView(mImageViews[position
		// % mImageViews.length]);
	}

	// 当前显示的view
	private View mCurrentView;

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mCurrentView = (View) object;
	}

	public View getPrimaryItem() {
		return mCurrentView;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = mImageViews[position % mImageViews.length];

		try {
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mImageViews[0] == mCurrentView) {
//						AppContext.showToastShort("连接1");
//						mViewPager.getContext().startActivity(
//								new Intent(mViewPager.getContext(),
//										AdverOneActivity.class));
					} else if (mImageViews[1] == mCurrentView) {
//						AppContext.showToastShort("连接2");
//						mViewPager.getContext().startActivity(
//								new Intent(mViewPager.getContext(),
//										AdverTwoActivity.class));
					} else {
//						AppContext.showToastShort("连接3");
//						mViewPager.getContext().startActivity(
//								new Intent(mViewPager.getContext(),
//										AdverThreeActivity.class));
					}
				}
			});
			((ViewPager) container).addView(view, 0);
		} catch (Exception e) {

		}

		return view;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		if (mShouldShowPoint)
			setImageBackground(position % mImageViews.length);
	}

	private void setImageBackground(int selectItems) {
		for (int i = 0; i < mTips.length; i++) {
			if (i == selectItems) {
				mTips[i].setBackgroundResource(R.mipmap.checked);
			} else {
				mTips[i].setBackgroundResource(R.mipmap.unchecked);
			}
		}
	}

	public void showNoticeViewPoints() {
		mShouldShowPoint = true;
	}
}
