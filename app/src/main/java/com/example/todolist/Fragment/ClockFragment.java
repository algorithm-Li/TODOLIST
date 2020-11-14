package com.example.todolist.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Activity.ClockActivity;
import com.example.todolist.Adapter.ClockRecyclerViewAdapter;
import com.example.todolist.Bean.Tomato;
import com.example.todolist.R;
import com.example.todolist.SpacesItemDecoration;
import com.example.todolist.Utils.ClockItemTouchHelperCallback;
import com.example.todolist.Utils.RecyclerItemClickListener;
import com.example.todolist.Utils.SPUtils;
import com.example.todolist.Utils.TomatoUtils;

import java.util.ArrayList;
import java.util.List;

public class ClockFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private ClockRecyclerViewAdapter clockRecyclerViewAdapter;
    private List<Tomato> clockList = new ArrayList<>();
    private LinearLayoutManager layout;
    private List<Tomato> localTomato;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    private int workLength, shortBreak,longBreak,frequency;
    private String clockTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();//获取上下文
    }

    //初始化
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clock, container, false);
        layout = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.clock_recycler_view);
        clockRecyclerViewAdapter = new ClockRecyclerViewAdapter(clockList, getActivity());
        recyclerView.setLayoutManager(layout);
        recyclerView.addItemDecoration(new SpacesItemDecoration(0));
        recyclerView.setAdapter(clockRecyclerViewAdapter);

        //Recyclerview 上 item的点击事件
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //加载数据
                clockTitle = clockList.get(clockRecyclerViewAdapter.getItemCount()-1-position).getTitle();
                workLength = clockList.get(clockRecyclerViewAdapter.getItemCount()-1-position).getWorkLength();
                shortBreak = clockList.get(clockRecyclerViewAdapter.getItemCount()-1-position).getShortBreak();
                longBreak = clockList.get(clockRecyclerViewAdapter.getItemCount()-1-position).getLongBreak();
                frequency = clockList.get(clockRecyclerViewAdapter.getItemCount()-1-position).getFrequency();

                //保存数据
                SPUtils.put(context,"pref_key_work_length", workLength);
                SPUtils.put(context,"pref_key_short_break", shortBreak);
                SPUtils.put(context,"pref_key_long_break", longBreak);
                SPUtils.put(context,"pref_key_long_break_frequency", frequency);

                //跳转
                Intent intent = new Intent(getActivity(), ClockActivity.class);//跳转到ClockActivity
                intent.putExtra("clocktitle",clockTitle);
                intent.putExtra("workLength", workLength);
                intent.putExtra("shortBreak", shortBreak);
                intent.putExtra("longBreak", longBreak);
                startActivity(intent);
            }

            //长按事件
            @Override
            public void onItemLongClick(View view, final int position) {

            }
        }));

        //滑动回调监听事件
        callback = new ClockItemTouchHelperCallback(clockRecyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        setDbData();//读取数据
        return rootView;
    }

    //数据初始化
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDbData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume(){
        setDbData();
        clockRecyclerViewAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void setDbData(){
        localTomato = TomatoUtils.getAllTomato(getContext());
        if (localTomato.size() > 0) {
            setListData(localTomato);
        }
    }

    /**
     * 设置list数据
     */
    private void setListData(List<Tomato> newList) {
        clockList.clear();
        clockList.addAll(newList);
        clockRecyclerViewAdapter.notifyDataSetChanged();
    }

}
