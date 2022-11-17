package com.redrum.todo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import com.redrum.todo.activity.TodoDetail;
import com.redrum.todo.activity.TodoEdit;
import com.redrum.todo.model.Todo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Todo> todoList;
    private final SQLiteDatabase db;
    private final MainActivity mainActivity;
    private final int adapterType;

    public TodoAdapter(MainActivity activity, int type, SQLiteDatabase db) {
        this.mainActivity = activity;
        this.adapterType = type;
        this.db = db;
        refresh();
    }

    public List<Todo> getTodoList() {
        return todoList;
    }

    // 创建列表项组件的方法，该方法创建组件会被自动缓存
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TodoViewHolder(view);
    }

    // 为列表项组件绑定数据的方法，每次组件重新显示出来时都会重新执行该方法
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        refresh();
        holder.checkBox.setChecked(todoList.get(position).getChecked() == 1);
        holder.checkBox.setOnClickListener(view -> checkChange(position));
        holder.textView.setText(todoList.get(position).getTitle());
        holder.textView.setOnClickListener(view -> {
            Bundle data = new Bundle();
            data.putSerializable("Info", todoList.get(position));
            data.putSerializable("action", "update");
            Intent intent = new Intent(getContext(), TodoDetail.class);
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

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.main_check_box);
            this.textView = itemView.findViewById(R.id.main_title);
        }
    }

    public Context getContext() {
        return mainActivity;
    }

    public void insertData(Todo todo) {
        todoList.add(todo);
        mainActivity.status_refresh();
        notifyItemInserted(todoList.size());
    }

    public Todo deleteData(int position) {
        Todo todo = todoList.get(position);
        todoList.remove(position);
        mainActivity.status_refresh();
        notifyItemRemoved(position);
        return todo;
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
                if (!flag)
                    notifyItemChanged(position);
                break;
            }
        }
    }

    public void update(int position) {
        Bundle data = new Bundle();
        data.putSerializable("action", "update");
        data.putSerializable("Info", todoList.get(position));
        Intent intent = new Intent(getContext(), TodoEdit.class);
        intent.putExtras(data);
        // 启动intent对应的Activity
        mainActivity.startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void checkChange(int position) {
        Todo todo = todoList.get(position);
        todo.setChecked(~todo.getChecked() & 1);

        String[] type = todo.getType().split("-");
        if (todo.getChecked() == 1) {
            if (type[0].equals("4")) {
                Todo temp = todo.renew();
                updateData(temp);
                if (Integer.parseInt(type[1]) <= 1) {
                    temp.setChecked(1);
                    updateData(temp);
                    mainActivity.recycle_view_refresh(0, position);
                }
            } else {
                mainActivity.recycle_view_refresh(0, position);
            }
        } else {
            if (type[0].equals("4")) {
                updateData(todo.back_renew());
            }
            mainActivity.recycle_view_refresh(1, position);
        }
        mainActivity.status_refresh();
    }

    public void refresh() {
        todoList = new ArrayList<>();
        List<Todo> tempList = DBHelper.getAllData(db);
        for (Todo i : tempList)
            if (adapterType == i.getChecked()) {
                if (i.isShowable(new Date()))
                    todoList.add(i);
                else if (i.isRenewable()) {
                    Todo temp = i.renew();
                    temp.setChecked(0);
                    DBHelper.updateData(db, temp);
                }
            }
        if (adapterType == 1) {
            if (todoList.size() > 0)
                Log.d("cao", "refresh: 胆子夺大啊！" + adapterType + "   " + todoList.size());
            mainActivity.show_switch(todoList.size() > 0);
        }
    }
}
