package com.example.todolist.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 数据库帮助类
 * 本地SQLite
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    //SQL语句，不可外部修改
    public static final String TODO = "create table Todos(" +
            "tid INTEGER primary key autoincrement," +
            "todotitle String," +
            "tododsc String," +
            "tododate String," +
            "todotime String," +
            "objectId String," +
            "remindTime long," +
            "remindTimeNoDay long," +
            "isAlerted int," +
            "imgId int," +
            "isRepeat int," +
            "F_tid int," +
            "isFinish int)";

    public static final String CLOCK = "create table Clock ("
            + "id integer primary key autoincrement,"
            + "objectId String,"
            + "clocktitle String,"
            + "workLength int,"
            + "shortBreak int,"
            + "longBreak int,"
            + "frequency int,"
            + "imgId int )";

    public static final String TIME = "create table timer_schedule ("
            + "_id integer primary key autoincrement,"
            + "clocktitle String,"
            + "start_time DATETIME,"
            + "end_time DATETIME,"
            + "duration INTEGER,"
            + "date_add DATE)";

    public static final String USER = "create table _User(" +
            "uid INTEGER primary key autoincrement," +
            "name TEXT," +
            "password TEXT," +
            "img TEXT)";

    private Context myContext;

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        myContext = context;
    }

    /**
     * 数据库创建
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TODO);
        db.execSQL(CLOCK);
        db.execSQL(TIME);
        db.execSQL(USER);
    }

    /**
     * 数据库更新
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Todos");
        db.execSQL("drop table if exists Clock");
        db.execSQL("drop table if exists timer_schedule");
        db.execSQL("drop table if exists _User");
        onCreate(db);
    }

}
