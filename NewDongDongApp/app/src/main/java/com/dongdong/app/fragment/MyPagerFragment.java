package com.dongdong.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.base.BaseFragment;
import com.dongdong.app.ui.FileManagerActivity;
import com.dongdong.app.ui.LoginActivity;
import com.dongdong.app.ui.SettingsActivity;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.AvatarView;
import com.dongdong.app.widget.TitleBar;

public class MyPagerFragment extends BaseFragment {

	private TitleBar mTitleBar;
	private AvatarView mIvAvatar;
	private TextView mTvName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
	}

	// 第二次进入此Fragement, onResume()方法没执行,需要加入此方法来重新设置accountName
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			refreshUI();
		}
		LogUtils.i("MyPagerFragment.clazz-->>>onHiddenChanged hidden:" + hidden);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_my;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(getLayoutId(), container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
	}

	public void initView(View view) {
		mTitleBar = (TitleBar) view.findViewById(R.id.tb_title);
		mIvAvatar = (AvatarView) view.findViewById(R.id.iv_avatar);
		mTvName = (TextView) view.findViewById(R.id.tv_name);

		mTitleBar.setBackArrowShowing(false);
		mTitleBar.setAddArrowShowing(false);
		mTitleBar.setTitleBarContent(getString(R.string.main_tab_name_my));
		mIvAvatar.setOnClickListener(this);
		view.findViewById(R.id.ll_myfamily).setOnClickListener(this);
		view.findViewById(R.id.ll_myhouse).setOnClickListener(this);
		view.findViewById(R.id.ll_myvillage).setOnClickListener(this);
		view.findViewById(R.id.ll_mycaller).setOnClickListener(this);
		view.findViewById(R.id.ll_mypicture).setOnClickListener(this);
		view.findViewById(R.id.ll_settings).setOnClickListener(this);
	}

	public void refreshUI() {
		if (!DongSDKProxy.isInitedDongAccount()) {
			mTvName.setText(this.getString(R.string.login));
		} else {
			String accountName = (String) AppContext.mAppConfig.getConfigValue(
					AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
					AppConfig.KEY_USER_NAME, "");
			mTvName.setText(accountName);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_avatar:
			if (!DongSDKProxy.isInitedDongAccount()) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.ll_myfamily:
			BaseApplication.showToastShortInCenter(R.string.building);
			break;
		case R.id.ll_myhouse:
			BaseApplication.showToastShortInCenter(R.string.building);
			break;
		case R.id.ll_myvillage:
			BaseApplication.showToastShortInCenter(R.string.building);
			break;
		case R.id.ll_mycaller:
			BaseApplication.showToastShortInCenter(R.string.building);
			break;
		case R.id.ll_mypicture:
			if (Environment.MEDIA_REMOVED.equals(Environment
					.getExternalStorageState())) {
				TipDialogManager.showTipDialog(getActivity(), R.string.warn,
						R.string.OPENFILE_ERROR);
				return;
			}
			startActivity(new Intent(getActivity(), FileManagerActivity.class));
			break;
		case R.id.ll_settings:
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;
		default:
			break;
		}
	};
}
