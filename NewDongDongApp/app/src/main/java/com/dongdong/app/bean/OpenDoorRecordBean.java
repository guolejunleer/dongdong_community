package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * 开门记录实体类
 */
@Entity(generateConstructors = false)
public class OpenDoorRecordBean implements Comparable<OpenDoorRecordBean>, Serializable {
    @Id(autoincrement = true)
    private Long id;

    private String deviceId;   //设备ID
    private String roomId; //房号ID
    private String userId; //用户ID
    private String comNumber; // 卡号/电话号
    private String type;  //开门类型
    private String timestamp; //开门时间
    private String deviceName; //设备名称
    private String roomNumber; //房号
    private String memberName; //用户名
    private String mobilePhone; // 手机号
    private String idNumber; //身份证号

    public OpenDoorRecordBean() {

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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComNumber() {
        return comNumber;
    }

    public void setComNumber(String comNumber) {
        this.comNumber = comNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @Override
    public int compareTo(OpenDoorRecordBean o) {
        if (o != null) {
            return this.getTimestamp().compareTo(o.getTimestamp());
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        OpenDoorRecordBean object = (OpenDoorRecordBean) o;
        return this.timestamp.equals(object.timestamp);
    }

    @Override
    public String toString() {
        return "(deviceId:" + deviceId + ",roomId:" + roomId + ",userId:" + userId +
                ",comNumber:" + comNumber + ",type:" + type + ",timestamp:" + timestamp +
                ",deviceName:" + deviceName + ",roomNumber:" + roomNumber + ",memberName:" +
                memberName + ",mobilePhone:" + mobilePhone + ",idNumber" + idNumber + ")";
    }
}
