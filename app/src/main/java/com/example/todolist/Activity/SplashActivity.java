package com.example.todolist.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.example.todolist.Bean.User;
import com.example.todolist.R;
import com.example.todolist.Receiver.NetworkReceiver;
import com.example.todolist.Service.AlarmService;
import com.example.todolist.Utils.FileUtils;
import com.example.todolist.Utils.NetWorkUtils;
import com.example.todolist.Utils.SPUtils;

import cn.bmob.v3.Bmob;
import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

/**
 * 开机动画页面
 * @author Algotithm
 */
public class SplashActivity extends BaseActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1500; // 开机时间
    private static final String APP_ID = "0ceb0a60a0331da80f73f11072d820f4";
    private NetworkReceiver networkReceiver;
    private FileUtils fileUtils;
    private static final String KEY_VIBRATE = "vibrator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        //复制assets下的资源文件到sd卡
        fileUtils = new FileUtils();
        fileUtils.copyData(getApplicationContext());
        SPUtils.put(this,"isFocus",false);

        //判断网络状态
        if (NetWorkUtils.isNetworkConnected(getApplication())) {
            Bmob.initialize(getApplication(),APP_ID);
        }

        Resources resource = this.getResources();
        startService(new Intent(this,AlarmService.class));
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new NormalDrawStrategy())//设置动画效果
                .setAppIcon(resource.getDrawable(R.drawable.ic_begin)) //图标
                .setColorOfAppName(R.color.colorPrimary) //颜色
                .setAppStatement("happy everyday！！！")//下面的语句
                .setColorOfAppStatement(R.color.colorPrimaryDark) //颜色
                .create();
        openingStartAnimation.show(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(User.getCurrentUser(User.class) == null){
                    Log.d("转跳","not null");
                    Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    Log.d("转跳","null");
                    Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        },SPLASH_DISPLAY_LENGTH);
    }
}