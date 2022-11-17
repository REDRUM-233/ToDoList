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

//主界面
public class MainActivity extends AppCompatActivity {
    //    主页滚动的部分view
    private RecyclerView todoRecyclerView;
    private TodoAdapter todoAdapter;

    //    半自动半手写数据库帮手
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    //    消息发送接收器
//    没想到这玩意要传给其他activity才能共享消息通道
//    所以给它设置成公共且静态的了
//    其他activity用MainActivity.handler调用
    public static Handler handler;

    TextView dateView;
    TextView status;
    ProgressBar progressBar;

    //    supress巴拉巴拉我也不知道是什么，但是有了之后会少一点警告
    @Override
    @SuppressLint({"HandlerLeak", "NotifyDataSetChanged", "SimpleDateFormat"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        设置显示的xml
        setContentView(R.layout.activity_main);

//        初始化数据库和数据库帮手
        dbHelper = new DBHelper(this, "temp_003.db3", null, 1);
//        获得可读写数据库
        db = dbHelper.getReadableDatabase();

//        消息处理机
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 114) {
//                    处理更新信息
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todoAdapter.updateData(todo);
                } else if (msg.what == 514) {
//                    处理新增信息
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todoAdapter.insertData(todo);
                    status_refresh();
                } else if (msg.what == 1919810) {
//                    处理删除信息
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todoAdapter.deleteData(todo);
                    status_refresh();
                }
            }
        };

//        滚动界面
        todoRecyclerView = findViewById(R.id.recycle_view);
//        设置内部管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        设置方向
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        todoRecyclerView.setLayoutManager(layoutManager);
//        设置数据和单个事项管理的adapter
        todoAdapter = new TodoAdapter(this, DBHelper.getAllData(db));
        todoRecyclerView.setAdapter(todoAdapter);

        //处理概览
        dateView = findViewById(R.id.main_overview_date);
        status = findViewById(R.id.main_overview_status);
        progressBar = findViewById(R.id.main_overview_progress);
        dateView.setText(new SimpleDateFormat("MM月dd日 EEEE").format(new Date()));
        status_refresh();

//        照抄的滑动机制
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new TouchHelper(todoAdapter));
        itemTouchHelper.attachToRecyclerView(todoRecyclerView);

//        右下角的悬浮按钮
        FloatingActionButton fab = findViewById(R.id.main_fab);
//        设置点击后打开添加界面
        fab.setOnClickListener(view -> {
//            启动add界面
            Intent addIntent = new Intent(this, TodoAdd.class);
            // 启动intent对应的Activity
            startActivity(addIntent);
        });

    }

    @SuppressLint("SetTextI18n")
    public void status_refresh() {
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

    @Override
    protected void onPause() {
        Log.d("cao", "run: 存了嘛你妈的");
        DBHelper.updateDataBase(db, todoAdapter.getTodoList());
        super.onPause();
    }

}