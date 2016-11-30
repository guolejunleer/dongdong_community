package com.dongdong.app.ui;

import com.dd121.community.R;
import com.dongdong.app.fragment.HomePagerFragment;
import com.dongdong.app.fragment.MyPagerFragment;

public enum MainTab {

	HOEM(0, R.string.main_tab_name_home, R.mipmap.test_001,
			HomePagerFragment.class),

	MY(1, R.string.main_tab_name_my, R.mipmap.test_001, MyPagerFragment.class);
	private int idx;
	private int resName;
	private int resIcon;
	private Class<?> clz;

	private MainTab(int idx, int resName, int resIcon, Class<?> clz) {
		this.idx = idx;
		this.resName = resName;
		this.resIcon = resIcon;
		this.clz = clz;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getResName() {
		return resName;
	}

	public void setResName(int resName) {
		this.resName = resName;
	}

	public int getResIcon() {
		return resIcon;
	}

	public void setResIcon(int resIcon) {
		this.resIcon = resIcon;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}
}
