package com.redrum.todo.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.redrum.todo.DBHelper;
import com.redrum.todo.R;
import com.redrum.todo.activity.MainActivity;
import com.redrum.todo.model.Todo;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Todo> todoList;
    private Intent baseIntent;
    private SQLiteDatabase db;
    private MainActivity mainActivity;

    public TodoAdapter(MainActivity activity, Intent intent, SQLiteDatabase db) {
        this.mainActivity = activity;
        this.baseIntent = intent;
        this.db = db;
        this.todoList = DBHelper.getAllData(db);
    }

    // 创建列表项组件的方法，该方法创建组件会被自动缓存
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TodoViewHolder(view, this);
    }

    // 为列表项组件绑定数据的方法，每次组件重新显示出来时都会重新执行该方法
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.textView.setText(todoList.get(position).getTitle());
        holder.textView.setOnClickListener(view -> {
            Bundle data = new Bundle();
            data.putSerializable("Info", todoList.get(position));
            Intent intent = new Intent(baseIntent);
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

    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public TextView textView;

        public TodoViewHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.main_check_box);
            this.textView = itemView.findViewById(R.id.main_title);
        }
    }

    public Context getContext() {
        return mainActivity;
    }

    public void delete(int position) {
        Todo todo = todoList.get(position);
        DBHelper.deleteData(db, todo);
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void update(int position) {
        Bundle data = new Bundle();
        data.putSerializable("Info", todoList.get(position));
        Intent intent = new Intent(baseIntent);
        intent.putExtras(data);
        // 启动intent对应的Activity
        mainActivity.startActivity(intent);
    }

    public void refreash() {
        this.todoList = DBHelper.getAllData(db);
    }
}
