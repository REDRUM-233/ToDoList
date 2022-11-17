package com.redrum.todo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
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
import com.redrum.todo.R;
import com.redrum.todo.TouchHelper;
import com.redrum.todo.adapter.TodoAdapter;
import com.redrum.todo.model.Todo;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private TodoAdapter todoAdapter_checked;
    private Button checked_switch;
    RecyclerView todoRecyclerView_checked;
    RecyclerView todoRecyclerView_unchecked;

    private TodoAdapter todoAdapter_unchecked;
    public static Handler handler;

    TextView dateView;
    TextView status;
    ProgressBar progressBar;

    @Override
    @SuppressLint({"HandlerLeak", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(this, "temp_2.db3", null, 1);
        db = dbHelper.getReadableDatabase();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 114) {
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    if (todo.getChecked() == 1)
                        todoAdapter_checked.updateData(todo);
                    else
                        todoAdapter_unchecked.updateData(todo);
                } else if (msg.what == 514) {
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    if (todo.getChecked() == 1)
                        todoAdapter_checked.insertData(todo);
                    else
                        todoAdapter_unchecked.insertData(todo);
                    status_refresh();
                } else if (msg.what == 1919810) {
//                    处理删除信息
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    if (todo.getChecked() == 1)
                        todoAdapter_checked.deleteData(todo);
                    else
                        todoAdapter_unchecked.deleteData(todo);
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
        checked_switch.setVisibility(View.GONE);

        todoRecyclerView_checked = findViewById(R.id.recycle_view_checked);
        todoRecyclerView_checked.setVisibility(View.GONE);
        LinearLayoutManager layoutManager_checked = new LinearLayoutManager(this);
        layoutManager_checked.setOrientation(LinearLayoutManager.VERTICAL);
        todoRecyclerView_checked.setLayoutManager(layoutManager_checked);
        todoAdapter_checked = new TodoAdapter(this, 1, db);
        todoRecyclerView_checked.setAdapter(todoAdapter_checked);

        todoRecyclerView_unchecked = findViewById(R.id.recycle_view_unchecked);
        LinearLayoutManager layoutManager_unchecked = new LinearLayoutManager(this);
        layoutManager_unchecked.setOrientation(LinearLayoutManager.VERTICAL);
        todoRecyclerView_unchecked.setLayoutManager(layoutManager_unchecked);
        todoAdapter_unchecked = new TodoAdapter(this, 0, db);
        todoRecyclerView_unchecked.setAdapter(todoAdapter_unchecked);


        checked_switch.setOnClickListener(view -> {
            if (todoRecyclerView_checked.getVisibility() == View.VISIBLE) {
                todoRecyclerView_checked.setVisibility(View.GONE);
                checked_switch.setBackground(getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));
            } else {
                todoRecyclerView_checked.setVisibility(View.VISIBLE);
                checked_switch.setBackground(getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
            }
        });

        ItemTouchHelper itemTouchHelper_checked = new
                ItemTouchHelper(new TouchHelper(todoAdapter_checked));
        itemTouchHelper_checked.attachToRecyclerView(todoRecyclerView_checked);

        ItemTouchHelper itemTouchHelper_unchecked = new
                ItemTouchHelper(new TouchHelper(todoAdapter_unchecked));
        itemTouchHelper_unchecked.attachToRecyclerView(todoRecyclerView_unchecked);

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

    @SuppressLint("SetTextI18n")
    public void status_refresh() {
        int checked = todoAdapter_checked.getTodoList().size();
        int unchecked = todoAdapter_unchecked.getTodoList().size();
        status.setText("今天已完成" + checked + "个待办事项（共" + (checked + unchecked) + "个）");
        progressBar.setProgress((int) Math.floor(100.0 * checked / (checked + unchecked)));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void recycle_view_refresh(int type, int position) {
        if (type == 0) {
            Todo temp = todoAdapter_unchecked.deleteData(position);
            todoAdapter_checked.insertData(temp);
            todoAdapter_unchecked.notifyItemRemoved(position);
            todoAdapter_checked.notifyItemInserted(0);
        } else {
            Todo temp = todoAdapter_checked.deleteData(position);
            todoAdapter_unchecked.insertData(temp);
            todoAdapter_checked.notifyItemRemoved(position);
            todoAdapter_unchecked.notifyItemInserted(0);
        }
    }

    public void show_switch(boolean type) {
        if (type)
            checked_switch.setVisibility(View.VISIBLE);
        else
            checked_switch.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        Log.d("cao", "run: 存了嘛你妈的");
        DBHelper.updateDataBase(db, todoAdapter_checked.getTodoList(), todoAdapter_unchecked.getTodoList());
        super.onPause();
    }
}