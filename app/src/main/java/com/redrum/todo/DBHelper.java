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

    public static int getCheckedCount(SQLiteDatabase db) {
        int cnt = 0;
        for (Todo i : getAllData(db))
            cnt += i.getChecked();
        return cnt;
    }

    public static void updateDataBase(SQLiteDatabase db, List<Todo> list) {
        db.execSQL("drop table todo_info ");
        db.execSQL("CREATE TABLE `todo_info`  (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                "  `checked` integer ,\n" +
                "  `type` varchar(255) ,\n" +
                "  `title` varchar(255) ,\n" +
                "  `desc` varchar(255) \n" +
                ");\n");
        for (Todo i : list) {
            insertData(db, i);
        }
    }
}
