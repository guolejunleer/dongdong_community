package com.dongdong.app.ui;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

import com.dd121.louyu.R;
import com.dongdong.app.MainActivity;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class RepairsActivity extends BaseActivity implements OnTitleBarClickListener {
	private TitleBar mTitleBar;
	private RadioButton mRbxinzengbaoxiu, mRbbaoxiurecord;
	@Override
	protected int getLayoutId() {
		return R.layout.activity_repairs;
	}
	@Override
	public void initView() {
		mTitleBar = (TitleBar) this.findViewById(R.id.tb_title);
		mRbxinzengbaoxiu=(RadioButton) this.findViewById(R.id.xinzengbaoxiu);
		mRbbaoxiurecord=(RadioButton) this.findViewById(R.id.baoxiurecord);
		
		mTitleBar.setTitleBarContent(getString(R.string.repair));
		mRbxinzengbaoxiu.setChecked(true);
		mTitleBar.setOnTitleBarClickListener(this);
		mTitleBar.setAddArrowShowing(false);
		
		mRbxinzengbaoxiu.setOnClickListener(mRbListener);
		mRbbaoxiurecord.setOnClickListener(mRbListener);
	}

	OnClickListener mRbListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.xinzengbaoxiu:
				break;
			case R.id.baoxiurecord:
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void initData() {

	}
	@Override
	public void onBackClick() {
		startActivity(new Intent(RepairsActivity.this,MainActivity.class));
		RepairsActivity.this.finish();
	}
	@Override
	public void onTitleClick() {
		
	}
	@Override
	public void onAddClick() {
		
	}

}
