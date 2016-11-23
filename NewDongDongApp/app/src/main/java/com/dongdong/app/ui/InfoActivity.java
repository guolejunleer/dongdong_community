package com.dongdong.app.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

import com.dd121.louyu.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class InfoActivity extends BaseActivity implements
		OnTitleBarClickListener {

	private TitleBar mTitleBar;
	private RadioButton mRbwuye,mRbbaojin;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_info;
	}

	public void initView() {
		mTitleBar = (TitleBar) this.findViewById(R.id.tb_title);
		mRbwuye = (RadioButton) this.findViewById(R.id.wuye);
		mRbbaojin = (RadioButton) this.findViewById(R.id.baojin);

		mTitleBar.setTitleBarContent(getString(R.string.message));
		mTitleBar.setAddArrowShowing(false);
		mRbwuye.setChecked(true);
		mTitleBar.setOnTitleBarClickListener(this);

		mRbwuye.setOnClickListener(onRbListener);
		mRbbaojin.setOnClickListener(onRbListener);
	}

	OnClickListener onRbListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.wuye:
				break;
			case R.id.baojin:
				break;
			}
		}
	};

	@Override
	public void initData() {

	}

	@Override
	public void onBackClick() {
		InfoActivity.this.finish();
	}

	@Override
	public void onTitleClick() {

	}

	@Override
	public void onAddClick() {
		
	}

}
