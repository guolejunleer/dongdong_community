package com.dongdong.app.bean;

public class PhoneBean {
	private String name;
	private String phoneNum;

	public PhoneBean() {
	}

	public PhoneBean(String name, String phoneNum) {
		this.name = name;
		this.phoneNum = phoneNum;
	}

	public String getCommonname() {
		return name;
	}

	public void setCommonname(String name) {
		this.name = name;
	}

	public String getCommonphone() {
		return phoneNum;
	}

	public void setCommonphone(String phoneNum) {
		this.phoneNum = phoneNum;
	}

}
