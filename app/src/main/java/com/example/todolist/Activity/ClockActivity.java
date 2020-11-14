package com.example.todolist.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.todolist.R;
import com.example.todolist.Service.ClockService;
import com.example.todolist.Service.FocusService;
import com.example.todolist.Utils.SPUtils;
import com.example.todolist.Utils.TimeFormatUtil;
import com.example.todolist.Widget.ClockApplication;
import com.example.todolist.Widget.ClockProgressBar;
import com.example.todolist.Widget.RippleWrapper;
import com.google.android.material.snackbar.Snackbar;
import com.jaouan.compoundlayout.RadioLayout;

import java.util.Objects;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import me.drakeet.materialdialog.MaterialDialog;

public class ClockActivity extends BaseActivity {

    //成员变量
    //布局文件里的变量
    private ClockApplication mApplication;
    private MenuItem mMenuItemIDLE;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnResume;
    private Button mBtnStop;
    private Button mBtnSkip;
    private TextView mTextCountDown;
    private TextView mTextTimeTile;
    private TextView focus_tint;
    private ClockProgressBar mProgressBar;
    private RippleWrapper mRippleWrapper;
    private long mLastClickTime = 0;
    private String clockTitle;
    private static final String KEY_FOCUS = "focus";
    private ImageView clock_bg;
    private ImageButton bt_music;
    private static int[] imageArray = new int[]{
            R.drawable.ic_img2,
            R.drawable.ic_img3,
            R.drawable.ic_img4,
            R.drawable.ic_img5,
            R.drawable.ic_img6,
            R.drawable.ic_img7,
            R.drawable.ic_img8,
            R.drawable.ic_img9,
            R.drawable.ic_img10,
            R.drawable.ic_img11,
            R.drawable.ic_img12
    };

    private static int[] hammerImageArray = new int[]{
            R.drawable.hammer_1,
            R.drawable.hammer_2,
            R.drawable.hammer_3,
            R.drawable.hammer_4,
            R.drawable.hammer_5,
            R.drawable.hammer_6,
            R.drawable.hammer_7,
            R.drawable.hammer_8,
            R.drawable.hammer_9,
            R.drawable.hammer_10
    };

    private int bg_id;
    private int workLength, shortBreak,longBreak;
    private long id;
    private RadioLayout river,rain,wave,bird,fire;

    //工作期间跳转到其他页面,默认为false
    private boolean out_flag = false;
    private boolean popDialog = false;

