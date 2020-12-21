package com.example.todolist.Activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.Receiver.NetworkReceiver;

import cn.bmob.v3.Bmob;
import es.dmoral.toasty.Toasty;

/**
 * 基类，用来测试Activity创建销毁状态
 * 实时获取网络状态
 */
public class BaseActivity extends AppCompatActivity {

    private boolean isRegistered = false;
    private static final String APP_ID = "0ceb0a60a0331da80f73f11072d820f4";
    private NetworkReceiver networkReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);

        //初始化
        Bmob.initialize(getApplication(),APP_ID);

        //注册网络状态监听广播
        networkReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
        registerReceiver(networkReceiver,filter);
        isRegistered = true;

        Toasty.Config.getInstance().apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        //解绑
        if(isRegistered){
            unregisterReceiver(networkReceiver);
        }
    }
}
