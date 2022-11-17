package com.redrum.todo.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoEdit extends AppCompatActivity {
    private Todo todo;
    private int what;
    private ConstraintLayout type_select_layout;
    private ConstraintLayout week_select_layout;
    private Dialog select_type_dialog;
    private Dialog select_week_dialog;
    private List<RadioButton> radioButtonList;

    @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility", "InflateParams"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);

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

        type_select_layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.type_select, null);
        week_select_layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.week_select, null);
        Context context = this;

        select_type_dialog = new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle("事件类型")
                .setView(type_select_layout)
                .setPositiveButton("确定", (dialog, witch) -> {
                    int checked_id = 0;
                    for (RadioButton i : radioButtonList)
                        if (i.isChecked())
                            checked_id = i.getId();
                    switch (checked_id) {
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
                            Log.d("cao", "onCreate: 怎么不是每周事件捏");
                            todo = todo.changeType(1, "0");
                            break;
                        case R.id.type_select_weekly_more: {
                            break;
                        }
                        case R.id.type_select_monthly:
                            todo = todo.changeType(2, "");
                            break;
                        case R.id.type_select_yearly:
                            todo = todo.changeType(3, "");
                            break;
                        case R.id.type_select_infinite:
                            todo = todo.changeType(4, "9999999");
                    }
                    button.setText(todo.getTypeDesc());
                })
                .setNegativeButton("取消", (dialog, witch) -> {
                }).create();

        select_week_dialog = new AlertDialog.Builder(context)
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
                }).create();


        button.setOnClickListener(view -> select_type_dialog.show());

        radioButtonList = new ArrayList<>();
        radioButtonList.add(type_select_layout.findViewById(R.id.type_select_weekly_more));
        radioButtonList.add(type_select_layout.findViewById(R.id.type_select_other));
        radioButtonList.add(type_select_layout.findViewById(R.id.type_select_simple));
        radioButtonList.add(type_select_layout.findViewById(R.id.type_select_weekly));
        radioButtonList.add(type_select_layout.findViewById(R.id.type_select_monthly));
        radioButtonList.add(type_select_layout.findViewById(R.id.type_select_yearly));
        radioButtonList.add(type_select_layout.findViewById(R.id.type_select_infinite));
        for (RadioButton i : radioButtonList) {
            i.setOnClickListener(view -> {
                for (RadioButton j : radioButtonList)
                    if (j != i)
                        j.setChecked(false);
                if (i == type_select_layout.findViewById(R.id.type_select_weekly_more)) {
                    select_type_dialog.dismiss();
                    select_week_dialog.show();
                }
            });
        }

        @SuppressLint("CutPasteId") EditText others_edit = type_select_layout.findViewById(R.id.type_select_edit);
        others_edit.setOnFocusChangeListener((view, b) -> {
            if(b){
                for (RadioButton j : radioButtonList)
                    j.setChecked(false);
                RadioButton other_button = type_select_layout.findViewById(R.id.type_select_other);
                other_button.setChecked(true);
            }
        });
        others_edit.setOnClickListener(view -> {
            for (RadioButton j : radioButtonList)
                j.setChecked(false);
            RadioButton other_button = type_select_layout.findViewById(R.id.type_select_other);
            other_button.setChecked(true);
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