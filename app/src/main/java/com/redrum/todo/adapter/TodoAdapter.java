package com.redrum.todo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.redrum.todo.R;
import com.redrum.todo.activity.MainActivity;
import com.redrum.todo.activity.TodoDetail;
import com.redrum.todo.activity.TodoEdit;
import com.redrum.todo.model.Todo;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    //    数据表
    private final List<Todo> todoList;
    //    用于启动其他activity的intent
    private final Intent editIntent;
    private final Intent detailIntent;
    //    垃圾adapter巴拉巴拉很多限制，
//    所以直接用MainActivity（挟天子以令诸侯！
    private final MainActivity mainActivity;

    //    初始化
    public TodoAdapter(MainActivity activity, List<Todo> todoList) {
        this.mainActivity = activity;
        this.todoList = todoList;
        this.detailIntent = new Intent(mainActivity, TodoDetail.class);
        this.editIntent = new Intent(mainActivity, TodoEdit.class);
    }

    // 创建列表项组件的方法，该方法创建组件会被自动缓存（fkjava自己的注释
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//        就是读res里面的item布局，只是代码又臭又长
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TodoViewHolder(view, this);
    }

    // 为列表项组件绑定数据的方法，每次组件重新显示出来时都会重新执行该方法（fkjava自己的注释
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
//        设置item里面的各种操作
//        设置保存的状态
        holder.checkBox.setChecked(todoList.get(position).getChecked() == 1);
//        设置相应
        holder.checkBox.setOnClickListener(view -> {
            int p = holder.getLayoutPosition();
            Todo todo = todoList.get(p);
            todo.setChecked(~todo.getChecked() & 1);
            updateData(todo);
            mainActivity.status_refresh();
        });
//        设置显示文本
        holder.textView.setText(todoList.get(position).getTitle());
//        设置点击响应
//        打开detail页面
        holder.textView.setOnClickListener(view -> {
//            打包数据
            Bundle data = new Bundle();
            data.putSerializable("Info", todoList.get(position));
//            复制intent
            Intent intent = new Intent(detailIntent);
//            装数据
            intent.putExtras(data);
            // 启动intent对应的Activity（fkjava自己的注释
            mainActivity.startActivity(intent);
        });
    }

    // 该方法的返回值决定包含多少个列表项（fkjava自己的注释
    @Override
    public int getItemCount() {
        return todoList.size();
    }

    //    缓存工具，有什么用我也不知道，但是必须要写
//    就是把item里面的组件都创建一下，链接一下
    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public TextView textView;

        public TodoViewHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.main_check_box);
            this.textView = itemView.findViewById(R.id.main_title);
        }
    }


    public void insertData(Todo todo) {
        todoList.add(todo);
        mainActivity.status_refresh();
        notifyItemInserted(todoList.size());
    }

    //    手写的删除一项
    public void deleteData(int position) {
        Todo todo = todoList.get(position);
        todoList.remove(position);
        mainActivity.status_refresh();
        notifyItemRemoved(position);
    }

    public void deleteData(Todo todo) {
        int position = todoList.indexOf(todo);
        todoList.remove(todo);
        mainActivity.status_refresh();
        notifyItemRemoved(position);
    }

    public void updateData(Todo todo) {
        for (Todo i : todoList) {
            if (i.getId() == todo.getId()) {
                int position = todoList.indexOf(i);
                boolean flag = i.getTitle().equals(todo.getTitle()) && i.getChecked() == todo.getChecked();
                todoList.remove(position);
                todoList.add(position, todo);
                Log.d("cao", "updateData: " + position + " " + flag);
                if (!flag)
                    notifyItemChanged(position);
                break;
            }
        }
    }

    //    手写的更新跳转函数————指跳转到更新界面edit_activity
    public void update(int position) {
        Bundle data = new Bundle();
        data.putSerializable("Info", todoList.get(position));
        Intent intent = new Intent(editIntent);
        intent.putExtras(data);
        // 启动intent对应的Activity
        mainActivity.startActivity(intent);
    }

    public List<Todo> getTodoList() {
        return todoList;
    }

    //    给touchHelper用的，让它也挟天子以令诸侯（不是
    public Context getContext() {
        return mainActivity;
    }
}
