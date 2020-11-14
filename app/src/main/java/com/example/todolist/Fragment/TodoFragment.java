package com.example.todolist.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.todolist.Activity.MainActivity;
import com.example.todolist.Activity.NewTodoActivity;
import com.example.todolist.Adapter.TodoAdapter;
import com.example.todolist.Bean.Todos;
import com.example.todolist.Dao.TodoDao;
import com.example.todolist.Interface.OnItemClickListener;
import com.example.todolist.R;
import com.example.todolist.Utils.ToDoUtils;
import com.example.todolist.Utils.TodoItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Todos> todosList = new ArrayList<>();
    private TodoAdapter todoAdapter;

    private List<Todos> localTodo;
    private ItemTouchHelper myItemTouchHelper;
    private Context context;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodoFragment() {
        // Required empty public constructor
    }
    public TodoFragment(Context context){
        this.context = context;
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

        Log.d("frag","onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_todo,container,false);

        //布局管理器，线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //获得实例
        recyclerView = (RecyclerView)rootView.findViewById(R.id.mRecycler);

        //得到适配器、数据
        todoAdapter = new TodoAdapter(getActivity(),todosList);
        //设置布局，线性布局
        recyclerView.setLayoutManager(layoutManager);
        //设置适配器
        recyclerView.setAdapter(todoAdapter);

        //滑动删除监听器、适配，加载
        ItemTouchHelper.Callback callback = new TodoItemTouchHelperCallback(todoAdapter);
        myItemTouchHelper = new ItemTouchHelper(callback);
        myItemTouchHelper.attachToRecyclerView(recyclerView);

        //监听
        todoAdapter.setOnItemLongClickListener(new TodoAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int pos) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_item,popupMenu.getMenu());
                Log.d("txxxxxx","长按点击");
                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //事件
                        int size = todoAdapter.todosList.size();
                        switch (item.getItemId()){
                            case R.id.removeItem:
                                new TodoDao(context).deleteFatherTodo(todosList.get(size-1-pos).getTid());
                                new TodoDao(context).deleteTaskTodo(todosList.get(size-1-pos).getTid());
                                todoAdapter.todosList.remove(size-1-pos);
                                todoAdapter.notifyItemRemoved(pos);
                                todoAdapter.notifyItemRangeChanged(pos,todoAdapter.todosList.size());
                                break;
                            case R.id.editItem:
                                Intent intent = new Intent(context,NewTodoActivity.class);
                                intent.putExtra("isEdit",true);
                                intent.putExtra("tid",todosList.get(size-1-pos).getTid());
                                startActivityForResult(intent,1);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }

                });
                popupMenu.show();
            }
        });

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
        todoAdapter.notifyDataSetChanged();
        super.onResume();

    }

    private void setDbData(){
        //获取本地数据
        localTodo = ToDoUtils.getAllFatherTodos(getContext());//获取所有
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
        todoAdapter.notifyDataSetChanged();//刷新
    }
}