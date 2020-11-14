package com.example.todolist.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.todolist.Bean.Todos;
import com.example.todolist.Dao.TodoDao;
import com.example.todolist.R;
import com.example.todolist.Service.AlarmService;
import com.example.todolist.Utils.PermissionPageUtils;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import me.drakeet.materialdialog.MaterialDialog;

public class NewTodoActivity extends BaseActivity {

    private Toolbar toolbar;
    private Calendar ca;
    private int mYear,mMonth,mDay;//当前日期
    private int mHour,mMin;//当前时间

    //布局文件控件变量
    private FloatingActionButton fab_ok;
    private TextView nv_todo_date,nv_todo_time,voice_result;
    private EditText nv_todo_title,nv_todo_dsc;
    private Switch nv_repeat;
    private ImageView new_bg;
    private FABProgressCircle fabProgressCircle;
    private Button mic_title,mic_dsc;

    //间接使用变量
    private String todoTitle,todoDsc;
    private String todoDate = null, todoTime = null;
    private int isRepeat = 0;
    private long remindTime;
    private int F_tid = 0;
    private int isFinish = 0;
    private int isAlerted = 0;

    //数据存储
    private Todos todos;

    //图片资源
    private static int[] imageArray = new int[]{R.drawable.img_1,
            R.drawable.img_2,
            R.drawable.img_3,
            R.drawable.img_4,
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_7,
            R.drawable.img_8,};
    private int imgId;

    //权限工具类
    private PermissionPageUtils permissionPageUtils;

