package com.dongdong.app.widget;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.dongdong.app.util.TDevice;

public class CommonViewPager extends ViewPager {

    private Timer mTimer;

    private boolean isRefresh = true;
    private boolean isScroll = true;

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public void setIsScroll(boolean isScroll) {
        this.isScroll = isScroll;
    }

    public CommonViewPager(Context context, AttributeSet attrs) {
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
                                startAutoNextPager();
                        }
                    }
                });
    }

    protected void startAutoNextPager() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                CommonViewPager.this.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isRefresh) {
                            CommonViewPager.this.setCurrentItem(CommonViewPager.this.getCurrentItem() + 1);
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

    public CommonViewPager(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return isScroll && super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isScroll && super.onTouchEvent(ev);
    }
}
