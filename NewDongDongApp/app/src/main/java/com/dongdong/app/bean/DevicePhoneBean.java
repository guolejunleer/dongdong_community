package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 设备-常用电话关系表
 */
@Entity(generateConstructors = false)
public class DevicePhoneBean {
    @Id(autoincrement = true)
    private Long id;

    private String deviceId;
    private String phoneId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    @Override
    public String toString() {
        return "deviceId:" + deviceId + ", phoneId:" + phoneId;
    }
}
