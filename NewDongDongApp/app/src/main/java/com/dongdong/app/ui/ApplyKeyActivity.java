package com.dongdong.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

import com.dd121.louyu.R;
import com.dongdong.app.adapter.ApplyKeyAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class ApplyKeyActivity extends BaseActivity implements
		OnTitleBarClickListener {

	private TitleBar mTitleBar;
	private RadioButton mRbyanzhengkey, mRberweikey;
	private ViewPager mVp;

	private List<View> mListViews;
	private View myanzhengView, merweiView;

	ApplyKeyAdapter mAdapter;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_applykey;
	}

	@SuppressWarnings("deprecation")
	public void initView() {
		mTitleBar = (TitleBar) this.findViewById(R.id.tb_title);
		mRbyanzhengkey = (RadioButton) this.findViewById(R.id.yanzhengkey);
		mRberweikey = (RadioButton) this.findViewById(R.id.erweikey);
		mVp = (ViewPager) this.findViewById(R.id.myviewpager);
		mTitleBar.setTitleBarContent(getString(R.string.applykey));

		mRbyanzhengkey.setChecked(true);
		mTitleBar.setOnTitleBarClickListener(this);
		mTitleBar.setAddArrowShowing(false);
		mVp.setOnPageChangeListener(new MyOnPageChangeListener());

		mRberweikey.setOnClickListener(onRbListener);
		mRbyanzhengkey.setOnClickListener(onRbListener);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				mRbyanzhengkey.setChecked(true);
				break;
			case 1:
				mRberweikey.setChecked(true);
				break;
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

	OnClickListener onRbListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.yanzhengkey:
				mVp.setCurrentItem(0);
				break;
			case R.id.erweikey:
				mVp.setCurrentItem(1);
				break;
			default:
				break;
			}
		}
	};

	public void initData() {
		mListViews = new ArrayList<View>();
		myanzhengView = this.getLayoutInflater().inflate(
				R.layout.applykey_yanzheng, null);
		merweiView = this.getLayoutInflater().inflate(R.layout.applykey_erwei,
				null);

		mListViews.add(myanzhengView);
		mListViews.add(merweiView);

		mVp.setAdapter(new ApplyKeyAdapter(mListViews));
	}

	@Override
	public void onBackClick() {
		ApplyKeyActivity.this.finish();
	}

	@Override
	public void onTitleClick() {
	}

	@Override
	public void onAddClick() {
	}
}
