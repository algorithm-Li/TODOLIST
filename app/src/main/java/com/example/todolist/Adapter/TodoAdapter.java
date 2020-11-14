package com.example.todolist.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.todolist.Activity.MainActivity;
import com.example.todolist.Anim.ExpandableViewHoldersUtil;
import com.example.todolist.Bean.Todos;
import com.example.todolist.DBHelper.MyDatabaseHelper;
import com.example.todolist.Dao.TodoDao;
import com.example.todolist.Interface.ItemTouchHelperAdapter;
import com.example.todolist.Interface.OnItemClickListener;
import com.example.todolist.R;
import com.example.todolist.Utils.BitmapUtils;
import com.example.todolist.Utils.ToDoUtils;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder>
        implements ItemTouchHelperAdapter {

    private Context myContext;
    public List<Todos> todosList;

    private int truePosition,itemPosition;
    private MaterialDialog dialog;
    private MyDatabaseHelper dbHelper;

    public TodoAdapter(Context context,List<Todos> todosList){
        this.myContext = context;
        this.todosList = todosList;
    }

    ExpandableViewHoldersUtil.KeepOneH<TodoViewHolder> keepOne = new ExpandableViewHoldersUtil.KeepOneH<>();
    //点击事件的回调
    private OnItemClickListener<Todos> onItemClickListener;

    //设置回调监听
    public void setOnItemClickListener(OnItemClickListener<Todos> listener){
        this.onItemClickListener = listener;
    }


    //长按事件的回调
    private OnItemLongClickListener onItemLongClickListener;
    public interface OnItemLongClickListener{
        void onItemLongClick(View view , int pos);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodoViewHolder((ViewGroup)LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        final Todos bean = todosList.get(todosList.size()-1-position);
        holder.bind(position, bean);
    }

    @Override
    public int getItemCount() {
        return todosList == null? 0 : todosList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(todosList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(fromPosition,toPosition);
        return true;
    }

    @Override
    public void removeItem(int position) {
        truePosition = todosList.size()-1-position;  //数据的真正位置
        itemPosition = position;    //用户选择删除的位置
        popAlertDialog();

    }

    //弹窗确认选择和退出
    private void popAlertDialog() {

        if (dialog == null) {

            dialog = new MaterialDialog(myContext);
            dialog.setMessage("确定删除？")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Todos todos = todosList.get(truePosition);
                            dbHelper = new MyDatabaseHelper(myContext, "Data.db", null, 1);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.delete("Todos","tid = ?",
                                    new String[]{todosList.get(truePosition).getTid()+ ""});
                            //同时删除子任务
                            db.delete("Todos","F_tid = ?",
                                    new String[]{todosList.get(truePosition).getTid()+ ""});

                            //若有云数据，在这里删除
                            //补充
                            todosList.remove(truePosition);
                            notifyItemRemoved(itemPosition);//更新位置
                            notifyItemRangeChanged(itemPosition,todosList.size());//更新数量
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        public void onClick(View view) {
                            notifyItemChanged(itemPosition);
                            dialog.dismiss();
                        }
                    });
        }
        dialog.show();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ExpandableViewHoldersUtil.Expandable{

        private TodoViewHolder mHolder;
        public final TextView myTime;
        public final TextView myTitle;
        public final TextView percent;
        public final TextView myDsc;
        public final ImageView mRight;

        public final RelativeLayout mTopLayout; //折叠View
        public final LinearLayout mBottomLayout; //折叠View
        public final ShineButton shineButton;
        public final RecyclerView task_rvl;
        public final Button add_task;//添加子任务按钮

        TaskAdapter taskAdapter;//子任务适配器
        List<Todos> taskTodos;//子任务数据
        int F_tid = 0;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            mHolder = this;
            myTime = itemView.findViewById(R.id.myTime);
            myTitle = itemView.findViewById(R.id.myTitle);
            percent = itemView.findViewById(R.id.percent);
            myDsc = itemView.findViewById(R.id.myDsc);
            mRight = itemView.findViewById(R.id.mRight);
            mTopLayout = itemView.findViewById(R.id.mTopLayout);
            mBottomLayout = itemView.findViewById(R.id.mBottomLayout);
            shineButton = itemView.findViewById(R.id.po_image1);
            task_rvl = itemView.findViewById(R.id.task_rlv);
            add_task = itemView.findViewById(R.id.add_task);
            mTopLayout.setOnClickListener(this);
        }

        //绑定数据
        public void bind(final int pos, final Todos bean) {
            this.F_tid = bean.getTid();
            keepOne.bind(this,pos);
            myTime.setText(bean.getDate() +" " + bean.getTime());
            myTitle.setText(bean.getTitle());
            myDsc.setText(bean.getDsc());

            //动态按钮设置状态
            boolean flag = false;
            if(bean.getIsFinish() == 1){
                flag = true;
            }
            shineButton.setChecked(flag);//从Todos bean里读取数据


            taskTodos = ToDoUtils.getTaskTodos(myContext,bean.getTid());
            Log.d("texxttt",bean.getTid()+"");
            if(bean.getIsFinish() == 1){
                percent.setText("完成度：100%");
            }else{
                if(taskTodos.size() == 0){
                    percent.setText("完成度：0%");
                }else{
                    double finish = 0;
                    for (Todos x: taskTodos) {
                        if(x.getIsFinish() == 1){
                            finish += 1;
                        }
                    }
                    if(finish == 0){
                        percent.setText("完成度：0%");
                    }else if(finish == taskTodos.size()){
                        percent.setText("完成度：100%");
                    }else{
                        DecimalFormat df = new DecimalFormat("#.00");
                        Log.d("number",df.format((finish/taskTodos.size())*100)+ "%");
                        percent.setText("完成度:" + df.format((finish/taskTodos.size())*100)+ "%");
                    }

                }
            }

            //监听
            final List<Todos> finalTaskTodos = taskTodos;
            shineButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View view, boolean checked) {
                    //补充
                    //根据Checked的值
                    //对数据库进行操作，刷新Recyclerview
                    new TodoDao(myContext).setisFinish(bean.getTid(),checked);
                    //刷新TodoList里的数据
                    if(checked){
                        bean.setIsFinish(1);
                        todosList.set(todosList.size()-1-pos,bean);
                    }else{
                        bean.setIsFinish(0);
                        todosList.set(todosList.size()-1-pos,bean);
                    }

                    if(bean.getIsFinish() == 1){
                        percent.setText("完成度：100%");
                    }else {
                        if (finalTaskTodos.size() == 0) {
                            percent.setText("完成度：0%");
                        }else {
                            double finish = 0;
                            for (Todos x : finalTaskTodos) {
                                if (x.getIsFinish() == 1) {
                                    finish += 1;
                                }
                            }
                            if(finish == 0){
                                percent.setText("完成度：0%");
                            }else if(finish == finalTaskTodos.size()){
                                percent.setText("完成度：100%");
                            }else{
                                DecimalFormat df = new DecimalFormat("#.00");
                                Log.d("number",df.format((finish/finalTaskTodos.size())*100)+ "%");
                                percent.setText("完成度:" + df.format((finish/finalTaskTodos.size())*100)+ "%");
                            }
                        }
                    }
                }
            });


            //Drawable drawable = myContext.getResources().getDrawable(bean.getImgId());
            //drawable.setBounds(0, 0, 69, 69);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
            //mTopLayout.setCompoundDrawables(null, drawable, null, null);//只放上面
            //mTopLayout.setBackground(drawable);
            mHolder.mBottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        Log.d("xxxxxx","点击");
                        onItemClickListener.onItemClick(bean, mHolder.mBottomLayout, pos);
                    }
                }
            });

            //回调长按监听事件
            if(onItemLongClickListener!=null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d("xxxxxx","长按");
                        onItemLongClickListener.onItemLongClick(itemView,pos);
                        return false;
                    }
                });
            }

            //子任务 Recyclerview 适配器
            taskAdapter = new TaskAdapter(myContext,taskTodos,bean.getTid(),percent);
            //获得布局管理器
            LinearLayoutManager layoutManager = new LinearLayoutManager(myContext);
            //设置布局
            task_rvl.setLayoutManager(layoutManager);
            //加载适配器
            task_rvl.setAdapter(taskAdapter);

            //add_task.setOnClickListener(this);

            add_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText inputServer = new EditText(myContext);
                    AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
                    builder.setTitle("输入子任务").setIcon(R.drawable.task_1).setView(inputServer)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String text = inputServer.getText().toString();
                                    if(text.equals("")){
                                        Log.d("insertTTTT","插入失败");
                                    }else{
                                        Todos insertTodo = new Todos();
                                        insertTodo.setDsc(text);
                                        insertTodo.setF_tid(F_tid);
                                        insertTodo.setIsFinish(0);
                                        long tid = new TodoDao(myContext).create(insertTodo);
                                        insertTodo.setTid((int)tid);
                                        taskTodos.add(insertTodo);
                                        taskAdapter.notifyDataSetChanged();
                                        if(bean.getIsFinish() == 1){
                                            percent.setText("完成度：100%");
                                        }else {
                                            if (taskTodos.size() == 0) {
                                                percent.setText("完成度：0%");
                                            }else {
                                                double finish = 0;
                                                for (Todos x : taskTodos) {
                                                    if (x.getIsFinish() == 1) {
                                                        finish += 1;
                                                    }
                                                }
                                                if(finish == 0){
                                                    percent.setText("完成度：0%");
                                                }else if(finish == taskTodos.size()){
                                                    percent.setText("完成度：100%");
                                                }else{
                                                    DecimalFormat df = new DecimalFormat("#.00");
                                                    Log.d("number",df.format((finish/taskTodos.size())*100)+ "%");
                                                    percent.setText("完成度:" + df.format((finish/taskTodos.size())*100)+ "%");
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                    builder.show();
                }
            });
            
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mTopLayout:
                    Log.d("xxxxxx","点击");
                    keepOne.toggle(mHolder, mRight);//点击下拉和上回
                    break;
//                case R.id.add_task:
//                    final EditText inputServer = new EditText(myContext);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
//                    builder.setTitle("输入子任务").setIcon(R.drawable.task_1).setView(inputServer)
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String text = inputServer.getText().toString();
//                                    if(text.equals("")){
//                                        Log.d("insertTTTT","插入失败");
//                                    }else{
//                                        Todos insertTodo = new Todos();
//                                        insertTodo.setDsc(text);
//                                        insertTodo.setF_tid(F_tid);
//                                        insertTodo.setIsFinish(0);
//                                        long tid = new TodoDao(myContext).create(insertTodo);
//                                        insertTodo.setTid((int)tid);
//                                        taskTodos.add(insertTodo);
//                                        taskAdapter.notifyDataSetChanged();
//                                    }
//                                }
//                            });
//                    builder.show();
//                    break;
                default:
                    break;
            }
        }

        @Override
        public View getExpandView() {
            //展开view
            return mBottomLayout;
        }
    }
}
