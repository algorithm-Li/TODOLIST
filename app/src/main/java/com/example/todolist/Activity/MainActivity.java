package com.example.todolist.Activity;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.todolist.Adapter.FragmentAdapter;
import com.example.todolist.DBHelper.MyDatabaseHelper;
import com.example.todolist.Fragment.ClockFragment;
import com.example.todolist.Fragment.TodoFragment;
import com.example.todolist.R;
import com.example.todolist.Utils.SPUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.kekstudio.dachshundtablayout.indicators.DachshundIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;
import top.wefor.circularanim.CircularAnim;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener
        , View.OnClickListener {

    private DrawerLayout drawer; //侧滑菜单
    private ImageView nav_bg; //侧滑导航栏头部图片空间
    private TextView nick_name; //用户名
    private TextView autograph; //位置时间人物说明
    private CircleImageView user_image; //用户图片

    private MyDatabaseHelper dbHelper;//数据库帮助类
    private FloatingActionButton fab; //悬浮按钮

    private DachshundTabLayout myTabLayout;
    private ViewPager myViewPager;

    private MenuItem myMenuItem;

    //是否开启专注模式 控件
    private SwitchCompat isFocus;
    private static final String KEY_FOCUS = "focus";

    //获取用户现在的应用栈
    private UsageStatsManager usageStatsManager;
    private List<UsageStats> queryUsageStats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置状态栏透明
        setStatusBar();

        //获取Toolbar实例、设置ToolBar、
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //获取ActionBar实例
        ActionBar bar = getSupportActionBar();
        if(bar!=null)
        {
            //当bar不为空时，调用setDisplayHomeAsUpEnabled让导航栏按钮显示出来
            //调用 setHomeAsUpIndicator 设置图标
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //获取DrawerLayout实例
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        //将drawer侧滑菜单绑定一个 触发器，响应事件
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //如果运行以下代码，将使原填充的菜单项无效，当用户再次访问菜单时，再次调用onCreateOptionsMenu(Menu menu)。
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                //补充
                //事件条件
                if(myMenuItem != null && newState == DrawerLayout.STATE_IDLE){
                    runNavigationItemSelected(myMenuItem);
                    myMenuItem = null;
                }

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //获取侧滑导航栏控件
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        //获取实例
        nav_bg = headerView.findViewById(R.id.nav_bg);
        nick_name = headerView.findViewById(R.id.nick_name);
        user_image = headerView.findViewById(R.id.user_image);
        autograph = headerView.findViewById(R.id.user_autograph);

        //设置监听
        user_image.setOnClickListener(this);
        nick_name.setOnClickListener(this);

        //数据库帮助类初始化
        dbHelper = new MyDatabaseHelper(this,"Data.db",null,1);
        dbHelper.getWritableDatabase();

        //动态获取权限
        initPermission();

        //初始化fab悬浮按钮
        initView();

        //初始化滑动分页
        initViewPager();
    }

    /**
     * 初始化fab悬浮按钮
     */
    private void initView() {
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    /**
     * 初始化滑动分页
     */
    private void initViewPager() {
        //获取实例
        myTabLayout = (DachshundTabLayout)findViewById(R.id.tab_layout_main);
        myViewPager = (ViewPager)findViewById(R.id.view_pager_main);
        //获取tab标题名
        List<String> tab_titles = new ArrayList<>();
        tab_titles.add(getString(R.string.tab_title_main_1));
        tab_titles.add(getString(R.string.tab_title_main_2));

        //获取Fragment
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TodoFragment(this));
        fragmentList.add(new ClockFragment());

        //限制2个
        myViewPager.setOffscreenPageLimit(2);

        //构建适配器
        FragmentAdapter myFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),fragmentList,tab_titles);
        myTabLayout.setAnimatedIndicator(new DachshundIndicator(myTabLayout));
        myViewPager.setAdapter(myFragmentAdapter);
        myTabLayout.setupWithViewPager(myViewPager);
        myTabLayout.setTabsFromPagerAdapter(myFragmentAdapter);

        myViewPager.addOnPageChangeListener(pageChangeListener);


    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 0) {
                fab.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //新建Todo画面
                        //跳转
                        CircularAnim.fullActivity(MainActivity.this, v)
                                .colorOrImageRes(R.color.colorPrimary)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        Intent intent = new Intent(MainActivity.this, NewTodoActivity.class);
                                        startActivityForResult(intent,1);
                                    }
                                });
                    }
                });
            }else if(position == 1){
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //新建Clock画面
                        //跳转
                        CircularAnim.fullActivity(MainActivity.this, v)
                                .colorOrImageRes(R.drawable.ic_img3)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        Intent intent = new Intent(MainActivity.this, NewClockActivity.class);
                                        startActivity(intent);
                                    }
                                });
                    }
                });
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                //跳转动画
                CircularAnim.fullActivity(MainActivity.this, v)
                        .colorOrImageRes(R.color.colorPrimary)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                Intent intent = new Intent(MainActivity.this, NewTodoActivity.class);
                                startActivityForResult(intent, 1);
                            }
                        });

                break;
            default:
                break;
        }
    }

    /**
     * toolbar 绑定弹出菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    /**
     * 点击toolbar上的不同菜单，响应不同事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START); //弹出侧滑菜单
                break;
            case R.id.action_settings:
                //点击设置，跳转到设置SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent,1);
                return true;
            case R.id.menu_focus:
                //点击弹出选择框
                final MaterialDialog focusDialog = new MaterialDialog(MainActivity.this);
                //加载布局文件
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View view = layoutInflater.inflate(R.layout.dialog_focus, null);
                //弹出框加载布局
                focusDialog.setView(view);
                //获取实例
                isFocus = view.findViewById(R.id.sw_focus);
                isFocus.setChecked(getIsFocus(this));
                isFocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            if (Build.VERSION.SDK_INT >= 21){
                                if (!isNoSwitch()){
                                    RequestPromission();
                                }
                            }
                            //保存数据
                            SPUtils.put(MainActivity.this, KEY_FOCUS, isChecked);
                        } else {
                            SPUtils.put(MainActivity.this, KEY_FOCUS, isChecked);
                        }
                    }
                });
                focusDialog.setTitle("专注模式");
                focusDialog.setCanceledOnTouchOutside(true);
                focusDialog.show();// 显示对话框
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 判断用户是否选择专注模式
     * @param context
     * @return
     */
    public boolean getIsFocus(Context context){
        Boolean isFocus = (Boolean) SPUtils.get(context, KEY_FOCUS, false);
        return isFocus;
    }

    /**
     * 判断“查看应用使用情况”是否开启
     * @return
     */
    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        if(Build.VERSION.SDK_INT >=21){
            //noinspection ResourceType
            usageStatsManager = (UsageStatsManager)this.getApplicationContext().getSystemService("usagestats");
            queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        }
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 跳转到“查看应用使用情况”页面
     */
    public void RequestPromission() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle("提示")
                .setMessage(String.format(Locale.US,"打开专注模式请允App查看应用的使用情况。"))
                .setPositiveButton("开启", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    /**
     * 设置状态栏透明
     */
    private void setStatusBar()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 用户点击返回键事件
     */
    @Override
    public void onBackPressed() {
        //如果有弹出侧滑菜单，则回缩侧滑菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * NavigationView头部设置监听事件
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            myMenuItem = item;
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * 根据Drawerlayout上的导航栏的按钮点击，转到不同的页面
     */
    private void runNavigationItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.nav_todo:
                myViewPager.setCurrentItem(0);
                break;
            case R.id.nav_clock:
                myViewPager.setCurrentItem(1);
                break;
            case R.id.nav_record:
                //跳转到记录页面
                //补充
                break;
            case R.id.nav_setting:
                //跳转到设置页面
                //补充
                Intent intent2 = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent2,1);
                break;
            case R.id.nav_about:
                //跳转到关于页面
                //补充
                break;
            default:
                break;
        }
    }


    /**
     * 动态获取权限，申请
     */
    private void initPermission(){
        //需要获取的权限
        //权限依次为
        /*
        1.录音权限
        2.访问网络状态权限
        3.允许程序访问网络连接，可能产生GPRS流量
        4.允许程序写入外部存储,如SD卡上写文件
        5.允许程序开机自动运行
        6.APP通知显示在状态栏权限
         */
        String[] permission = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
        };

        //保存未申请的权限到ArrayList中
        //ContextCompat.checkSelfPermission(Context context, String permission) => 检查权限
        //有权限: PackageManager.PERMISSION_GRANTED
        //无权限: PackageManager.PERMISSION_DENIED
        ArrayList<String> applyList = new ArrayList<>();

        for(String str:permission){
            if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,str)){
                applyList.add(str);
            }
        }

        //向系统申请权限
        //requestCode：返回码：123
        String[] tmpList = new String[applyList.size()];
        if(!applyList.isEmpty()){
            ActivityCompat.requestPermissions(this,applyList.toArray(tmpList),123);
        }
    }
}