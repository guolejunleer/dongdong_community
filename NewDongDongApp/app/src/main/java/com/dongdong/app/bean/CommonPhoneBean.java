package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 常用电话实体类
 */
@Entity(generateConstructors = false)
public class CommonPhoneBean {
    @Id(autoincrement = true)
    private Long id;

    private String phoneId;
    private String department;
    private String phoneNumber;
    private String address;

    public CommonPhoneBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        CommonPhoneBean object = (CommonPhoneBean) o;
        return this.phoneId.equals(object.phoneId);
    }

    @Override
    public String toString() {
        return  ",phoneId:" + phoneId + ",department:" + department +
                ",phoneNumber:" + phoneNumber + ",address:" + address;
    }
}
