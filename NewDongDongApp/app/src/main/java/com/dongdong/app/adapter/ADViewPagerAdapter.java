package com.dongdong.app.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dd121.community.R;
import com.dongdong.app.util.LogUtils;

import java.util.List;

public class ADViewPagerAdapter extends PagerAdapter implements OnPageChangeListener {

    private Context mContext;
    private ViewPager mViewPager;
    private int[] mImgIdArray = new int[]{R.mipmap.ad_background, R.mipmap.ad_background, R.mipmap.ad_background};
    private ImageView[] mImageViews;
    private ImageView[] mTips;
    private boolean mShouldShowPoint;

    @SuppressWarnings("deprecation")
    public ADViewPagerAdapter(Context context, ViewPager viewPager,
                              List<String> datas, ViewGroup points) {
        mContext = context;
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
                    new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
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
//        int des = position % mImageViews.length;
//        View view = mImageViews[des];
//        try {
//            container.removeView(view);
//        } catch (Exception e) {
//            LogUtils.i("ADViewPagerAdapter.clazz--->>>destroyItem Exception:"
//                    + e.toString());
//            e.printStackTrace();
//        }
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        int des = position % mImageViews.length;
//        View view = mImageViews[des];
//        try {
//            container.addView(view, 0);
//        } catch (Exception e) {
//            LogUtils.i("ADViewPagerAdapter.clazz--->>>instantiateItem Exception:"
//                    + e.toString());
//            e.printStackTrace();
//        }
//        return view;
        int des = position % mImageViews.length;
        View channelView = LayoutInflater.from(mContext).inflate(
                R.layout.viewpager_item, container, false);
        ImageView mImageView = (ImageView) channelView
                .findViewById(R.id.iv_ad_item);
        mImageView.setImageResource(mImgIdArray[des]);
        container.addView(channelView);
        return channelView;
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

    public void showADViewPoints() {
        mShouldShowPoint = true;
    }

}
