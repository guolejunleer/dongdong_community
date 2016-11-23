package com.dongdong.app.ui;

import com.dd121.louyu.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class HomeSafetyActivity extends BaseActivity implements
		OnTitleBarClickListener {
	private TitleBar mTitleBar;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_homesafety;
	}

	@Override
	public void initView() {
		mTitleBar = (TitleBar) this.findViewById(R.id.tb_title);
		mTitleBar.setTitleBarContent(getString(R.string.homesafe));
		mTitleBar.setAddArrowShowing(false);
		mTitleBar.setOnTitleBarClickListener(this);
	}

	@Override
	public void initData() {
	}

	@Override
	public void onBackClick() {
		HomeSafetyActivity.this.finish();
	}

	@Override
	public void onTitleClick() {

	}

	@Override
	public void onAddClick() {
	}

}
