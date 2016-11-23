package com.dongdong.app.ui;

import com.dd121.louyu.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class FunctionManagerActivity extends BaseActivity implements OnTitleBarClickListener {
	private TitleBar mTitleBar;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_function_manager;
	}
	@Override
	public void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mTitleBar.setTitleBarContent(getString(R.string.functionmanager));
		mTitleBar.setOnTitleBarClickListener(this);
		mTitleBar.setAddArrowShowing(false);
		
	}

	@Override
	public void initData() {
		
	}
	@Override
	public void onBackClick() {
		FunctionManagerActivity.this.finish();
	}
	@Override
	public void onTitleClick() {
	}
	@Override
	public void onAddClick() {
	}

}
