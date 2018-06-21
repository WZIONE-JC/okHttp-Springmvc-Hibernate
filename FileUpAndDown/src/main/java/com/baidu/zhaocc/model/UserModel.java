package com.baidu.zhaocc.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: zhaochaochao
 * @Date: 2018/6/11
 * @Description:
 */
public class UserModel {
    private String userName;
    private MultipartFile image;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
