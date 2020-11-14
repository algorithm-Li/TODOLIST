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
import com.example.todolist.Dao.TodoDao;
import com.example.todolist.R;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 子任务 适配器
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    List<Todos> todosList;
    Context context;

    DisplayMetrics dm;//用来计算 下拉时LinearLayout的高度

    TextView percent;
    int F_tid;

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

    public TaskAdapter(Context context,List<Todos> todosList,int F_tid, TextView percent){
        this.context = context;
        this.todosList = todosList;
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.F_tid = F_tid;
        this.percent = percent;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(position,todosList.get(position));
    }

    @Override
    public int getItemCount() {
        return todosList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ViewHolder myHolder;
        public final TextView task_id;
        public final ShineButton task_button;
        public final TextView task_content;

        public ViewHolder(@NonNull View itemView) {
            //获取实例
            super(itemView);
            myHolder = this;
            task_id = (TextView)itemView.findViewById(R.id.task_id);
            task_button = (ShineButton)itemView.findViewById(R.id.task_button);
            task_content = (TextView)itemView.findViewById(R.id.task_content);
        }

        public void bind(final int pos, final Todos bean) {
            //加载数据
            task_id.setText(pos + 1 +".".toString());
            task_content.setText(bean.getDsc());
            //动态按钮设置状态

            boolean flag = false;
            if(todosList.get(pos).getIsFinish() == 1){
                flag = true;
                task_content.setPaintFlags(task_content.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                task_content.setPaintFlags(task_content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            task_button.setChecked(flag);
            task_button.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View view, boolean checked) {
                    Log.d("texxt","checked"+ checked);
                    //补充
                    //根据Checked的值
                    //对数据库进行操作，刷新Recyclerview
                    percent.setText("100%");
                    new TodoDao(context).setisFinish(bean.getTid(),checked);
                    //刷新TodoList里的数据
                    Log.d("texxtxx:",pos+"");
                    if(checked){
                        todosList.get(pos).setIsFinish(1);
                        task_content.setPaintFlags(task_content.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else{
                        todosList.get(pos).setIsFinish(0);
                        task_content.setPaintFlags(task_content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }

                    //刷新percent的值
                    int finish = new TodoDao(context).getOneTodo(F_tid).getIsFinish();
                    if(finish == 1){
                        percent.setText("完成度：100%");
                    }else{
                        if(todosList.size() == 0){
                            percent.setText("完成度：0%");
                        }else{
                            double success = 0;
                            for(Todos x: todosList){
                                if(x.getIsFinish()==1){
                                    success +=1;
                                }
                            }
                            if(success == 0){
                                percent.setText("完成度：0%");
                            }else if(success == todosList.size()){
                                percent.setText("完成度：100%");
                            }else{
                                DecimalFormat df = new DecimalFormat("#.00");
                                Log.d("number",df.format((success/todosList.size())*100)+ "%");
                                percent.setText("完成度:" + df.format((success/todosList.size())*100)+ "%");
                            }
                        }
                    }
                }
            });
        }
    }

    //转换
    int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
