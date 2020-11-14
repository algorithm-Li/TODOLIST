package com.example.todolist.Activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.todolist.Bean.Tomato;
import com.example.todolist.DBHelper.MyDatabaseHelper;
import com.example.todolist.R;
import com.example.todolist.Utils.SPUtils;
import com.example.todolist.Utils.SeekBarPreference;
import com.example.todolist.Widget.ClockApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lai.library.ButtonStyle;

import java.util.ArrayList;
import java.util.Random;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * 新建待办事项类
 */
public class NewClockActivity extends BaseActivity {

    private MyDatabaseHelper dbHelper;
    private String clockTitle;
    private FloatingActionButton fab_ok;
    private EditText nv_clock_title;
    private static final String TAG = "NewClockActivity";
    private Toolbar toolbar;
    private ImageView new_bg;
    private static int[] imageArray = new int[]{R.drawable.c_img1,
            R.drawable.c_img2,
            R.drawable.c_img3,
            R.drawable.c_img4,
            R.drawable.c_img5,
            R.drawable.c_img6,
            R.drawable.c_img7,};
    private int imgId;
    private int workLength, shortBreak,longBreak,frequency;
    SQLiteDatabase db;
    private Tomato tomato;
    private long id;
    private MaterialDialog voice;
    private TextView voice_result;
    private Button mic_clock;
    private boolean enableOffline = true;
    private ButtonStyle done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_new_clock);
        toolbar = (Toolbar) findViewById(R.id.new_clock_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = new MyDatabaseHelper(NewClockActivity.this, "Data.db", null, 1);
        db = dbHelper.getWritableDatabase();
        initPermission();
        initView();
        initClick();
        initHeadImage();
    }

    private void initView() {
        nv_clock_title = (EditText) findViewById(R.id.new_clock_title);
        fab_ok = (FloatingActionButton) findViewById(R.id.fab_clock);
        new_bg = (ImageView) findViewById(R.id.new_clock_bg);
        mic_clock = (Button) findViewById(R.id.mic_clock);
    }

    private void initHeadImage(){
        Random random = new Random();
        imgId = imageArray[random.nextInt(7)];
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true);
        Glide.with(this)
                .load(imgId)
                .apply(options)
                .into(new_bg);
    }

    private void initClick() {
        Resources res = getResources();
        // 工作时长
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_work_length))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_work_length_value))
                .setMax(res.getInteger(R.integer.pref_work_length_max))
                .setMin(res.getInteger(R.integer.pref_work_length_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress((int) SPUtils
                        .get(NewClockActivity.this,"pref_key_work_length", ClockApplication.DEFAULT_WORK_LENGTH))
                .build();
        // 短时休息
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_short_break))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_short_break_value))
                .setMax(res.getInteger(R.integer.pref_short_break_max))
                .setMin(res.getInteger(R.integer.pref_short_break_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress((int)SPUtils
                        .get(NewClockActivity.this,"pref_key_short_break", ClockApplication.DEFAULT_SHORT_BREAK))
                .build();
        // 长时休息
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_long_break))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_long_break_value))
                .setMax(res.getInteger(R.integer.pref_long_break_max))
                .setMin(res.getInteger(R.integer.pref_long_break_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress((int)SPUtils
                        .get(NewClockActivity.this,"pref_key_long_break", ClockApplication.DEFAULT_LONG_BREAK))
                .build();
        // 长时休息间隔
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_long_break_frequency))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_long_break_frequency_value))
                .setMax(res.getInteger(R.integer.pref_long_break_frequency_max))
                .setMin(res.getInteger(R.integer.pref_long_break_frequency_min))
                .setUnit(R.string.pref_title_frequency_value)
                .setProgress((int)SPUtils
                        .get(NewClockActivity.this,"pref_key_long_break_frequency", ClockApplication.DEFAULT_LONG_BREAK_FREQUENCY))
                .build();

        //设置完成
        fab_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clockTitle = nv_clock_title.getText().toString();
                workLength = (int) SPUtils
                        .get(NewClockActivity.this, "pref_key_work_length", ClockApplication.DEFAULT_WORK_LENGTH);
                shortBreak = (int) SPUtils
                        .get(NewClockActivity.this, "pref_key_short_break", ClockApplication.DEFAULT_SHORT_BREAK);
                longBreak = (int) SPUtils
                        .get(NewClockActivity.this, "pref_key_long_break", ClockApplication.DEFAULT_LONG_BREAK);
                frequency = (int) SPUtils
                        .get(NewClockActivity.this, "pref_key_long_break_frequency", ClockApplication.DEFAULT_LONG_BREAK_FREQUENCY);
                tomato = new Tomato();
                tomato.setTitle(clockTitle);
                tomato.setWorkLength(workLength);
                tomato.setShortBreak(shortBreak);
                tomato.setLongBreak(longBreak);
                tomato.setFrequency(frequency);
                tomato.setImgId(imgId);
                ContentValues values = new ContentValues();
                values.put("clocktitle", clockTitle);
                values.put("workLength", workLength);
                values.put("shortBreak", shortBreak);
                values.put("longBreak", longBreak);
                values.put("frequency", frequency);
                values.put("imgId", imgId);
                id = db.insert("Clock",null,values);
                Intent intent = new Intent(NewClockActivity.this, ClockActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("clocktitle",clockTitle);
                intent.putExtra("workLength", workLength);
                intent.putExtra("shortBreak", shortBreak);
                intent.putExtra("longBreak", longBreak);
                startActivity(intent);
                finish();
            }
        });

        mic_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showVoiceDialog();
                //start();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /*动态权限申请*/
    private void initPermission() {
        String[] permission = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> applyList = new ArrayList<>();

        for (String per : permission) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, per)) {
                applyList.add(per);
            }
        }

        String[] tmpList = new String[applyList.size()];
        if (!applyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, applyList.toArray(tmpList), 123);
        }
    }

    /**
     * 返回按钮监听
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //设置状态栏
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
}
