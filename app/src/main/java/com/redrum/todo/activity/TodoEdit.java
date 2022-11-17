package com.redrum.todo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

public class TodoEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);

        // 状态栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 修改文本框
        EditText title = findViewById(R.id.edit_title);
        EditText desc = findViewById(R.id.edit_desc);

        // 获得需要修改的todo
        Intent intent = getIntent();
        Todo todo = (Todo) intent.getSerializableExtra("Info");

        // 修改编辑框
        title.setText(todo.getTitle());
        desc.setText(todo.getDesc());

        // 右下角悬浮按钮
        FloatingActionButton fab = findViewById(R.id.edit_fab);
        // 设置点击相应
        // 打包数据发送给mainActivity的handler处理
        fab.setOnClickListener(view -> {
            // 打包数据
            Todo update_todo = new Todo(todo.getId(), todo.getChecked(), todo.getType(), title.getText().toString(), desc.getText().toString());
            Message message = new Message();
            Bundle data = new Bundle();
            data.putSerializable("info", update_todo);
            message.setData(data);
            // 设置消息类型
            message.what = 114;
            // 发送消息
            Handler handler = MainActivity.handler;
            handler.sendMessage(message);
            finish();
        });
    }

    // 设置状态栏左上角退出按钮响应
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}