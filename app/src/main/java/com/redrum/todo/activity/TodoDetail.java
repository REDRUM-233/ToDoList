package com.redrum.todo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

public class TodoDetail extends AppCompatActivity {
    Todo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        todo = (Todo) intent.getSerializableExtra("Info");

        TextView title = findViewById(R.id.detail_title);
        TextView desc = findViewById(R.id.detail_desc);
        TextView checked = findViewById(R.id.detail_checked);

        title.setText(todo.getTitle());
        checked.setText(todo.getChecked() == 1 ? "已完成" : "未完成");
        desc.setText(todo.getDesc());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.detail_menu_edit: {
                Intent intent = new Intent(this, TodoEdit.class);
                Bundle data = new Bundle();
                data.putSerializable("Info", todo);
                intent.putExtras(data);
                startActivity(intent);
                finish();
                break;
            }case R.id.detail_menu_delete:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("删除");
                builder.setMessage("是否删除该Todo？");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Message message = new Message();
                                Bundle data = new Bundle();
                                data.putSerializable("info", todo);
                                message.setData(data);
                                message.what = 1919810;
                                Handler handler = MainActivity.handler;
                                handler.sendMessage(message);
                                finish();
                            }
                        });
                builder.setNegativeButton("返回",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        return true;
    }
}