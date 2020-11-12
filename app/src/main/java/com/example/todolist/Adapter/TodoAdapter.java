package com.example.todolist.Adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.todolist.Bean.Todos;
import com.example.todolist.R;
import com.example.todolist.Utils.BitmapUtils;
import com.example.todolist.Utils.SPUtils;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private List<Todos> todosList;
    private Context context;

    //构造函数
    public TodoAdapter(Context context){
        this.context = context;
    }

    public TodoAdapter(List<Todos> todos, Context context){
        this.todosList = todos;
        this.context = context;
    }

    //自定义内部类 ViewHolder类
    static class ViewHolder extends RecyclerView.ViewHolder{
        //数据、控件
        TextView todo_title;
        TextView todo_desc;
        TextView todo_date;
        TextView todo_time;
        TextView isAlerted;
        ImageView card_background;
        TextView isRepeat;
        TimelineView timelineView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //获取实例
            todo_title = (TextView)itemView.findViewById(R.id.todo_title);
            todo_desc = (TextView)itemView.findViewById(R.id.todo_desc);
            todo_date = (TextView)itemView.findViewById(R.id.todo_date);
            //todo_time = (TextView)itemView.findViewById(R.id.todo_time);
            //isAlerted = (TextView)itemView.findViewById(R.id.isAlerted);
            isRepeat = (TextView)itemView.findViewById(R.id.isRepeat);
            card_background = (ImageView)itemView.findViewById(R.id.card_bg);
            timelineView = (TimelineView)itemView.findViewById(R.id.time_marker);
        }
    }

    @NonNull
    @Override
    public TodoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position){
        View v = LayoutInflater.from(context).inflate(R.layout.item_todo,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .signature(new ObjectKey(SPUtils.get(context,"head_signature","")))
                .placeholder(R.drawable.ic_bg_2);*/

        holder.todo_title.setText(todosList.get(todosList.size()-1-position).getTitle());
        holder.todo_desc.setText(todosList.get(todosList.size()-1-position).getDsc());
        holder.todo_date.setText(todosList.get(todosList.size()-1-position).getDate() + " " +
                todosList.get(todosList.size()-1-position).getTime());
        //holder.card_background.setImageBitmap(BitmapUtils.readBitMap(context,todosList.get(todosList.size()-1-position).getImgId()));
        Glide.with(context).load(todosList.get(todosList.size()-1-position).getImgId()).into(holder.card_background);
        //Glide.with(context).load(todosList.get(todosList.size()-1-position).getId()))

        if(todosList.get(todosList.size()-1-position).getIsRepeat() == 1){
            holder.isRepeat.setText("重复");
            holder.isRepeat.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        }else{
            holder.isRepeat.setText("单次");
            holder.isRepeat.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        }

        //
        //if(todosList.get(todosList.size()-1-position).getRemin)


    }

    @Override
    public int getItemCount() {
        return todosList.size();
    }


}
