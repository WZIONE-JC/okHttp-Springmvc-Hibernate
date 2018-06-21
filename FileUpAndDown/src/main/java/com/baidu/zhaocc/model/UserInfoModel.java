package com.baidu.zhaocc.model;

/**
 * @Auther: zhaochaochao
 * @Date: 2018/6/13
 * @Description:
 */
public class UserInfoModel {
    private String userName;
    private String password;

    public UserInfoModel() {
    }

    public UserInfoModel(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "userName :" + userName + " password:" + password;
    }
}
