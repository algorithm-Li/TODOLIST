package com.example.todolist.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.todolist.Adapter.TestAdapter;
import com.example.todolist.Adapter.TodoAdapter;
import com.example.todolist.Bean.Todos;
import com.example.todolist.R;
import com.example.todolist.Utils.ToDoUtils;
import com.example.todolist.Utils.TodoItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Todos> todosList = new ArrayList<>();
    private TestAdapter testAdapter;

    private List<Todos> localTodo;
    private ItemTouchHelper myItemTouchHelper;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static TestFragment newInstance(String param1, String param2) {
        TestFragment fragment = new TestFragment();
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

        Log.d("frag","onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_test,container,false);

        //布局管理器，线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //获得实例
        recyclerView = (RecyclerView)rootView.findViewById(R.id.mRecycler);
        /*
        todosList.add(new Todos("测试1","我爱你",new Date().toString(),"123456",R.drawable.img_3,1));
        todosList.add(new Todos("测试2","我爱你",new Date().toString(),"123456",R.drawable.ic_bg_2,1));
        todosList.add(new Todos("测试3","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试4","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
        todosList.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));*/

        //得到适配器、数据
        testAdapter = new TestAdapter(getActivity(),todosList);
        //设置布局，线性布局
        recyclerView.setLayoutManager(layoutManager);
        //设置适配器
        recyclerView.setAdapter(testAdapter);

        //滑动删除监听器、适配，加载
        ItemTouchHelper.Callback callback = new TodoItemTouchHelperCallback(testAdapter);
        myItemTouchHelper = new ItemTouchHelper(callback);
        myItemTouchHelper.attachToRecyclerView(recyclerView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("frag","onViewCreate");
        setDbData();//获取数据
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume(){
        //重载数据
        setDbData();
        testAdapter.notifyDataSetChanged();
        super.onResume();

    }

    private void setDbData(){
        //获取本地数据
        localTodo = ToDoUtils.getAllTodos(getContext());//获取所有
        if (localTodo.size() > 0) {
            setListData(localTodo);
        }
    }

    /**
     * 设置list数据
     */
    private void setListData(List<Todos> newList) {
        //清空重新设置
        todosList.clear();
        todosList.addAll(newList);
        testAdapter.notifyDataSetChanged();//刷新
    }
}