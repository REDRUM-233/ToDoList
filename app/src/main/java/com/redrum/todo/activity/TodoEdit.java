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

//        说过了
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

//        跟add同理
        EditText title = findViewById(R.id.edit_title);
        EditText desc = findViewById(R.id.edit_desc);

//        获得需要修改的todo
        Intent intent = getIntent();
        Todo todo = (Todo) intent.getSerializableExtra("Info");

//        唯一跟add不一样的就是把数据给拿过来显示了
        title.setText(todo.getTitle());
        desc.setText(todo.getDesc());

        FloatingActionButton fab = findViewById(R.id.edit_fab);
        fab.setOnClickListener(view -> {
            Todo update_todo = new Todo(todo.getId(), todo.getChecked(), todo.getType(), title.getText().toString(), desc.getText().toString());
            Message message = new Message();
            Bundle data = new Bundle();
            data.putSerializable("info", update_todo);
            message.setData(data);
            message.what = 114;
            Handler handler = MainActivity.handler;
            handler.sendMessage(message);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}