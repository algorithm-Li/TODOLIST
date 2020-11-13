package com.example.todolist.Utils;

import android.content.Context;
import android.util.Log;

import com.example.todolist.Bean.Todos;
import com.example.todolist.Dao.TodoDao;

import java.util.ArrayList;
import java.util.List;

public class ToDoUtils {

    /**
     * 返回数据库用户所有的任务
     * @param context
     * @return
     */
    public static List<Todos> getAllTodos(Context context) {
        List<Todos> temp = new ArrayList<Todos>();
        List<Todos> findAll = new TodoDao(context).getAllTask();
        Log.i("ToDoUtils","任务个数" + findAll.size());
        if (findAll != null && findAll.size() > 0) {
            temp.addAll(findAll);
        }
        return temp;
    }

    /**
     * 返回数据库中Todos的F_tid是传进参数tid的todo列表
     */
    public static List<Todos> getTaskTodos(Context context,int tid){
        List<Todos> temp = new ArrayList<Todos>();
        List<Todos> findAll = new TodoDao(context).getTaskTodos(tid);
        Log.i("ToDoUtils","子任务个数" + findAll.size());
        if(findAll!=null && findAll.size()>0){
            temp.addAll(findAll);
        }
        return temp;
    }

    /**
     * 返回数据库用户所有的父任务
     * @param context
     * @return
     */
    public static List<Todos> getAllFatherTodos(Context context) {
        List<Todos> temp = new ArrayList<Todos>();
        List<Todos> findAll = new TodoDao(context).getAllFatherTodos();
        Log.i("ToDoUtils","任务个数" + findAll.size());
        if (findAll != null && findAll.size() > 0) {
            temp.addAll(findAll);
        }
        return temp;
    }

}
