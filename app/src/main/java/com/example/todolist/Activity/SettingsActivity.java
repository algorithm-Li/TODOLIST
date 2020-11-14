package com.example.todolist.Activity;


import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import com.example.todolist.Fragment.SettingsFragment;
import com.example.todolist.R;

public class SettingsActivity extends BaseActivity {

    private Toolbar toolbar;

    /**
     * 初始化
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载布局文件
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar_preference);
        //设置ActionBar
        setSupportActionBar(toolbar);
        //ActionBar不显示标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //ActionBar显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //添加Fragment的正确姿势
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }

    /**
     * 返回按钮监听
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //点击菜单上的按钮的监听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
