package com.redrum.todo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.redrum.todo.model.Todo;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE `todo_info`  (\n" +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                "  `type` varchar(255) ,\n" +
                "  `title` varchar(255) ,\n" +
                "  `desc` varchar(255)\n" +
                ");\n");
        this.insertData(db, "1", "欢迎使用ToDo待办事项", "");
        this.insertData(db, "1", "右下角+号可以添加事项", "");
        this.insertData(db, "1", "这是一个试图凑出两行来测试两行的布局是否好看的垃圾话", "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS `todo_info`");
        onCreate(db);
    }

    public static List<Todo> getAllData(SQLiteDatabase db) {
        List<Todo> todoList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from todo_info", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Todo todo = new Todo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
                    todoList.add(todo);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return todoList;
    }

    public static void insertData(SQLiteDatabase db, String type, String title, @Nullable String desc) {
        db.execSQL(
                "insert into todo_info values(null, ?, ?, ?);"
                , new String[]{type, title, desc == null ? "" : desc});
    }

    public static void insertData(SQLiteDatabase db, Todo todo) {
        db.execSQL(
                "insert into todo_info values(null, ?, ?, ?);"
                , new String[]{todo.getType(), todo.getTitle(), todo.getDesc() == null ? "" : todo.getDesc()});
    }

    public static void deleteData(SQLiteDatabase db, int id) {
        db.execSQL(
                "delete from todo_info where id=?;"
                , new String[]{id + ""});
    }

    public static void deleteData(SQLiteDatabase db, Todo todo) {
        db.execSQL(
                "delete from todo_info where id=?;"
                , new String[]{todo.getId() + ""});
    }

    public static void updateData(SQLiteDatabase db, String type, String title, String desc, int id) {
        db.execSQL(
                "update todo_info set type = ?, title = ?, desc = ? where id = ?;"
                , new String[]{type, title, desc == null ? "" : desc, id + ""});
    }

    public static void updateData(SQLiteDatabase db, Todo todo) {
        db.execSQL(
                "update todo_info set type = ?, title = ?, desc = ? where id = ?;"
                , new String[]{todo.getType(), todo.getTitle(), todo.getDesc() == null ? "" : todo.getDesc(), todo.getId() + ""});
    }
}
