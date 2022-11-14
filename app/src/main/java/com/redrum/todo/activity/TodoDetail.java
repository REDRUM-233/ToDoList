package com.redrum.todo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

public class TodoDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Todo todo = (Todo) intent.getSerializableExtra("Info");

        TextView title = findViewById(R.id.detail_title);
        TextView type = findViewById(R.id.detail_type);
        TextView desc = findViewById(R.id.detail_desc);
        TextView checked = findViewById(R.id.detail_checked);

        title.setText(todo.getTitle());
        type.setText(todo.getTypeDesc());
        checked.setText(todo.getChecked() == 1 ? "已完成" : "未完成");
        desc.setText(todo.getDesc());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}