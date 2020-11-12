package com.example.todolist.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.todolist.Adapter.TodoAdapter;
import com.example.todolist.Bean.Todos;
import com.example.todolist.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment {


    //变量
    private RecyclerView recyclerView;
    private List<Todos> todosList = new ArrayList<>();
    private TodoAdapter todoAdapter;
    private List<Todos> localTodo;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //加载布局文件
        View rootView = inflater.inflate(R.layout.fragment_todo,container,false);

        //线性布局管理器，默认是纵向排列的
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);

        todosList.add(new Todos("测试1","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试2","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试3","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试4","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_1,1));
        todoAdapter = new TodoAdapter(todosList,getActivity());
        recyclerView.setLayoutManager(layout);
        //recyclerView.addItemDecoration(new SpaceItem);
        recyclerView.setAdapter(todoAdapter);

        recyclerView.getItemAnimator().setChangeDuration(300);
        recyclerView.getItemAnimator().setMoveDuration(300);


        return rootView;

    }
}