package com.dongdong.app.bean;

public class PhoneMessBean {

	private String phoneNum = "";
	private String phoneName = "";

	public PhoneMessBean(String phoneName, String phoneNum) {
		this.phoneName = phoneName;
		this.phoneNum = phoneNum;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}

}
