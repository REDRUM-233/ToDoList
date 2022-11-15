package com.redrum.todo.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

public class TodoAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);

//        顶栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
//        启用按钮
        actionBar.setHomeButtonEnabled(true);
//        设置左上角退出按钮显示
        actionBar.setDisplayHomeAsUpEnabled(true);

//        获得文本框
        EditText title = findViewById(R.id.edit_title);
        EditText desc = findViewById(R.id.edit_desc);

//        右下角悬浮按钮
        FloatingActionButton fab = findViewById(R.id.edit_fab);
        fab.setOnClickListener(view -> {
//            包装数据
            Todo update_todo = new Todo(-1, 0, "0", title.getText().toString(), desc.getText().toString());
//            新建需要发送的message
            Message message = new Message();
//            新建数据包
            Bundle data = new Bundle();
//            装入数据
            data.putSerializable("info", update_todo);
//            装入数据包
            message.setData(data);
//            设置信件类型
            message.what = 514;
//            用MainActivity.handler发送到它的信息通道
            MainActivity.handler.sendMessage(message);
//            关闭界面
            finish();
        });
    }

//    设置左上角按钮响应
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}