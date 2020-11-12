package com.example.todolist.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * SharedPreferences存储工具类
 * 键值对存储
 * 设置文件名称："share_data"
 */
public class SPUtils {
    //不可修改变量
    private static final String KEY_VIBRATOR = "vibrator";

    //保存在手机里的文件名
    private static final String FILE_NAME = "share_data";

    /**
     * 保存不同类型的数据
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context,String key,Object object){

        SharedPreferences SP = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        if(object instanceof String) {
            editor.putString(key,(String)object);
        }else if(object instanceof Integer) {
            editor.putInt(key,(Integer)object);
        }else if(object instanceof Boolean){
            editor.putBoolean(key,(Boolean)object);
        }else if(object instanceof Float){
            editor.putFloat(key,(Float)object);
        } else if(object instanceof Long){
            editor.putLong(key,(Long)object);
        }else{
            editor.putString(key,object.toString());
        }
        //editor.apply();//提交
        SharedPreferencesCompat.apply(editor);
    }


    /**
     * 根据key值，获取相应的数据，根据数据类型的不同，调用不同的方法
     * @param context
     * @param key
     * @param object
     * @return
     */
    public static Object get(Context context,String key,Object object){
        SharedPreferences SP = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        if(object instanceof String) {
            return SP.getString(key,(String)object);
        }else if(object instanceof Integer) {
            return SP.getInt(key,(Integer)object);
        }else if(object instanceof Boolean){
            return SP.getBoolean(key,(Boolean)object);
        }else if(object instanceof Float){
            return SP.getFloat(key,(Float)object);
        } else if(object instanceof Long){
            return SP.getLong(key,(Long)object);
        }else{
            return null;
        }
    }


    /**
     * 移除数据，与key值对应
     * @param context
     * @param key
     */
    public static void remove(Context context,String key)
    {
        SharedPreferences SP = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有的数据
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences SP = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 判断查询某个key值是否存在
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context,String key){
        SharedPreferences SP = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return SP.contains(key);
    }

    /**
     * 获取所有键值对数据
     * @param context
     * @return
     */
    public static Map<String,?> getAll(Context context) {
        SharedPreferences SP = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return SP.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     * 实际上就是先调用了editor.apply()方法
     * 如果发生异常的话，则再调用editor.commit()的方法。
     * 简单说就是优先使用apply()方法。
     * 1000条数据，commit大概需要10ms左右，而apply只需要1ms
     */
    private static class SharedPreferencesCompat{
        private static final Method sApplyMethod = findApplyMethod();
        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked","rawtypes"})
        private static Method findApplyMethod(){
            try{
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            }catch (NoSuchMethodException e){
                e.printStackTrace();
            }
            return null;
        }
        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor){
            try{
                if(sApplyMethod != null){
                    sApplyMethod.invoke(editor);
                    return;
                }
            }catch (IllegalArgumentException e){

            }catch (IllegalAccessException e){

            }catch (InvocationTargetException e){

            }
            editor.commit();
        }
    }
}
