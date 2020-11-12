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



}
