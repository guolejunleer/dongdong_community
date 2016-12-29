package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 访客留影实体类
 */
@Entity(generateConstructors = false)
public class VisitorPhotoBean implements Comparable<VisitorPhotoBean> {
    @Id(autoincrement = true)
    private Long id;

    private int size;
    private String type;
    private String photoUrl;
    private String roomValue;
    private int deviceId;
    private String deviceName;
    private String photoTimestamp;
    private int userId;

    public VisitorPhotoBean() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRoomValue() {
        return roomValue;
    }

    public void setRoomValue(String roomValue) {
        this.roomValue = roomValue;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPhotoTimestamp() {
        return photoTimestamp;
    }

    public void setPhotoTimestamp(String photoTimestamp) {
        this.photoTimestamp = photoTimestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(VisitorPhotoBean v) {
        return this.getPhotoTimestamp().compareTo(v.getPhotoTimestamp());
    }

    @Override
    public boolean equals(Object o) {
        VisitorPhotoBean object = (VisitorPhotoBean) o;
        return this.getPhotoTimestamp().equals(object.getPhotoTimestamp());
    }

    @Override
    public String toString() {
        return "(photoUrl:" + photoUrl + ",deviceName" + deviceName + ",photoTimestamp" +
                photoTimestamp + ",size:" + size + ",type:" + type + ",roomValue:" + roomValue + ")";
    }
}
