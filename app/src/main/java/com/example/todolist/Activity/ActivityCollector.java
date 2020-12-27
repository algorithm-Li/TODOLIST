package com.example.todolist.Activity;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Activity 收集，单例类
 * @author Algotithm
 */
public class ActivityCollector {

    //单利类初始化
    private static ActivityCollector myActivity = new ActivityCollector();
    private ArrayList<Activity> myArrayList;

    //稀有构造函数
    private ActivityCollector() {
        myArrayList = new ArrayList<Activity>();
    }

    //获取实例
    public static ActivityCollector getInstance()
    {
        if(myActivity == null)
        {
            myActivity = new ActivityCollector();
        }
        return myActivity;
    }

    //添加activity
    public static void addActivity(Activity activity){
        myActivity.myArrayList.add(activity);
    }

    //删除activity
    public static void removeActivity(Activity activity){
        myActivity.myArrayList.remove(activity);
    }

    //关闭所有activity
    public static void finishAll(){
        for (Activity activity:myActivity.myArrayList) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        myActivity.myArrayList.clear();
    }
}
