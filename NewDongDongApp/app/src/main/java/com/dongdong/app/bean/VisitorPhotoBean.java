package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 访客留影实体类
 */
@Entity(generateConstructors = false)
public class VisitorPhotoBean {
    @Id(autoincrement = true)
    private Long id;

    private String photoUrl;
    private String deviceName;
    private String photoTimestamp;

    public VisitorPhotoBean() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    @Override
    public String toString() {
        return "(photoUrl:"+photoUrl+",deviceName"+deviceName+",photoTimestamp"+photoTimestamp+")";
    }
}
