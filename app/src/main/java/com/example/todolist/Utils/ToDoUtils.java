package com.example.todolist.Utils;

import android.content.Context;
import android.util.Log;

import com.example.todolist.Bean.Todos;
import com.example.todolist.Dao.TodoDao;

import java.util.ArrayList;
import java.util.List;

/**
 * ToDo相关操作工具类
 */
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


    /**
     * 获取并返回今天未被提醒切大于当前时间的事项
     */
    public static List<Todos> getTodayTodos(Context context){
        List<Todos> todayTodos = new ArrayList<Todos>();
        try {
            List<Todos> findAll = new TodoDao(context).getNotAlertTodos();
            if (findAll != null && findAll.size()>0){
                for (Todos todos : findAll){
                    if (todos.getRemindTime() >= System.currentTimeMillis() && isToday(todos.getRemindTime())){
                        todayTodos.add(todos);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return todayTodos;
    }

    /**
     * 判断要提醒的事项是否为今天
     */
    private static boolean isToday(long date){
        if (date/1000/60/60/24 == System.currentTimeMillis()/1000/60/60/24){
            return true;
        }
        return false;
    }

    /**
     * 将改任务设置为已被提醒
     */
    public static void setHasAlerted(Context context, int tid) {
        TodoDao todoDao = new TodoDao(context);
        Todos todos = todoDao.getOneTodo(tid);
        if (todos != null) {
            todoDao.setisAlerted(tid);
        }
    }

}
