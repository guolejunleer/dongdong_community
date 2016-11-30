package com.dongdong.app.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.dongdong.app.interf.BaseFragmentInterface;

public class BaseFragment extends Fragment implements OnClickListener,
		BaseFragmentInterface {

	protected LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mInflater = inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	protected int getLayoutId() {
		return 0;
	}

	protected View inflateView(int resId) {
		return this.mInflater.inflate(resId, null);
	}

	@Override
	public void initView(View view) {
	}

	@Override
	public void initData() {
	}

	@Override
	public void onClick(View v) {
	}

}
