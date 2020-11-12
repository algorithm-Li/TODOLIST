package com.example.todolist.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Bean.Todos;
import com.example.todolist.R;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.List;

/**
 * 子任务 适配器
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    List<Todos> todosList;
    Context context;

    DisplayMetrics dm;//用来计算 下拉时LinearLayout的高度

    public TaskAdapter(Context context){
        this.context = context;
    }

    public TaskAdapter(Context context,List<Todos> todosList){
        Log.d("texxt",todosList.size()+"");
        this.context = context;
        this.todosList = todosList;
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_child_task,parent,false);
        TaskAdapter.ViewHolder viewHolder = new TaskAdapter.ViewHolder(v);

        //计算高度
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((dm.heightPixels - dip2px(20))*todosList.size(), ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置高度
        v.setLayoutParams(lp);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //加载数据
        holder.task_id.setText(position + 1 +".".toString());
        holder.task_content.setText(todosList.get(position).getDsc());
        holder.task_button.setChecked(false);
        holder.task_button.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                Log.d("texxt","checked"+ checked);
                //补充
                //根据Checked的值
                //对数据库进行操作，刷新Recyclerview

                if(checked){
                    holder.task_content.setPaintFlags(holder.task_content.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else{
                    holder.task_content.setPaintFlags(holder.task_content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return todosList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView task_id;
        ShineButton task_button;
        TextView task_content;

        public ViewHolder(@NonNull View itemView) {
            //获取实例
            super(itemView);
            task_id = (TextView)itemView.findViewById(R.id.task_id);
            task_button = (ShineButton)itemView.findViewById(R.id.task_button);
            task_content = (TextView)itemView.findViewById(R.id.task_content);
        }
    }

    //转换
    int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
