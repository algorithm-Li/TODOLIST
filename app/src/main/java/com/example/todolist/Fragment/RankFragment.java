package com.example.todolist.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Adapter.RankRecyclerViewAdapter;
import com.example.todolist.Bean.User;
import com.example.todolist.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class RankFragment extends Fragment {

    private RecyclerView recyclerView;
    private RankRecyclerViewAdapter rankRecyclerViewAdapter;
    private List<User> rankList = new ArrayList<>();

    public RankFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRank();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        recyclerView = view.findViewById(R.id.rank_recycler);
        rankRecyclerViewAdapter = new RankRecyclerViewAdapter(rankList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(rankRecyclerViewAdapter);
        return view;
    }

    private void getRank(){
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.order("total");
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
                    Log.i("Rank", "查询到: "+list.size()+" 条数据");
                    setListData(list);
                    Log.i("Rank", ""+rankList.size());
                }
            }
        });
    }

    /**
     * 设置list数据
     */
    private void setListData(List<User> newList) {
        rankList.addAll(newList);
        rankRecyclerViewAdapter.notifyDataSetChanged();
    }
}