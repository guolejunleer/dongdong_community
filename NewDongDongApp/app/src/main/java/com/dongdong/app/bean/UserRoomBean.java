package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 用户-设备-房间关系表
 */
@Entity(generateConstructors = false)
public class UserRoomBean {
    @Id(autoincrement = true)
    private Long id;

    private int deviceId;
    private int userId;
    private int roomId;

    public UserRoomBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "UserRoomBean{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", userId=" + userId +
                ", roomId=" + roomId +
                '}';
    }
}
