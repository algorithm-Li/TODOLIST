package com.example.todolist.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.todolist.Activity.AppsActivity;
import com.example.todolist.Activity.ShowTimeActivity;
import com.example.todolist.Activity.WeekStats;
import com.example.todolist.R;

/**
 * 三个选项，选择显示应用使用时间项目
 * @author Algorithm
 */
public class ShowTimeFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout screenTime;
    private RelativeLayout weekstats;
    private RelativeLayout appstats;

    public ShowTimeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_time,container,false);
        screenTime = (RelativeLayout)view.findViewById(R.id.screen_time);
        weekstats = (RelativeLayout)view.findViewById(R.id.weekstats);
        appstats = (RelativeLayout)view.findViewById(R.id.appstats);

        screenTime.setOnClickListener(this);
        weekstats.setOnClickListener(this);
        appstats.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.screen_time:
                Intent intent = new Intent(getContext(), ShowTimeActivity.class);
                startActivity(intent);
                break;
            case R.id.appstats:
                Intent intent1 = new Intent(getContext(), AppsActivity.class);
                startActivity(intent1);
                break;
            case R.id.weekstats:
                Intent intent2 = new Intent(getContext(), WeekStats.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}