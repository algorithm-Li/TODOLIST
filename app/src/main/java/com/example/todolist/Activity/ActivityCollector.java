package com.example.todolist.Activity;

import android.app.Activity;

import java.util.ArrayList;

public class ActivityCollector {

    private static ActivityCollector myActivity = new ActivityCollector();
    private ArrayList<Activity> myArrayList;

    private ActivityCollector() {
        myArrayList = new ArrayList<Activity>();
    }

    public static ActivityCollector getInstance()
    {
        if(myActivity == null)
        {
            myActivity = new ActivityCollector();
        }
        return myActivity;
    }

    public static void addActivity(Activity activity){
        myActivity.myArrayList.add(activity);
    }

    public static void removeActivity(Activity activity){
        myActivity.myArrayList.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity:myActivity.myArrayList) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        myActivity.myArrayList.clear();
    }
}
