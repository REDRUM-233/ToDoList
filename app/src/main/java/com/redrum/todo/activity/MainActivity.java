package com.redrum.todo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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


public class MainActivity extends AppCompatActivity {
    private RecyclerView todoRecyclerView;
    private TodoAdapter todoAdapter;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    public static Handler handler;
    private Intent editIntent;
    private Intent detailIntent;

    @Override
    @SuppressLint({"HandlerLeak", "NotifyDataSetChanged"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this, "temp_002.db3", null, 1);
        db = dbHelper.getReadableDatabase();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 114) {
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    DBHelper.updateData(db, todo);
                } else if (msg.what == 514) {
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    DBHelper.insertData(db, todo);
                }
                else if(msg.what==1919810){
                    Bundle data = msg.getData();
                    Todo todo = (Todo) data.getSerializable("info");
                    DBHelper.deleteData(db, todo);
                }
                todoAdapter.refreash();
                todoAdapter.notifyDataSetChanged();
            }
        };

        detailIntent = new Intent(this, TodoDetail.class);
        editIntent = new Intent(this, TodoEdit.class);

        todoRecyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        todoRecyclerView.setLayoutManager(layoutManager);
        todoAdapter = new TodoAdapter(this, detailIntent, editIntent, db);
        todoRecyclerView.setAdapter(todoAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new TouchHelper(todoAdapter));
        itemTouchHelper.attachToRecyclerView(todoRecyclerView);

        FloatingActionButton fab = findViewById(R.id.main_fab);
        fab.setOnClickListener(view -> {
            Intent addIntent = new Intent(this, TodoAdd.class);
            // 启动intent对应的Activity
            startActivity(addIntent);
        });
    }
}