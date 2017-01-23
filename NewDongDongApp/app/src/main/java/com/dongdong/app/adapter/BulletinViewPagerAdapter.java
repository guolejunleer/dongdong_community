package com.dongdong.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.bean.BulletinBean;
import com.dongdong.app.ui.BulletinDetailActivity;
import com.dongdong.app.widget.CommonViewPager;
import com.gViewerX.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class BulletinViewPagerAdapter extends PagerAdapter implements OnPageChangeListener {

    private Context mContext;
    private CommonViewPager mViewPager;
    private List<BulletinBean> mBulletinBeanList = new ArrayList<>();
    private ImageView[] mTips;
    private boolean mShouldShowPoint = true;

    private ViewGroup mPoints;

    public BulletinViewPagerAdapter(Context context, CommonViewPager viewPager, ViewGroup points) {
        mContext = context;
        mViewPager = viewPager;
        mPoints = points;
        mViewPager.setOnPageChangeListener(this);
    }

    public void setBulletinData(List<BulletinBean> list) {
        mBulletinBeanList.clear();
        for (BulletinBean bean : list) {
            mBulletinBeanList.add(bean);
        }
        mTips = new ImageView[mBulletinBeanList.size()];
        mPoints.removeAllViews();
        for (int i = 0; i < mTips.length; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(10, 10));
            mTips[i] = imageView;
            if (i == 0) {
                mTips[i].setBackgroundResource(R.mipmap.checked);
            } else {
                mTips[i].setBackgroundResource(R.mipmap.unchecked);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            mPoints.addView(imageView, layoutParams);
        }
        notifyDataSetChanged();
        LogUtils.i("BulletinViewPagerAdapter.clazz-->>>setBulletinData list.size():"
                + list.size());
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.bulletin_view_pager_item, null);
        TextView tvViewTitle = (TextView) view.findViewById(R.id.tv_view_title);
        TextView tvViewTime = (TextView) view.findViewById(R.id.tv_view_time);
        LogUtils.i("BulletinViewPagerAdapter.clazz-->>>instantiateItem position:"
                + position + ",mBulletinBeanList.size():" + mBulletinBeanList.size());
        if (mBulletinBeanList.size() == 0) {
            tvViewTitle.setText("暂无物业公告");
            tvViewTime.setVisibility(View.GONE);
            mViewPager.setIsRefresh(false);
            mViewPager.setIsScroll(false);
        } else {
            //对ViewPager页号求模取出View列表中要显示的项
            final int newPosition = position % mBulletinBeanList.size();
            final BulletinBean bean = mBulletinBeanList.get(newPosition);
            tvViewTitle.setText(bean.getTitle());
            tvViewTime.setText(bean.getCreated());
            tvViewTime.setVisibility(View.VISIBLE);
            mViewPager.setIsRefresh(true);
            mViewPager.setIsScroll(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.i("BulletinViewPagerAdapter.clazz-->>>instantiateItem onClick-->>>>>>>>>>>>>newPosition:" + newPosition);
                    Intent intent = new Intent(mContext, BulletinDetailActivity.class);
                    intent.putExtra(BulletinDetailActivity.BULLETIN_TITLE, bean.getTitle());
                    intent.putExtra(BulletinDetailActivity.BULLETIN_NOTICE, bean.getNotice());
                    intent.putExtra(BulletinDetailActivity.BULLETIN_CREATED, bean.getCreated());
                    mContext.startActivity(intent);
                }
            });
        }
        container.addView(view);
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
        if (!mShouldShowPoint) {
            if (mBulletinBeanList.size() == 0) {
                setImageBackground(0);
            } else {
                setImageBackground(position % mBulletinBeanList.size());
            }
        }
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; mTips != null && i < mTips.length; i++) {
            if (i == selectItems) {
                mTips[i].setBackgroundResource(R.mipmap.checked);
            } else {
                mTips[i].setBackgroundResource(R.mipmap.unchecked);
            }
        }
    }

    public void hiddenADViewPoints() {
        mShouldShowPoint = false;
    }
}
