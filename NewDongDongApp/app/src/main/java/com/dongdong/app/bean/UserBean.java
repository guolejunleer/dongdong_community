package com.dongdong.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 账户实体类
 * author leer （http://www.dd121.com）
 * created at 2016/11/24 11:24
 */
@Entity(generateConstructors = false)
public class UserBean {
    @Id(autoincrement = true)
    private Long id;

    private String userName;
    private String passWord;

    private int index;

    public UserBean() {
    }

    public UserBean(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        UserBean bean = (UserBean) o;
        return this.userName.equals(bean.getUserName());
    }

    @Override
    public String toString() {
        return "(id:" + id + ",userName:" + userName + ",index:" + index + ")";
    }
}
