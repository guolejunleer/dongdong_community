package com.dongdong.app.bean;

import com.ddclient.jnisdk.InfoUser;

public class UserInfoBean {
	private InfoUser userInfo;
	private PhoneMessBean phoneMessObject;

	public UserInfoBean(InfoUser userInfo, PhoneMessBean phoneMessObject) {
		this.userInfo = userInfo;
		if (phoneMessObject != null) {
			this.phoneMessObject = phoneMessObject;
		} else {
			this.phoneMessObject = new PhoneMessBean("", "");
		}
	}

	public InfoUser getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(InfoUser userInfo) {
		this.userInfo = userInfo;
	}

	public PhoneMessBean getPhoneMessObject() {
		return phoneMessObject;
	}

	public void setPhoneMessObject(PhoneMessBean phoneMessObject) {
		this.phoneMessObject = phoneMessObject;
	}

}
