package com.dongdong.app.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dd121.louyu.R;
import com.dongdong.app.adapter.ViewPagerAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.util.SPUtils;

public class LeadActivity extends BaseActivity {
	private ViewPager mVpmyviewPager;
	private LayoutInflater mLiinflater;
	private Button mBtbtn;
	private ViewPagerAdapter mVpadapter;
	private ImageView mIvView[];
	private ImageView mIvimgView;

	private AtomicInteger mAiwhat = new AtomicInteger(0);

	@Override
	protected int getLayoutId() {
		return R.layout.activity_lead;
	}

	@SuppressWarnings("deprecation")
	public void initView() {
		mVpmyviewPager = (ViewPager) this.findViewById(R.id.myviewpager);
		ViewGroup myviewGroup = (ViewGroup) this.findViewById(R.id.mynext);
		List<View> ar = new ArrayList<View>();
		mLiinflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v0 = mLiinflater.inflate(R.layout.myjoin, null);
		LinearLayout lay0 = (LinearLayout) v0.findViewById(R.id.gtimg);
		lay0.setBackgroundResource(R.mipmap.u1);
		ar.add(lay0);

		View v1 = mLiinflater.inflate(R.layout.myjoin, null);
		LinearLayout lay1 = (LinearLayout) v1.findViewById(R.id.gtimg);
		lay1.setBackgroundResource(R.mipmap.u2);
		ar.add(lay1);

		View v2 = mLiinflater.inflate(R.layout.myjoin, null);
		LinearLayout lay2 = (LinearLayout) v2.findViewById(R.id.gtimg);
		lay2.setBackgroundResource(R.mipmap.u3);
		ar.add(lay2);

		mBtbtn = (Button) v2.findViewById(R.id.mybtn);
		mBtbtn.setText(R.string.go);
		mBtbtn.setVisibility(View.VISIBLE);
		mBtbtn.setOnClickListener(OnmBtbtnClickListener);
		mVpadapter = new ViewPagerAdapter(LeadActivity.this, ar);
		mVpmyviewPager.setAdapter(mVpadapter);

		mIvView = new ImageView[ar.size()];

		for (int i = 0; i < ar.size(); i++) {
			mIvimgView = new ImageView(this);
			mIvimgView.setLayoutParams(new LayoutParams(9, 9));
			mIvimgView.setPadding(5, 5, 5, 5);
			mIvView[i] = mIvimgView;
			myviewGroup.addView(mIvView[i]);
		}

		mVpmyviewPager
				.setOnPageChangeListener(OnmVpmyviewPagerPageChangeListener);

		mVpmyviewPager.setOnTouchListener(OnmVpmyviewPagerTouchListener);
	}

	@Override
	public void initData() {
		boolean flag = (Boolean) SPUtils.getParam(this, "pro", "FIRST", true);
		if (flag) {
			SPUtils.setParam(LeadActivity.this, "pro", "FIRST", false);
		} else {
			startActivity(new Intent(LeadActivity.this, LoadActivity.class));
			LeadActivity.this.finish();
		}
	}

	/*****************************/
	OnPageChangeListener OnmVpmyviewPagerPageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			mAiwhat.getAndSet(arg0);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};
	/*****************************/

	OnTouchListener OnmVpmyviewPagerTouchListener = new OnTouchListener() {

		public boolean onTouch(View arg0, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
			}
			return false;
		}
	};

	OnClickListener OnmBtbtnClickListener = new OnClickListener() {

		public void onClick(View arg0) {
			startActivity(new Intent(LeadActivity.this, LoadActivity.class));
			LeadActivity.this.finish();
		}
	};

}