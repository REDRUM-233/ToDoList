package com.redrum.todo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

public class TodoDetail extends AppCompatActivity {

    Todo todo;
    TextView title;
    TextView type;
    TextView desc;
    TextView checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        Intent intent = getIntent();
        todo = (Todo) intent.getSerializableExtra("Info");

        title = findViewById(R.id.detail_title);
        type = findViewById(R.id.detail_type);
        desc = findViewById(R.id.detail_desc);
        checked = findViewById(R.id.detail_checked);

        title.setText(todo.getTitle());
        type.setText(todo.getTypeDesc());
        checked.setText(todo.getChecked() == 1 ? "已完成" : "未完成");
        desc.setText(todo.getDesc());
    }
}