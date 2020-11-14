package com.example.todolist.Utils;


import android.content.Context;
import android.util.Log;

import com.example.todolist.Bean.Tomato;
import com.example.todolist.Dao.ClockDao;

import java.util.ArrayList;
import java.util.List;

/**
 * 锤子时钟工具类
 * 利用番茄工作法
 */
public class TomatoUtils {

    /**
     * 返回数据库用户所有的番茄钟
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static List<Tomato> getAllTomato(Context context) {
        List<Tomato> temp = new ArrayList<Tomato>();
        List<Tomato> findAll = new ClockDao(context).getDbAllTomato();
        Log.i("ClockDao","番茄任务个数" + findAll.size());
        if (findAll != null && findAll.size() > 0) {
            temp.addAll(findAll);
        }
        return temp;
    }

    public interface GetTomatoCallBack {
        void onSuccess(List<Tomato> tomato);

        void onError(int errorCode, String msg);
    }

    public interface DeleteTomatoListener {
        void onSuccess();

        void onError(int errorCord, String msg);
    }
}
