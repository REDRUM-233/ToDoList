package com.redrum.todo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redrum.todo.DBHelper;
import com.redrum.todo.R;
import com.redrum.todo.TouchHelper;
import com.redrum.todo.adapter.TodoAdapter;
import com.redrum.todo.model.Todo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// 主界面
public class MainActivity extends AppCompatActivity {
    // 主页滚动的部分view
    private RecyclerView todoRecyclerView;
    private TodoAdapter todoAdapter;

    // 数据库管理
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    // 消息发送接收
    public static Handler handler;

    // 概览组件
    TextView dateView;
    TextView status;
    ProgressBar progressBar;

    @Override
    @SuppressLint({"HandlerLeak", "NotifyDataSetChanged", "SimpleDateFormat"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据库
        dbHelper = new DBHelper(this, "temp_003.db3", null, 1);
        // 获得可读写数据库
        db = dbHelper.getReadableDatabase();

        // 初始化消息处理
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 114) {
                    // 处理更新信息
                    // 打包数据并交由todoAdapter处理
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todoAdapter.updateData(todo);
                } else if (msg.what == 514) {
                    // 处理新增信息
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todoAdapter.insertData(todo);
                    // 刷新概览栏数据
                    status_refresh();
                } else if (msg.what == 1919810) {
                    // 处理删除信息
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todoAdapter.deleteData(todo);
                    status_refresh();
                }
            }
        };


        // 创建滚动界面
        todoRecyclerView = findViewById(R.id.recycle_view);
        // 创建内部布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 设置布局方向
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // 设置布局管理
        todoRecyclerView.setLayoutManager(layoutManager);
        // 设置adapter
        todoAdapter = new TodoAdapter(this, DBHelper.getAllData(db));
        todoRecyclerView.setAdapter(todoAdapter);


        //处理概览
        dateView = findViewById(R.id.main_overview_date);
        status = findViewById(R.id.main_overview_status);
        progressBar = findViewById(R.id.main_overview_progress);
        dateView.setText(new SimpleDateFormat("MM月dd日 EEEE").format(new Date()));
        status_refresh();


        // 事项滑动事件
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(todoAdapter));
        itemTouchHelper.attachToRecyclerView(todoRecyclerView);


        // 右下角的悬浮按钮
        FloatingActionButton fab = findViewById(R.id.main_fab);
        // 点击后跳转新增界面TodoAdapter
        fab.setOnClickListener(view -> {
            // 创建intent并启动新增界面TodoAdd的activity
            Intent addIntent = new Intent(this, TodoAdd.class);
            startActivity(addIntent);
        });

    }


    // 概览栏刷新
    @SuppressLint("SetTextI18n")
    public void status_refresh() {
        // 创建处理线程以免影响UI线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Todo> todoList = todoAdapter.getTodoList();
                int cnt = 0;
                int size = todoList.size();
                for (Todo i : todoList)
                    cnt += i.getChecked();
                status.setText("今天已完成" + cnt + "个待办事项（共" + size + "个）");
                progressBar.setProgress((int) Math.floor(100.0 * cnt / size));
            }
        }).run();
    }

    // 在Pause状态空隙刷新数据库
    @Override
    protected void onPause() {
        DBHelper.updateDataBase(db, todoAdapter.getTodoList());
        super.onPause();
    }

}