    //是否是点击编辑功能进来的
    private boolean isEdit = false;
    private int tid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置状态栏
        setStatusBar();
        //加载布局文件
        setContentView(R.layout.activity_new_todo);
        //获取Toolbar实例、设置ToolBar、
        toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        setSupportActionBar(toolbar);
        //去除Toolbar标题
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //返回按钮显示出来
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //初始化日期实例
        ca = Calendar.getInstance();
        //获取年月日
        getDate();
        //获取小时分钟
        getTime();
        //检测是否是点击编辑按钮进来，获取数据
        initChecked();
        //初始化权限，检测申请权限
        initPermission();
        //初始化布局文件里的控件，设定监听器
        initView();
        //初始化imgId
        initHeadImage();
        //查询权限
        checkNotificationPermission();
    }

    private void initView() {

        //获取实例
        fab_ok = (FloatingActionButton) findViewById(R.id.fab_ok);
        nv_todo_title = (EditText) findViewById(R.id.new_todo_title);
        nv_todo_dsc = (EditText) findViewById(R.id.new_todo_dsc);
        nv_todo_date = (TextView) findViewById(R.id.new_todo_date);
        nv_todo_time = (TextView) findViewById(R.id.new_todo_time);
        nv_repeat = (Switch) findViewById(R.id.repeat);
        new_bg = (ImageView) findViewById(R.id.new_bg);
        fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        mic_title = (Button) findViewById(R.id.mic_title);
        mic_dsc = (Button) findViewById(R.id.mic_dsc);
        Todos dataTodo;
        //如果是编辑操作
        if(isEdit){
            dataTodo = new TodoDao(NewTodoActivity.this).getOneTodo(tid);
            nv_todo_title.setText(dataTodo.getTitle()); todoTitle = dataTodo.getTitle();
            nv_todo_dsc.setText(dataTodo.getDsc()); todoDsc = dataTodo.getDsc();
            nv_todo_date.setText(dataTodo.getDate()); todoDate = dataTodo.getDate();
            nv_todo_time.setText(dataTodo.getTime()); todoTime = dataTodo.getTime();
            if(dataTodo.getIsRepeat() == 1){
                nv_repeat.setChecked(true);
            }else{
                nv_repeat.setChecked(false);
            }
            isRepeat = dataTodo.getIsRepeat();

            isAlerted = dataTodo.getIsAlerted();
            isFinish = dataTodo.getIsFinish();
            F_tid = dataTodo.getF_tid();
        }


        //弹出日期选择器
        nv_todo_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //构建一个日期对话框，该对话框已经集成了日期选择器
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewTodoActivity.this, onDateSetListener, mYear, mMonth, mDay);
                //设置可以取消
                datePickerDialog.setCancelable(true);
                //设置点击框外取消
                datePickerDialog.setCanceledOnTouchOutside(true);
                //把日期对话框显示在界面上
                datePickerDialog.show();
            }
        });

        //弹出时间选择器
        nv_todo_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewTodoActivity.this, onTimeSetListener, mHour,mMin, true);
                timePickerDialog.setCancelable(true);
                timePickerDialog.setCanceledOnTouchOutside(true);
                timePickerDialog.show();
            }
        });

        //toolbar点击返回图标，销毁activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//销毁
            }
        });

        //获取 用户是否选择 重复提醒
        nv_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    isRepeat = 1;
                } else {
                    isRepeat = 0;
                }
            }
        });

        //语音输入标题功能
        mic_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //补充
            }
        });
        //语音输入内容功能
        mic_dsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //补充
            }
        });


        fab_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (todoDate==null){
                    Toasty.info(NewTodoActivity.this, "没有设置日期", Toast.LENGTH_SHORT, true).show();
                } else if (todoTime==null) {
                    Toasty.info(NewTodoActivity.this, "没有设置提醒时间", Toast.LENGTH_SHORT, true).show();

                } else {
                    fabProgressCircle.show();
                    todoTitle = nv_todo_title.getText().toString();
                    todoDsc = nv_todo_dsc.getText().toString();
                    Calendar calendarTime = Calendar.getInstance();
                    calendarTime.setTimeInMillis(System.currentTimeMillis());
                    calendarTime.set(Calendar.YEAR, mYear);
                    calendarTime.set(Calendar.MONTH, mMonth);
                    calendarTime.set(Calendar.DAY_OF_MONTH, mDay);
                    calendarTime.set(Calendar.HOUR_OF_DAY, mHour);
                    calendarTime.set(Calendar.MINUTE, mMin);
                    calendarTime.set(Calendar.SECOND, 0);
                    remindTime = calendarTime.getTimeInMillis();
                    Log.i("TODOOO", "时间是"+String.valueOf(remindTime));
                    todos = new Todos();
                    todos.setTitle(todoTitle);
                    todos.setDsc(todoDsc);
                    todos.setDate(todoDate);
                    todos.setTime(todoTime);
                    todos.setRemindTime(remindTime);
                    todos.setIsAlerted(isAlerted);
                    todos.setIsRepeat(isRepeat);
                    todos.setImgId(imgId);
                    todos.setF_tid(F_tid);
                    todos.setIsFinish(isFinish);

                    //如果是编辑操作
                    if(isEdit){
                        //todos.setTid(tid);
                        //更新数据
                        Log.d("update","更新成功");
                        new TodoDao(getApplicationContext()).updateOneTodo(tid,todos);
                    }else{
                        //插入数据
                        new TodoDao(getApplicationContext()).create(todos);
                    }
                    //后台服务
                    startService(new Intent(NewTodoActivity.this, AlarmService.class));
                    finish();
                    }
                }
        });
    }

    /**
     * 获取是否是 编辑已存在数据状态
     */
    public void initChecked(){
        isEdit = getIntent().getBooleanExtra("isEdit",false);
        tid = getIntent().getIntExtra("tid",0);
    }

    /**
     * 日期选择器对话框监听
     * 一旦点击日期对话框上的确定按钮，就会触发监听器的onDateSet方法
     */
    public DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mYear = year;
            mMonth = month;
            mDay = dayOfMonth;
            todoDate = year+ "年"+(month + 1) + "月" + dayOfMonth + "日";
            nv_todo_date.setText(todoDate);
        }
    };

    /**
     * 时间选择对话框监听
     * 一旦点击时间选择对话框上的确定按钮，就会触发监听器的onTimeSet方法
     */
    public TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            mHour = hour;
            mMin = minute;
            if (minute < 10){
                todoTime = hour + ":" + "0" + minute;
            } else {
                todoTime = hour + ":" + minute;
            }
            nv_todo_time.setText(todoTime);
        }
    };


    /**
     * 获取日期
     */
    private void getDate(){
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取时间
     */
    private void getTime(){
        mHour = ca.get(Calendar.HOUR_OF_DAY);
        mMin = ca.get(Calendar.MINUTE);
    }

    private void initHeadImage(){
        //产生一个随机数
        //加载画面
        Random random = new Random();
        imgId = imageArray[random.nextInt(8)];
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true);
        Glide.with(getApplicationContext())
                .load(imgId)
                .apply(options)
                .into(new_bg);

    }


    /**
     * 设置状态栏透明
     */
    private void setStatusBar(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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

    /*动态权限申请*/
    private void initPermission() {
        /*
        1.允许程序录制声音通过手机或耳机的麦克
        2.允许程序获取网络信息状态，如当前的网络连接是否有效
        3.允许程序访问网络连接，可能产生GPRS流量
        4.允许程序写入外部存储,如SD卡上写文件
         */
        String permission[] = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ArrayList<String> applyList = new ArrayList<>();

        //保存未申请的权限到ArrayList中
        //ContextCompat.checkSelfPermission(Context context, String permission) => 检查权限
        //有权限: PackageManager.PERMISSION_GRANTED
        //无权限: PackageManager.PERMISSION_DENIED
        for (String per : permission) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, per)) {
                applyList.add(per);
            }
        }

        //向系统申请权限
        //requestCode：返回码：123
        String tmpList[] = new String[applyList.size()];
        if (!applyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, applyList.toArray(tmpList), 123);
        }
    }

    private void checkNotificationPermission(){
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplication());
        boolean isOpened = manager.areNotificationsEnabled();
        permissionPageUtils = new PermissionPageUtils(this);
        //适配方式 1
        if (!isOpened){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final MaterialDialog check = new MaterialDialog(this);
                check.setTitle("提示");
                check.setMessage("未开启通知权限，将会影响待办提醒功能，请手动开启");
                check.setPositiveButton("开启", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", getPackageName());
                        intent.putExtra("app_uid", getApplicationInfo().uid);
                        startActivity(intent);
                        check.dismiss();
                    }
                });
                check.setCanceledOnTouchOutside(true);
                check.show();

            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                final MaterialDialog check = new MaterialDialog(this);
                check.setTitle("提示");
                check.setMessage("未开启通知权限，将会影响待办提醒功能，请手动开启");
                check.setPositiveButton("开启", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        check.dismiss();
                    }
                });
                check.setCanceledOnTouchOutside(true);
                check.show();
            }
        }
    }
}