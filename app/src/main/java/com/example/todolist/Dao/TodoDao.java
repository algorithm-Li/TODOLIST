package com.example.todolist.Dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.todolist.Bean.Todos;
import com.example.todolist.DBHelper.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 待办事项本地数据库操作
 */
public class TodoDao {
    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase database;

    public TodoDao(Context context){
        myDatabaseHelper = new MyDatabaseHelper(context.getApplicationContext(),"Data.db",null,1);
    }

    public void open() throws SQLException{
        database = myDatabaseHelper.getWritableDatabase();
    }

    public void close(){
       database.close();
    }

    /**
     * 根据传入的Todo Bean，插入数据，并返回记录ID
     */
    public long create(Todos todos){
        this.open();
        ContentValues values = new ContentValues();
        values.put("todotitle",todos.getTitle());
        values.put("tododsc",todos.getDsc());
        values.put("tododate",todos.getDate());
        values.put("todotime",todos.getTime());
        values.put("remindTime",todos.getRemindTime());
        values.put("remindTimeNoDay",todos.getRemindTimeNoDay());
        values.put("isAlerted",todos.getIsAlerted());
        values.put("isRepeat",todos.getIsRepeat());
        values.put("imgId",todos.getImgId());
        values.put("F_tid",todos.getF_tid());
        values.put("isFinish",todos.getIsFinish());
        long tid = database.insert("Todos",null,values);
        close();
        Log.d("texxt", Long.toString(tid));
        return tid;
    }

    /**
     * 获取并返回所有未被提醒的事项
     */
    public List<Todos> getNotAlertTodos(){
        open();
        List<Todos> allTodos = new ArrayList<Todos>();
        //执行SQL，获取数据
        Cursor cursor = database.query("Todos",null,"isAlerted = ?",
                new String[]{"0"},null,null,"remindTime");
        while(cursor.moveToNext()){
            Todos data = new Todos();
            data.setTid(cursor.getInt(cursor.getColumnIndex("tid")));
            data.setTitle(cursor.getString(cursor.getColumnIndex("todotitle")));
            data.setDsc(cursor.getString(cursor.getColumnIndex("tododsc")));
            data.setDate(cursor.getString(cursor.getColumnIndex("tododate")));
            data.setTime(cursor.getString(cursor.getColumnIndex("todotime")));
            data.setRemindTime(cursor.getLong(cursor.getColumnIndex("remindTime")));
            data.setRemindTimeNoDay(cursor.getLong(cursor.getColumnIndex("remindTimeNoDay")));
            data.setIsAlerted(cursor.getInt(cursor.getColumnIndex("isAlerted")));
            data.setIsRepeat(cursor.getInt(cursor.getColumnIndex("isRepeat")));
            data.setImgId(cursor.getInt(cursor.getColumnIndex("imgId")));
            data.setF_tid(cursor.getInt(cursor.getColumnIndex("F_tid")));
            data.setIsFinish(cursor.getInt(cursor.getColumnIndex("isFinish")));
            allTodos.add(data);
        }
        cursor.close();
        close();
        return allTodos;
    }

    /**
     * 获取所有task
     * @return
     */
    public List<Todos> getAllTask() {
        open();
        List<Todos> todosList = new ArrayList<Todos>();
        Cursor cursor= database.rawQuery("SELECT * FROM Todos", null);
        while(cursor.moveToNext()) {
            Todos data = new Todos();
            data.setTid(cursor.getInt(cursor.getColumnIndex("tid")));
            data.setTitle(cursor.getString(cursor.getColumnIndex("todotitle")));
            data.setDsc(cursor.getString(cursor.getColumnIndex("tododsc")));
            data.setDate(cursor.getString(cursor.getColumnIndex("tododate")));
            data.setTime(cursor.getString(cursor.getColumnIndex("todotime")));
            data.setRemindTime(cursor.getLong(cursor.getColumnIndex("remindTime")));
            data.setRemindTimeNoDay(cursor.getLong(cursor.getColumnIndex("remindTimeNoDay")));
            data.setIsAlerted(cursor.getInt(cursor.getColumnIndex("isAlerted")));
            data.setIsRepeat(cursor.getInt(cursor.getColumnIndex("isRepeat")));
            data.setImgId(cursor.getInt(cursor.getColumnIndex("imgId")));
            data.setDbObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
            data.setF_tid(cursor.getInt(cursor.getColumnIndex("F_tid")));
            data.setIsFinish(cursor.getInt(cursor.getColumnIndex("isFinish")));
            todosList.add(data);
        }
        cursor.close();
        close();
        Log.i("ToDoDao", "查询到本地的任务个数：" + todosList.size());
        return todosList;
    }

    /**
     * 设置待办事项为已提醒
     * @param tid
     */
    public void setisAlerted(int tid){
        open();
        Log.i("ToDoDao", "数据已更新");
        ContentValues values = new ContentValues();
        values.put("isAlerted", 1);
        Log.i("ToDoDao", String.valueOf(tid));
        database.update("Todos", values, "id = ?", new String[]{tid + ""});
        close();
    }

    /**
     * 设置待办事项已完成
     * @param tid
     */
    public void setisFinish(int tid){
        open();
        Log.i("ToDoDao", "数据已更新");
        ContentValues values = new ContentValues();
        values.put("isFinish", 1);
        Log.i("ToDoDao", String.valueOf(tid));
        database.update("Todos", values, "id = ?", new String[]{tid + ""});
        close();
    }

    /**
     * 保存多个
     * @param list
     */
    public void saveAll(List<Todos> list) {
        for (Todos todos : list) {
            create(todos);
        }
    }

    /**
     * 清空表数据
     */
    public void clearAll() {
        open();
        database.execSQL("delete from Todos");
        database.execSQL("update sqlite_sequence set seq = 0 where name = 'Todos' ");
        close();
    }

}
