package com.redrum.todo.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redrum.todo.R;
import com.redrum.todo.model.Todo;

public class TodoAdd extends AppCompatActivity {

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

        FloatingActionButton fab = findViewById(R.id.edit_fab);
        fab.setOnClickListener(view -> {
            Todo update_todo = new Todo(-1, "1", title.getText().toString(), desc.getText().toString());
            Message message = new Message();
            Bundle data = new Bundle();
            data.putSerializable("info", update_todo);
            message.setData(data);
            message.what = 514;
            MainActivity.handler.sendMessage(message);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}