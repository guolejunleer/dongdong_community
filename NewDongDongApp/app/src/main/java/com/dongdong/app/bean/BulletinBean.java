package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 物业公告实体类
 */
@Entity(generateConstructors = false)
public class BulletinBean implements Comparable<BulletinBean> {
    @Id(autoincrement = true)
    private Long id;

    private String notice;
    private String created;
    private String title;
    private String noticeId;
    private String deviceId;
    private String villageId;

    public BulletinBean() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
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
    public int compareTo(BulletinBean b) {
        if (b != null) {
            return this.getCreated().compareTo(b.getCreated());
        }
        return 0;
    }

    @Override
    public String toString() {
        return "id is " + id + "notice is " + notice + ";created is "
                + created + ";title is " + title + ";noticeId is" + noticeId
                + ";deviceId is" + deviceId + ",villageId is " + villageId;
    }
}
