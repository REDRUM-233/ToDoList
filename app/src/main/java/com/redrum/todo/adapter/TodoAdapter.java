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

    // 数据表
    private final List<Todo> todoList;
    // 用于启动其他activity的intent
    private final Intent editIntent;
    private final Intent detailIntent;
    // 获得mainActivity用于创建intent和调用mainActivity方法
    private final MainActivity mainActivity;

    // 初始化
    public TodoAdapter(MainActivity activity, List<Todo> todoList) {
        this.mainActivity = activity;
        this.todoList = todoList;
        this.detailIntent = new Intent(mainActivity, TodoDetail.class);
        this.editIntent = new Intent(mainActivity, TodoEdit.class);
    }

    // 重写创建列表项组件的方法，该方法创建组件会被自动缓存
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // 读取item布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TodoViewHolder(view, this);
    }

    // 重写为列表项组件绑定数据的方法，每次组件重新显示出来时都会重新执行该方法
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        // 设置checkBox原有状态
        holder.checkBox.setChecked(todoList.get(position).getChecked() == 1);
        // 设置checkBox点击响应
        // 修改数据集并调用mainActivity刷新概览栏
        holder.checkBox.setOnClickListener(view -> {
            // 获得当前布局位置
            int p = holder.getLayoutPosition();
            // 查找该位置对应的事项数据
            Todo todo = todoList.get(p);
            // 修改checked状态
            todo.setChecked(~todo.getChecked() & 1);
            // 改回数据集
            updateData(todo);
            // 刷新概览栏
            mainActivity.status_refresh();
        });

        // 设置显示文本
        holder.textView.setText(todoList.get(position).getTitle());
        // 设置点击响应
        // 打开detail页面
        holder.textView.setOnClickListener(view -> {
            // 打包数据
            Bundle data = new Bundle();
            data.putSerializable("Info", todoList.get(position));
            // 复制intent
            Intent intent = new Intent(detailIntent);
            // 装数据
            intent.putExtras(data);
            // 启动intent对应的Activity
            mainActivity.startActivity(intent);
        });
    }

    // 该方法的返回值决定包含多少个列表项
    @Override
    public int getItemCount() {
        return todoList.size();
    }

    // 设置缓存布局
    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public TextView textView;

        public TodoViewHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.main_check_box);
            this.textView = itemView.findViewById(R.id.main_title);
        }
    }

    // 插入数据集
    public void insertData(Todo todo) {
        // 默认增加id
        todo.setId(todoList.size() + 1);
        // 存入数据集
        todoList.add(todo);
        // 刷新mainActivity概览栏
        mainActivity.status_refresh();
        // 调用RecyclerView.Adapter的notifyItemInserted修改布局
        notifyItemInserted(todoList.size());
    }

    // 从数据集中删除数据
    public void deleteData(int position) {
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

    // 从数据集中修改数据
    public void updateData(Todo todo) {
        // 查找对应id
        for (Todo i : todoList) {
            if (i.getId() == todo.getId()) {
                int position = todoList.indexOf(i);
                // 计算是否需要刷新
                boolean flag = i.getTitle().equals(todo.getTitle()) && i.getChecked() == todo.getChecked();
                todoList.remove(position);
                todoList.add(position, todo);
                // 如果checked状态和title都没有改变就不用修改布局
                if (!flag)
                    notifyItemChanged(position);
                break;
            }
        }
    }

    // 跳转到更新界面edit_activity
    public void update(int position) {
        // 打包数据
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

    public Context getContext() {
        return mainActivity;
    }
}
