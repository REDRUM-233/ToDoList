package com.redrum.todo.activity;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

public class TodoDetail extends AppCompatActivity {
    Todo todo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

//        状态栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
//        启用按钮
        actionBar.setHomeButtonEnabled(true);
//        显示左上角退出
        actionBar.setDisplayShowHomeEnabled(true);
//        显示右上角按钮（不过他妈的好像没用
        actionBar.setDisplayHomeAsUpEnabled(true);

//        获得启动自己的intent，取出数据
        Intent intent = getIntent();
        todo = (Todo) intent.getSerializableExtra("Info");

//        修改文本
        TextView title = findViewById(R.id.detail_title);
        TextView desc = findViewById(R.id.detail_desc);
        TextView checked = findViewById(R.id.detail_checked);

        title.setText(todo.getTitle());
        checked.setText(todo.getChecked() == 1 ? "已完成" : "未完成");
        desc.setText(todo.getDesc());
    }

//    重写这个试图让右上角按钮显示，但是失败了也懒得删了
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    顶栏按钮响应
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            右上角退出
            case android.R.id.home:
                finish();
                break;
//                修改
            case R.id.detail_menu_edit: {
//                创建intent用于启动activity
                Intent intent = new Intent(this, TodoEdit.class);
//                打包数据
                Bundle data = new Bundle();
                data.putSerializable("Info", todo);
//                装数据
                intent.putExtras(data);
//                启动
                startActivity(intent);
//                关闭这个界面
                finish();
                break;
//                删除
            }case R.id.detail_menu_delete:{
//                这一大坨直接复制粘贴的滑动响应
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("删除");
                builder.setMessage("是否删除该Todo？");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                说过了
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