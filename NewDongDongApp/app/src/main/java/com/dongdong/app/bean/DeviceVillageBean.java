package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 设备小区关系表
 */
@Entity(generateConstructors = false)
public class DeviceVillageBean {
    @Id(autoincrement = true)
    private Long id;

    private String deviceId;
    private String villageId;

    public DeviceVillageBean(){

    }

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

    public String getVillageId() {
        return villageId;
    }

    public void setVillageId(String villageId) {
        this.villageId = villageId;
    }

    @Override
    public String toString() {
        return ("deviceId:" + deviceId + ",villageId:" + villageId);
    }
}
