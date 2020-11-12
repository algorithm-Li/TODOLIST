package com.example.todolist.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class BitmapUtils {

    /**
     * 压缩图片，防止内存溢出闪退
     */
    public static Bitmap readBitMap(Context context,int resId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        //获取图片资源
        InputStream inputStream = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(inputStream,null,options);
    }
}
