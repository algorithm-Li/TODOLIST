package com.example.todolist.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.todolist.Activity.MainActivity;
import com.example.todolist.Anim.ExpandableViewHoldersUtil;
import com.example.todolist.Bean.Todos;
import com.example.todolist.DBHelper.MyDatabaseHelper;
import com.example.todolist.Interface.ItemTouchHelperAdapter;
import com.example.todolist.Interface.OnItemClickListener;
import com.example.todolist.R;
import com.example.todolist.Utils.BitmapUtils;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder>
        implements ItemTouchHelperAdapter {

    private Context myContext;
    private List<Todos> todosList;

    private int truePosition,itemPosition;
    private MaterialDialog dialog;
    private MyDatabaseHelper dbHelper;

    ExpandableViewHoldersUtil.KeepOneH<TestViewHolder> keepOne = new ExpandableViewHoldersUtil.KeepOneH<>();
    //点击事件的回调
    private OnItemClickListener<Todos> onItemClickListener;

    //设置回调监听
    public void setOnItemClickListener(OnItemClickListener<Todos> listener){
        this.onItemClickListener = listener;
        Log.d("TODOOO","setOnItemClickListener");
    }


    public TestAdapter(Context context,List<Todos> todosList){
        this.myContext = context;
        this.todosList = todosList;
        Log.d("TODOOO","TestAdapter");
    }
    
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("TODOOO","onCreateViewHolder");
        return new TestViewHolder((ViewGroup)LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        Log.d("TODOOO","onBindViewHolder");
        final Todos bean = todosList.get(todosList.size()-1-position);
        holder.bind(position, bean);
    }

    @Override
    public int getItemCount() {
        Log.d("TODOOO","getItemCount");
        return todosList == null? 0 : todosList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.d("TODOOO","onItemMove");
        Collections.swap(todosList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(fromPosition,toPosition);
        return true;
    }

    @Override
    public void removeItem(int position) {
        Log.d("TODOOO","removeItem");
        truePosition = todosList.size()-1-position;  //数据的真正位置
        Log.d("TODOOO",truePosition+"");
        itemPosition = position;    //用户选择删除的位置
        Log.d("TODOOO",itemPosition+"");
        popAlertDialog();

    }

    //弹窗确认选择和退出
    private void popAlertDialog() {

        Log.d("TODOOO","popAlertDialog");
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

    public class TestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableViewHoldersUtil.Expandable{

        private TestViewHolder mHolder;
        public final TextView mTitle;
        public final TextView mSource;
        public final TextView mReferee;
        public final TextView mLink;
        public final ImageView mRight;
        public final RelativeLayout mTopLayout; //折叠View
        public final LinearLayout mBottomLayout; //折叠View
        public final TextView percent;
        public final ShineButton shineButton;
        public final RecyclerView task_rvl;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("TODOOO","TestViewHolder");
            mHolder = this;
            mTitle = itemView.findViewById(R.id.mTitle);
            mSource = itemView.findViewById(R.id.mSource);
            mReferee = itemView.findViewById(R.id.mReferee);
            mLink = itemView.findViewById(R.id.mLink);
            mRight = itemView.findViewById(R.id.mRight);
            percent = itemView.findViewById(R.id.percent);
            mTopLayout = itemView.findViewById(R.id.mTopLayout);
            mBottomLayout = itemView.findViewById(R.id.mBottomLayout);
            shineButton = itemView.findViewById(R.id.po_image1);
            task_rvl = itemView.findViewById(R.id.task_rlv);
            mTopLayout.setOnClickListener(this);
        }

        //绑定数据
        public void bind(final int pos, final Todos bean) {
            Log.d("TODOOO","bind");
            keepOne.bind(this,pos);
            mTitle.setText(bean.getTitle());
            mSource.setText(bean.getDsc());
            mReferee.setText(bean.getDate());
            mLink.setText(bean.getImgId());
            percent.setText("完成度：100%");
            //Drawable drawable = myContext.getResources().getDrawable(bean.getImgId());
            //drawable.setBounds(0, 0, 69, 69);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
            //mTopLayout.setCompoundDrawables(null, drawable, null, null);//只放上面
            //mTopLayout.setBackground(drawable);

            //动态按钮设置状态
            shineButton.setChecked(true);//从Todos bean里读取数据
            //监听
            shineButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View view, boolean checked) {
                    Log.d("texxt","checked"+ checked);
                    //补充
                    //根据Checked的值
                    //对数据库进行操作，刷新Recyclerview
                }
            });

            mHolder.mBottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TODOOO","mBottomLayout.setOnClickListener");
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(bean, mHolder.mBottomLayout, pos);
                    }
                }
            });

            //更换数据源
            //以下为测试数据
            List<Todos> tmp = new ArrayList<Todos>();
            tmp.add(new Todos("测试3","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
            tmp.add(new Todos("测试4","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));
            tmp.add(new Todos("测试5","我爱你",new Date().toString(),"123456",R.drawable.ic_nav_bg,1));

            //子任务 Recyclerview 适配器
            TaskAdapter taskAdapter = new TaskAdapter(myContext,tmp);
            //获得布局管理器
            LinearLayoutManager layoutManager = new LinearLayoutManager(myContext);
            //设置布局
            task_rvl.setLayoutManager(layoutManager);
            //加载适配器
            task_rvl.setAdapter(taskAdapter);
        }

        @Override
        public void onClick(View v) {
            Log.d("TODOOO","ViewHolder.onClick");
            switch (v.getId()) {
                case R.id.mTopLayout:
                    keepOne.toggle(mHolder, mRight);//点击下拉和上回
                    break;
            }
        }

        @Override
        public View getExpandView() {
            Log.d("TODOOO","ViewHolder.getExpandView");
            return mBottomLayout;
        }
    }
}
