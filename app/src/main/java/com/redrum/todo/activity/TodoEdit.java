package com.redrum.todo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

public class TodoEdit extends AppCompatActivity {
    Todo todo;
    int what;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        EditText title = findViewById(R.id.edit_title);
        EditText desc = findViewById(R.id.edit_desc);
        Button button = findViewById(R.id.edit_type);

        Intent intent = getIntent();
        if (intent.getSerializableExtra("action").equals("update")) {
            todo = (Todo) intent.getSerializableExtra("Info");
            what = 114;
        } else {
            todo = new Todo(-1, 0, "0", "请填写标题", "");
            what = 514;
        }

        title.setText(todo.getTitle());
        desc.setText(todo.getDesc());
        button.setText(todo.getTypeDesc());
        button.setOnClickListener(view -> {
            ConstraintLayout type_select_layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.type_select, null);
            new AlertDialog.Builder(this)
                    .setIcon(null)
                    .setTitle("事件类型")
                    .setView(type_select_layout)
                    .setPositiveButton("确定", (dialog, witch) -> {
                        RadioGroup rg = type_select_layout.findViewById(R.id.type_select_group);
                        switch (rg.getCheckedRadioButtonId()) {
                            case R.id.type_select_other: {
                                EditText editText = type_select_layout.findViewById(R.id.type_select_edit);
                                int days;
                                try {
                                    days = Integer.parseInt(editText.getText().toString());
                                } catch (Exception e) {
                                    days = -1;
                                }
                                if (days == -1)
                                    todo = todo.changeType(0, "");
                                else
                                    todo = todo.changeType(4, days + "");
                                break;
                            }
                            case R.id.type_select_simple:
                                todo = todo.changeType(0, "");
                                break;
                            case R.id.type_select_weekly:
                                todo = todo.changeType(1, "0");
                                break;
                            case R.id.type_select_weekly_more: {
                                ConstraintLayout week_select_layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.week_select, null);
                                new AlertDialog.Builder(this)
                                        .setIcon(null)
                                        .setTitle("星期选择")
                                        .setView(week_select_layout)
                                        .setPositiveButton("确定", (week_dialog, week_witch) -> {
                                            String week_checked = "";
                                            CheckBox week1 = week_select_layout.findViewById(R.id.week1);
                                            if (week1.isChecked())
                                                week_checked += "1";
                                            CheckBox week2 = week_select_layout.findViewById(R.id.week2);
                                            if (week2.isChecked())
                                                week_checked += "2";
                                            CheckBox week3 = week_select_layout.findViewById(R.id.week3);
                                            if (week3.isChecked())
                                                week_checked += "3";
                                            CheckBox week4 = week_select_layout.findViewById(R.id.week4);
                                            if (week4.isChecked())
                                                week_checked += "4";
                                            CheckBox week5 = week_select_layout.findViewById(R.id.week5);
                                            if (week5.isChecked())
                                                week_checked += "5";
                                            CheckBox week6 = week_select_layout.findViewById(R.id.week6);
                                            if (week6.isChecked())
                                                week_checked += "6";
                                            CheckBox week0 = week_select_layout.findViewById(R.id.week0);
                                            if (week0.isChecked())
                                                week_checked += "7";
                                            if (week_checked.length() != 0) {
                                                todo = todo.changeType(1, week_checked);
                                                button.setText(todo.getTypeDesc());

                                            }
                                        })
                                        .setNegativeButton("取消", (week_dialog, week_witch) -> {
                                        }).create().show();
                            }
                            case R.id.type_select_monthly:
                                todo = todo.changeType(2, "");
                                break;
                            case R.id.type_select_yearly:
                                todo = todo.changeType(3, "");
                                break;
                        }
                        button.setText(todo.getTypeDesc());
                    })
                    .setNegativeButton("取消", (dialog, witch) -> {
                    }).create().show();
        });

        FloatingActionButton fab = findViewById(R.id.edit_fab);
        fab.setOnClickListener(view -> {
            Todo update_todo = new Todo(todo.getId(), todo.getChecked(), todo.getType(), title.getText().toString(), desc.getText().toString());
            Message message = new Message();
            Bundle data = new Bundle();
            data.putSerializable("info", update_todo);
            message.setData(data);
            message.what = what;
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