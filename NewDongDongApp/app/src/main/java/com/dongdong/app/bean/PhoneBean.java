package com.dongdong.app.bean;

public class PhoneBean {
    private String name;
    private String phoneNum;

    public PhoneBean() {
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

    @Override
    public String toString() {
        return "(name:" + name + "phoneNum:" + phoneNum+")";
    }
}
