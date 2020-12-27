package com.example.todolist.Bean;

import android.graphics.Bitmap;
import cn.bmob.v3.BmobUser;

/**
 * 用户
 */
public class User extends BmobUser {

    //private BmobFile img;
    private String nickName;
    private String autograph;
    private String sex;
    private Bitmap localImg;
    private Integer total;

//    public BmobFile getImg() {
//        return img;
//    }

//    public void setImg(BmobFile img) {
//        this.img = img;
//    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Bitmap getLocalImg() {
        return localImg;
    }

    public void setLocalImg(Bitmap localImg) {
        this.localImg = localImg;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
