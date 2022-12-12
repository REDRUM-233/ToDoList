package com.redrum.todo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.redrum.todo.model.Todo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DesktopService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DesktopRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    public class DesktopRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private Intent intent;
        private List<Todo> todoList;
        private SQLiteDatabase db;

        public DesktopRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        @Override
        public void onCreate() {
            // 数据库
            DBHelper dbHelper = new DBHelper(context, getString(R.string.db), null, 1);
            db = dbHelper.getReadableDatabase();
            todoList = new ArrayList<>();
            List<Todo> tempList = DBHelper.getAllData(db);
            db.close();
            for (Todo i : tempList)
                if (i.isShowable(new Date())) {
                    if (i.getChecked() == 0)
                        todoList.add(i);
                } else if (i.isRenewable()) {
                    Todo temp = i.renew();
                    temp.setChecked(0);
                    todoList.add(i);
                }
        }

        @Override
        public void onDataSetChanged() {
            // 数据库
            DBHelper dbHelper = new DBHelper(context, getString(R.string.db), null, 1);
            db = dbHelper.getReadableDatabase();
            todoList.clear();
            List<Todo> tempList = DBHelper.getAllData(db);
            for (Todo i : tempList) {
                if (i.isShowable(new Date())) {
                    if (i.getChecked() == 0)
                        todoList.add(i);
                } else if (i.isRenewable()) {
                    Log.d("cao", "onDataSetChanged: "+i.getTypeDesc());
                    Todo temp = i.renew();
                    temp.setChecked(0);
                    Log.d("cao", "onDataSetChanged: "+i.getTypeDesc());
                    DBHelper.updateData(db, temp);
                    todoList.add(i);
                }
            }
        }

        @Override
        public void onDestroy() {
            todoList.clear();
        }

        @Override
        public int getCount() {
            return todoList.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item);
            rv.setTextViewText(R.id.widget_title, todoList.get(i).getTitle());
            // 跳转详情
            Intent detail_intent = new Intent(context, DesktopProvider.class);
            detail_intent.setAction(DesktopProvider.ITEM_INTENT);
            Bundle data = new Bundle();
            data.putSerializable("type", DesktopProvider.JUMP2DETAIL);
            data.putSerializable("Info", todoList.get(i));
            detail_intent.putExtras(data);
//            设置点击发送intent
            rv.setOnClickFillInIntent(R.id.widget_title, detail_intent);
            // 勾选
            Intent check_intent = new Intent(context, DesktopProvider.class);
            check_intent.setAction(DesktopProvider.ITEM_INTENT);
            Bundle check_data = new Bundle();
            check_data.putSerializable("type", DesktopProvider.ITEM_CHECKED_ACTION);
            check_data.putSerializable("Info", todoList.get(i));
            check_intent.putExtras(check_data);
//            设置点击发送intent
            rv.setOnClickFillInIntent(R.id.widget_check_box, check_intent);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
