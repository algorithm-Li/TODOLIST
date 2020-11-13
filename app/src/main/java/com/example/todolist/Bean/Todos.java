package com.example.todolist.Bean;

import java.util.Date;

public class Todos {

    //数据
    private String title;
    private String dsc;
    private String date;
    private String time;
    private int tid,isAlerted,isRepeat,imgId;
    private long remindTime,remindTimeNoDay;
    private String dbObjectId;
    private int F_tid;
    private int isFinish;

    //构造函数
    public Todos() {

    }

    public Todos(String title, String dsc, String date, String time, String dbObjectId,
                 long remindTime,long remindTimeNoDay, int isAlerted,
                 int imgId, int isRepeat,int F_tid,int isFinish){
        this.title = title;
        this.dsc = dsc;
        this.date = date;
        this.time = time;
        this.dbObjectId = dbObjectId;
        this.remindTime = remindTime;
        this.remindTimeNoDay = remindTimeNoDay;
        this.isAlerted = isAlerted;
        this.imgId = imgId;
        this.isRepeat = isRepeat;
        this.F_tid = F_tid;
        this.isFinish = isFinish;
    }

    public Todos(String title, String dsc, String date, String time,
                 Long remindTime, int imgId, int isRepeat,int isAlerted){
        this.title = title;
        this.dsc = dsc;
        this.date = date;
        this.time = time;
        this.imgId = imgId;
        this.isRepeat = isRepeat;
        this.isAlerted = isAlerted;
        this.remindTime = remindTime;
    }

    //构造函数，补充
    public Todos(String title,String dsc,String date,String time,int imgId,int isRepeat){
        this.title = title;
        this.dsc = dsc;
        this.date = date;
        this.time = time;
        this.imgId = imgId;
        this.isRepeat = isRepeat;
    }

    public String getDbObjectId() {
        return dbObjectId;
    }

    public void setDbObjectId(String dbObjectId) {
        this.dbObjectId = dbObjectId;
    }

    public long getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(long remindTime) {
        this.remindTime = remindTime;
    }

    public long getRemindTimeNoDay() {
        return remindTimeNoDay;
    }

    public void setRemindTimeNoDay(long remindTimeNoDay) {
        this.remindTimeNoDay = remindTimeNoDay;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String desc) {
        this.dsc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getIsAlerted() {
        return isAlerted;
    }

    public void setIsAlerted(int isAlerted) {
        this.isAlerted = isAlerted;
    }

    public int getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(int isRepeat) {
        this.isRepeat = isRepeat;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getF_tid() {
        return F_tid;
    }

    public void setF_tid(int f_tid) {
        F_tid = f_tid;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

}
