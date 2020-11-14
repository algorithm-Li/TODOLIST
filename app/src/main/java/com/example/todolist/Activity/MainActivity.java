package com.example.todolist.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.todolist.Adapter.FragmentAdapter;
import com.example.todolist.Bean.Todos;
import com.example.todolist.DBHelper.MyDatabaseHelper;
import com.example.todolist.Dao.TodoDao;
import com.example.todolist.Fragment.ClockFragment;
import com.example.todolist.Fragment.TodoFragment;
import com.example.todolist.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.kekstudio.dachshundtablayout.indicators.DachshundIndicator;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
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
                fab.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //新建Clock画面
                        //跳转
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CircularAnim.fullActivity(MainActivity.this, v)
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
                //调用相应事件
                //补充
                Toast.makeText(this,"You clicked 设置!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_focus:
                //调用相应事件
                //补充
                Toast.makeText(this,"You clicked 专注模式!",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
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