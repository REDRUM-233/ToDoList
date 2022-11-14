package com.redrum.todo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.redrum.todo.DBHelper;
import com.redrum.todo.R;
import com.redrum.todo.activity.MainActivity;
import com.redrum.todo.model.Todo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Todo> todoList;
    private Intent detailIntent;
    private Intent editIntent;
    private SQLiteDatabase db;
    private MainActivity mainActivity;

    public TodoAdapter(MainActivity activity, Intent detailIntent, Intent editIntent, SQLiteDatabase db) {
        this.mainActivity = activity;
        this.detailIntent = detailIntent;
        this.editIntent = editIntent;
        this.db = db;
        todoList = new ArrayList<>();
        refreash();
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
    public void onBindViewHolder(@NonNull TodoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.checkBox.setChecked(todoList.get(position).getChecked() == 1);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkChange(position, b);
            }
        });
        holder.textView.setText(todoList.get(position).getTitle());
        holder.textView.setOnClickListener(view -> {
            Bundle data = new Bundle();
            data.putSerializable("Info", todoList.get(position));
            data.putSerializable("action", "update");
            Intent intent = new Intent(detailIntent);
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
        data.putSerializable("action", "update");
        data.putSerializable("Info", todoList.get(position));
        Intent intent = new Intent(editIntent);
        intent.putExtras(data);
        // 启动intent对应的Activity
        mainActivity.startActivity(intent);
    }

    public void checkChange(int position, boolean is) {
        Todo todo = todoList.get(position);
        todo.setChecked(is ? 1 : 0);
        DBHelper.updateData(db, todo);
        refreash();
        notifyItemChanged(position);
    }

    public void refreash() {
        List<Todo> tempList = DBHelper.getAllData(db);
        for (Todo i : tempList) {
            if (i.isShowable(new Date()))
                this.todoList.add(i);
            else if (i.isRenewable()) {
                DBHelper.updateData(db, i.renew());
                this.todoList.add(i.renew());
            }
        }
        this.todoList = tempList;
    }
}