    /**
     * 新建跳转到MainActivity 的 intent
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    /**
     * ClockActivity 创建调用的 onCreate函数
     * 用来初始化
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化状态栏
        setStatusBar();
        //加载布局文件
        setContentView(R.layout.activity_clock);
        //获取用户点击事件传过来的 intent, intent里保存着参数，用getXXXXXExtra来获取数据
        Intent intent = getIntent();
        clockTitle = intent.getStringExtra("clocktitle");
        workLength = intent.getIntExtra("workLength",ClockApplication.DEFAULT_WORK_LENGTH);
        shortBreak = intent.getIntExtra("shortBreak",ClockApplication.DEFAULT_SHORT_BREAK);
        longBreak = intent.getIntExtra("longBreak",ClockApplication.DEFAULT_LONG_BREAK);
        id = intent.getLongExtra("id",1);

        //获取Application实例
        //Application和Activity,Service一样,是android框架的一个系统组件
        //Application可以说是单例 (singleton)模式的一个类.且application对象的生命周期是整个程序中最长的，
        //它的生命周期就等于这个程序的生命周期
        mApplication = (ClockApplication)getApplication();

        //获取其他变量实例
        mBtnStart = (Button)findViewById(R.id.btn_start);
        mBtnPause = (Button)findViewById(R.id.btn_pause);
        mBtnResume = (Button)findViewById(R.id.btn_resume);
        mBtnStop = (Button)findViewById(R.id.btn_stop);
        mBtnSkip = (Button)findViewById(R.id.btn_skip);
        mTextCountDown = (TextView)findViewById(R.id.text_count_down);
        mTextTimeTile = (TextView)findViewById(R.id.text_time_title);
        mProgressBar = (ClockProgressBar)findViewById(R.id.tick_progress_bar);
        mRippleWrapper = (RippleWrapper)findViewById(R.id.ripple_wrapper);
        focus_tint = (TextView)findViewById(R.id.focus_hint);
        bt_music = (ImageButton) findViewById(R.id.bt_music);
        clock_bg = (ImageView) findViewById(R.id.clock_bg);
        //判断是否开启白噪音
        if(isSoundOn()){
            bt_music.setEnabled(true);
            bt_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
        } else {
            bt_music.setEnabled(false);
            bt_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_off));
        }
        //放置默认的白噪音 模式
        SPUtils.put(this,"music_id",R.raw.river);
        //提示用户如何开启关闭白噪音
        Toasty.normal(this, "双击界面打开或关闭白噪音", Toast.LENGTH_SHORT).show();
        //初始化监听事件
        initActions();
        //随机改变背景图片
        initBackgroundImage();
    }

    //初始化背景图片，产生随机数来随机获取
    private void initBackgroundImage(){
        Random random = new Random();
        bg_id = imageArray[random.nextInt(11)];
        //内存优化
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true);
        //使用Glide来加载，优化内存
        Glide.with(getApplicationContext())
                .load(bg_id)
                .apply(options)
                .into(clock_bg);
    }

    /**
     * 初始化 控件的监听事件
     */
    private void initActions() {
        //开始按钮的监听事件
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ClockService.newIntent(getApplicationContext());
                i.setAction(ClockService.ACTION_START);
                i.putExtra("id",id);
                i.putExtra("clockTitle",clockTitle);
                i.putExtra("workLength",workLength);
                i.putExtra("shortBreak",shortBreak);
                i.putExtra("longBreak",longBreak);
                startService(i);//开启服务
                mApplication.start();
                updateButtons();
                updateTitle();
                updateRipple();
                if (getIsFocus(ClockActivity.this)){
                    startService(new Intent(ClockActivity.this, FocusService.class));
                    focus_tint.setVisibility(View.VISIBLE);
                }
            }
        });

        //暂停
        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ClockService.newIntent(getApplicationContext());
                i.setAction(ClockService.ACTION_PAUSE);
                i.putExtra("time_left", (String) mTextCountDown.getText());
                startService(i);

                mApplication.pause();
                updateButtons();
                updateRipple();
            }
        });

        //中断后继续
        mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ClockService.newIntent(getApplicationContext());
                i.setAction(ClockService.ACTION_RESUME);
                startService(i);

                mApplication.resume();
                updateButtons();
                updateRipple();
            }
        });

        //停止
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog exitDialog = new MaterialDialog(ClockActivity.this);
                exitDialog.setTitle("提示")
                        .setMessage("放弃后，本次番茄钟将作废")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent2 = new Intent(ClockActivity.this, MainActivity.class);
                                startActivity(intent2); //跳转到MainActivity页面
                                stopService(new Intent(ClockActivity.this, FocusService.class));
                                Glide.get(ClockActivity.this).clearMemory();
                                exitApp();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            public void onClick(View view) {
                                exitDialog.dismiss();
                            }
                        });

                exitDialog.show();
            }
        });

        //跳过按钮
        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ClockService.newIntent(getApplicationContext());
                i.setAction(ClockService.ACTION_STOP);
                startService(i);

                mApplication.skip();
                reload();
            }
        });

        //点击开关白噪音
        mRippleWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - mLastClickTime < 500) {

                    // 修改 SharedPreferences
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext()).edit();
                    if (isSoundOn()) {
                        editor.putBoolean("pref_key_tick_sound", false);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_TICK_SOUND_OFF);
                        startService(i);
                        bt_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_off));
                        bt_music.setEnabled(false);
                        Snackbar.make(view, getResources().getString(R.string.toast_tick_sound_off),
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        editor.putBoolean("pref_key_tick_sound", true);

                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_TICK_SOUND_ON);
                        startService(i);
                        bt_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
                        bt_music.setEnabled(true);
                        Snackbar.make(view, getResources().getString(R.string.toast_tick_sound_on),
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                    try {
                        editor.apply();
                    } catch (AbstractMethodError unused) {
                        editor.commit();
                    }
                    updateRipple();
                }
                mLastClickTime = clickTime;
            }
        });

        //选择白噪音
        bt_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(ClockActivity.this);
                View musicView = layoutInflater.inflate(R.layout.dialog_music, null);
                river = musicView.findViewById(R.id.sound_river);
                rain = musicView.findViewById(R.id.sound_rain);
                wave = musicView.findViewById(R.id.sound_wave);
                bird = musicView.findViewById(R.id.sound_bird);
                fire = musicView.findViewById(R.id.sound_fire);
                //流水
                river.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.river);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                //雷雨
                rain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.rain);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                //海浪
                wave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.ocean);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                //鸟叫
                bird.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.bird);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                //火焰
                fire.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.fire);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                final MaterialDialog alert = new MaterialDialog(ClockActivity.this);
                alert.setPositiveButton("关闭", new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                alert.setContentView(musicView);
                alert.setCanceledOnTouchOutside(true);
                alert.show();
            }
        });
    }

    //按住返回键 退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            final MaterialDialog exitDialog = new MaterialDialog(this);
            exitDialog.setTitle("提示")
                    .setMessage("本次番茄钟将作废，是否退出")
                    .setPositiveButton("退出", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent2 = new Intent(ClockActivity.this, MainActivity.class);
                            startActivity(intent2); //退回MainActivity页面
                            stopService(new Intent(ClockActivity.this, FocusService.class));
                            Glide.get(ClockActivity.this).clearMemory(); //优化内存
                            exitApp();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        public void onClick(View view) {
                            exitDialog.dismiss();
                        }
                    });
            exitDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        reload();//重载
        Intent intent = getIntent();
        out_flag = intent.getBooleanExtra("out_flag",false);
        if(out_flag && !popDialog){
            intent.putExtra("out_flag",false);
            popDialog = true;
            final MaterialDialog dialog = new MaterialDialog(ClockActivity.this);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View view = layoutInflater.inflate(R.layout.dialog_image,null);
            dialog.setView(view);
            ImageView imageView = view.findViewById(R.id.pop_image);
            Random random = new Random();
            Drawable drawable = getDrawable(hammerImageArray[random.nextInt(hammerImageArray.length)]);
            imageView.setBackground(drawable);
            dialog.show();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    popDialog = false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ClockActivity_test: ","onResume");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ClockService.ACTION_COUNTDOWN_TIMER);
        registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ClockActivity_test: ","onPause");
        unregisterReceiver(mIntentReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ClockActivity_test: ","onDestroy");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        releaseImageViewResouce(clock_bg);
    }

    private void reload() {
        mApplication.reload();
        mProgressBar.setMaxProgress(mApplication.getMillisInTotal() / 1000);
        mProgressBar.setProgress(mApplication.getMillisUntilFinished() / 1000);
        updateText(mApplication.getMillisUntilFinished());
        updateTitle();
        updateButtons();
        updateScene();
        updateRipple();
        updateAmount();
        if (getSharedPreferences().getBoolean("pref_key_screen_on", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    //实时更新
    //updateText 、updateTitle、updateButtons、updateScene、updateRipple、updateAmount
    private void updateText(long millisUntilFinished) {
        mTextCountDown.setText(TimeFormatUtil.formatTime(millisUntilFinished));
    }

    private void updateTitle() {
        if (mApplication.getState() == ClockApplication.STATE_FINISH) {
            String title;

            if (mApplication.getScene() == ClockApplication.SCENE_WORK) {
                title = getResources().getString(R.string.scene_title_work);
            } else {
                title = getResources().getString(R.string.scene_title_break);
            }

            mTextTimeTile.setText(title);
            mTextTimeTile.setVisibility(View.VISIBLE);
            mTextCountDown.setVisibility(View.GONE);
        } else {
            mTextTimeTile.setVisibility(View.GONE);
            mTextCountDown.setVisibility(View.VISIBLE);
        }
    }

    private void updateButtons() {
        int state = mApplication.getState();
        int scene = mApplication.getScene();
        boolean isPomodoroMode = getSharedPreferences()
                .getBoolean("pref_key_pomodoro_mode", true);

        // 在番茄模式下不能暂停定时器
        mBtnStart.setVisibility(
                state == ClockApplication.STATE_WAIT || state == ClockApplication.STATE_FINISH ?
                        View.VISIBLE : View.GONE);

        if (isPomodoroMode) {
            mBtnPause.setVisibility(View.GONE);
            mBtnResume.setVisibility(View.GONE);
        } else {
            mBtnPause.setVisibility(state == ClockApplication.STATE_RUNNING ?
                    View.VISIBLE : View.GONE);
            mBtnResume.setVisibility(state == ClockApplication.STATE_PAUSE ?
                    View.VISIBLE : View.GONE);
        }

        if (scene == ClockApplication.SCENE_WORK) {
            mBtnSkip.setVisibility(View.GONE);
            if (isPomodoroMode) {
                mBtnStop.setVisibility(!(state == ClockApplication.STATE_WAIT ||
                        state == ClockApplication.STATE_FINISH) ?
                        View.VISIBLE : View.GONE);
            } else {
                mBtnStop.setVisibility(state == ClockApplication.STATE_PAUSE ?
                        View.VISIBLE : View.GONE);
            }

        } else {
            mBtnStop.setVisibility(View.GONE);
            if (isPomodoroMode) {
                mBtnSkip.setVisibility(!(state == ClockApplication.STATE_WAIT ||
                        state == ClockApplication.STATE_FINISH) ?
                        View.VISIBLE : View.GONE);
            } else {
                mBtnSkip.setVisibility(state == ClockApplication.STATE_PAUSE ?
                        View.VISIBLE : View.GONE);
            }

        }
    }

    public void updateScene() {
        int scene = mApplication.getScene();

        ((TextView)findViewById(R.id.stage_work_value))
                .setText(getResources().getString(R.string.stage_time_unit, workLength));
        ((TextView)findViewById(R.id.stage_short_break_value))
                .setText(getResources().getString(R.string.stage_time_unit, shortBreak));
        ((TextView)findViewById(R.id.stage_long_break_value))
                .setText(getResources().getString(R.string.stage_time_unit, longBreak));

        findViewById(R.id.stage_work).setAlpha(
                scene == ClockApplication.SCENE_WORK ? 0.9f : 0.5f);
        findViewById(R.id.stage_short_break).setAlpha(
                scene == ClockApplication.SCENE_SHORT_BREAK ? 0.9f : 0.5f);
        findViewById(R.id.stage_long_break).setAlpha(
                scene == ClockApplication.SCENE_LONG_BREAK ? 0.9f : 0.5f);
    }

    private void updateRipple() {
        boolean isPlayOn = getSharedPreferences().getBoolean("pref_key_tick_sound", true);
        if (isPlayOn) {
            if (mApplication.getState() == ClockApplication.STATE_RUNNING) {
                mRippleWrapper.start();
                return;
            }
        }
        mRippleWrapper.stop();
    }

    private void updateAmount() {
        long amount = getSharedPreferences().getLong("pref_key_amount_durations", 0);
        TextView textView = (TextView)findViewById(R.id.amount_durations);
        textView.setText(getResources().getString(R.string.amount_durations, amount));
    }

    //广播监听
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ClockService.ACTION_COUNTDOWN_TIMER)) {
                String requestAction = intent.getStringExtra(ClockService.REQUEST_ACTION);

                switch (requestAction) {
                    case ClockService.ACTION_TICK:
                        long millisUntilFinished = intent.getLongExtra(
                                ClockService.MILLIS_UNTIL_FINISHED, 0);
                        mProgressBar.setProgress(millisUntilFinished / 1000);
                        updateText(millisUntilFinished);
                        break;
                    case ClockService.ACTION_FINISH:
                    case ClockService.ACTION_AUTO_START:
                        reload();
                        break;
                }
            }
        }
    };

    //获取存储实例
    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void exitApp() {
        stopService(ClockService.newIntent(getApplicationContext()));
        mApplication.exit();
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

    //判断是否开启专注模式
    private boolean getIsFocus(Context context){
        Boolean isFocus = (Boolean) SPUtils.get(context, KEY_FOCUS, false);
        return isFocus;
    }

    //判断是否开启白噪音
    private boolean isSoundOn(){
        return getSharedPreferences().getBoolean("pref_key_tick_sound", true);
    }

    public static void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}