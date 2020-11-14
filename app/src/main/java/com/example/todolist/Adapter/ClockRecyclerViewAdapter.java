package com.example.todolist.Adapter;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.todolist.Bean.Tomato;
import com.example.todolist.DBHelper.MyDatabaseHelper;
import com.example.todolist.Interface.ItemTouchHelperAdapter;
import com.example.todolist.R;
import com.example.todolist.Utils.BitmapUtils;
import com.example.todolist.Utils.SPUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * RecyclerView适配器
 */
public class ClockRecyclerViewAdapter extends RecyclerView.Adapter<ClockRecyclerViewAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Tomato> tomatoList;
    private Context context;
    private MaterialDialog dialog;
    private int truePosition,itemPosition;
    private MyDatabaseHelper dbHelper;


    public ClockRecyclerViewAdapter(List<Tomato> tomato, Context context) {
        this.tomatoList = tomato;
        this.context=context;
    }


    //自定义ViewHolder类
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView clock_title;
        TextView work_time;
        ImageView clock_card_bg;

        public ViewHolder(View itemView) {
            super(itemView);
            //获取实例
            clock_title = (TextView) itemView.findViewById(R.id.clock_title);
            work_time = (TextView) itemView.findViewById(R.id.work_time);
            clock_card_bg = (ImageView) itemView.findViewById(R.id.clock_card_bg);
        }
    }

    @NonNull
    @Override
    public ClockRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_clock,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ClockRecyclerViewAdapter.ViewHolder ViewHolder, int i) {

        RequestOptions options2 =new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .signature(new ObjectKey(Objects.requireNonNull(SPUtils.get(context, "head_signature", ""))))
                .placeholder(R.drawable.ic_img1);

        ViewHolder.clock_title.setText(tomatoList.get(tomatoList.size()-1-i).getTitle());
        ViewHolder.work_time.setText(tomatoList.get(tomatoList.size()-1-i).getWorkLength() + " 分钟");
        ViewHolder.clock_card_bg.setImageBitmap(BitmapUtils.readBitMap(context,tomatoList.get(tomatoList.size()-1-i).getImgId()));

    }

    @Override
    public int getItemCount() {
        return tomatoList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(tomatoList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(fromPosition,toPosition);
        return true;
    }

    public void removeItem(int position){
        truePosition = tomatoList.size()-1-position;
        itemPosition = position;
        popAlertDialog();
    }

    private void popAlertDialog() {

        if (dialog == null) {
            dialog = new MaterialDialog(context);
            dialog.setMessage("确定删除？")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Tomato tomato = tomatoList.get(truePosition);
                            String clockTitle = tomatoList.get(truePosition).getTitle();
                            dbHelper = new MyDatabaseHelper(context, "Data.db", null, 1);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.delete("Clock","clocktitle = ?",
                                    new String[]{clockTitle});

                            tomatoList.remove(truePosition);
                            notifyItemRemoved(itemPosition);
                            notifyItemRangeChanged(itemPosition,tomatoList.size());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        public void onClick(View view) {
                            notifyItemChanged(itemPosition);
                            Log.i("sx", "item已刷新 ");
                            dialog.dismiss();
                        }
                    });
        }
        dialog.show();
    }
}