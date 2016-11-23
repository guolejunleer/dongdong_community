package com.dongdong.app.widget;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.dongdong.app.adapter.ADViewPagerAdapter;
import com.dongdong.app.adapter.LinkRoomDynamicLayoutAdapter;
import com.dongdong.app.adapter.NoticeViewPagerAdapter;
import com.dongdong.app.util.TDevice;

public class AdViewPager extends ViewPager {

	private Timer mTimer;

	private PagerAdapter adPagerAdapter;

	private boolean isRefresh = true;

	public void setIsrefresh(boolean isrefresh) {
		this.isRefresh = isrefresh;
	}
	public AdViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						getViewTreeObserver()
								.removeOnGlobalLayoutListener(this);
						int d = (int) (TDevice.getScreenWidth() / 4);
						if (getHeight() < 2 * d) {
							if (adPagerAdapter != null && adPagerAdapter instanceof ADViewPagerAdapter) {
								startAutoNextPager();
							}
						}
					}
				});
	}

	protected void startAutoNextPager() {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				AdViewPager.this.post(new Runnable() {

					@Override
					public void run() {
						if (isRefresh) {
							AdViewPager.this.setCurrentItem(AdViewPager.this.getCurrentItem() + 1);
						}
					}
				});
			}
		}, new Date(), 3000);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public AdViewPager(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

	}

	public void showUI(ViewGroup points, int type) {
		if (type == LinkRoomDynamicLayoutAdapter.COMMON_VIEWPAGER) {
			adPagerAdapter = new NoticeViewPagerAdapter(getContext(),this,null, points);
			((NoticeViewPagerAdapter) adPagerAdapter).showNoticeViewPoints();
		} else {
			adPagerAdapter = new ADViewPagerAdapter(getContext(), this, null,points);
			((ADViewPagerAdapter) adPagerAdapter).showADViewPoints();
		}
		this.setAdapter(adPagerAdapter);
		this.setCurrentItem(10000);
	}
}
