package com.example.todolist.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.todolist.Bean.User;
import com.example.todolist.R;

public class ScheduleActivity extends AppCompatActivity {

    private User user;
    private int tdTimes = 0;
    private int tdDuration = 0;
    private int allTimes = 0;
    private int allDuration = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);
        toolbar = (Toolbar) findViewById(R.id.schedule_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView todayDurations = (TextView)findViewById(R.id.schedule_today_durations);
        final TextView todayTimes = (TextView)findViewById(R.id.schedule_today_times);
        final TextView amountDurations = (TextView)findViewById(R.id.schedule_amount_durations);
        final TextView amountTimes = (TextView)findViewById(R.id.schedule_amount_times);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}