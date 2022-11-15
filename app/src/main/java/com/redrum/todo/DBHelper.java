package com.redrum.todo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.redrum.todo.model.Todo;

import java.util.ArrayList;
import java.util.List;

//它自带的只有数据库整体的创建和版本控制，
// 其他功能都是手写的
public class DBHelper extends SQLiteOpenHelper {

//    不知道
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

//    同一个数据库名字运行一次，不是应用的onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE `todo_info`  (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                "  `checked` integer ,\n" +
                "  `type` varchar(255) ,\n" +
                "  `title` varchar(255) ,\n" +
                "  `desc` varchar(255) \n" +
                ");\n");
        insertData(db, 0, "0", "欢迎使用ToDo待办事项", "");
        insertData(db, 0, "0", "右下角+号可以添加事项", "");
        insertData(db, 0, "0", "这是一个试图凑出两行来测试两行的布局是否好看的垃圾话", "");
    }

//    版本更新，不过我不会用，所以基本咩用上
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS `todo_info`");
        onCreate(db);
    }

//    手写的读取数据库所有数据
    public static List<Todo> getAllData(SQLiteDatabase db) {
        List<Todo> todoList = new ArrayList<>();
//        获得query指针
        Cursor cursor = db.rawQuery("select * from todo_info", null);
//        循环读取
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Todo todo = new Todo(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    todoList.add(todo);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return todoList;
    }

//    下面都是手写的增删改的配合sql语句的代码

    public static void insertData(SQLiteDatabase db, int checked, String type, String title, String desc) {
        db.execSQL(
                "insert into todo_info values(null, ?, ?, ?, ?);"
                , new String[]{checked + "", type, title, desc == null ? "" : desc});
    }

    public static void insertData(SQLiteDatabase db, Todo todo) {
        db.execSQL(
                "insert into todo_info values(null,?, ?, ?, ?);"
                , new String[]{todo.getChecked() + "", todo.getType(), todo.getTitle(), todo.getDesc() == null ? "" : todo.getDesc()});
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

    public static void updateData(SQLiteDatabase db, int checked, String type, String title, String desc, int id) {
        db.execSQL(
                "update todo_info set checked = ?, type = ?, title = ?, desc = ? where id = ?;"
                , new String[]{checked + "", type, title, desc == null ? "" : desc, id + ""});
    }

    public static void updateData(SQLiteDatabase db, Todo todo) {
        db.execSQL(
                "update todo_info set checked = ?, type = ?, title = ?, desc = ? where id = ?;"
                , new String[]{todo.getChecked() + "", todo.getType(), todo.getTitle(), todo.getDesc() == null ? "" : todo.getDesc(), todo.getId() + ""});
    }
}
