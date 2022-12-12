package com.redrum.todo.activity;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redrum.todo.DBHelper;
import com.redrum.todo.DesktopProvider;
import com.redrum.todo.R;
import com.redrum.todo.TouchHelper;
import com.redrum.todo.adapter.TodoAdapter;
import com.redrum.todo.model.Todo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;


public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private TodoAdapter todoAdapter;
    private Button checked_switch;
    RecyclerView todoRecyclerView;

    public static Handler handler;

    TextView dateView;
    TextView status;
    ProgressBar progressBar;

    @Override
    @SuppressLint({"HandlerLeak", "NotifyDataSetChanged", "UseCompatLoadingForDrawables", "SimpleDateFormat"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(this, getString(R.string.db), null, 1);
        db = dbHelper.getReadableDatabase();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 114) {
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todoAdapter.updateData(todo);
                } else if (msg.what == 514) {
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todo.setId(todoAdapter.getItemCount() + todoAdapter.getItemCount());
                    todoAdapter.insertData(todo);
                    status_refresh();
                } else if (msg.what == 1919) {
//                    处理删除信息
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    todoAdapter.deleteData(todo);
                    status_refresh();
                }
            }
        };

        //处理概览
        dateView = findViewById(R.id.main_overview_date);
        status = findViewById(R.id.main_overview_status);
        progressBar = findViewById(R.id.main_overview_progress);
        dateView.setText(new SimpleDateFormat("MM月dd日 EEEE").format(new Date()));

        checked_switch = findViewById(R.id.main_checked_switch);

        todoRecyclerView = findViewById(R.id.main_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        todoRecyclerView.setLayoutManager(layoutManager);
        todoAdapter = new TodoAdapter(this, 1, db);
        todoAdapter.changeType(0);
        todoRecyclerView.setAdapter(todoAdapter);

        status_refresh();

        checked_switch.setOnClickListener(view -> {
            if (todoAdapter.getAdapterType() == 0)
                checked_switch.setBackground(getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));
            else
                checked_switch.setBackground(getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
            todoAdapter.changeType(~todoAdapter.getAdapterType() & 1);
        });

        ItemTouchHelper itemTouchHelper_unchecked = new
                ItemTouchHelper(new TouchHelper(todoAdapter));
        itemTouchHelper_unchecked.attachToRecyclerView(todoRecyclerView);

        FloatingActionButton fab = findViewById(R.id.main_fab);
        fab.setOnClickListener(view -> {
            Bundle data = new Bundle();
            data.putSerializable("action", "add");
            Intent intent = new Intent(this, TodoEdit.class);
            intent.putExtras(data);
            // 启动intent对应的Activity
            startActivity(intent);
        });
    }

    int before, after, pace;
    Timer timer;

    @SuppressLint("SetTextI18n")
    public void status_refresh() {
        int size = todoAdapter.getTodoList().size();
        int unchecked = size - todoAdapter.getUnchecked().size();
        status.setText("今天已完成" + unchecked + "个待办事项（共" + size + "个）");
        timer = new Timer();
        before = progressBar.getProgress();
        after = (int) Math.floor(100.0 * unchecked / size);
        pace = before < after ? 1 : -1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (before != after) {
                    progressBar.setProgress(before += pace);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        Log.d("cao", "onResume: ");
        todoAdapter.notifyDataSetChanged();
        status_refresh();
        super.onResume();
    }

    @Override
    protected void onPause() {
        DBHelper.updateDataBase(db, todoAdapter.getTodoList());
//        刷新桌面组件
        AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        ComponentName cn = new ComponentName(this, DesktopProvider.class);
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_list);
        super.onPause();
    }
}