package com.redrum.todo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.redrum.todo.activity.MainActivity;
import com.redrum.todo.activity.SplashActivity;
import com.redrum.todo.activity.TodoDetail;
import com.redrum.todo.activity.TodoEdit;
import com.redrum.todo.model.Todo;

public class DesktopProvider extends AppWidgetProvider {
    //    定义一系列Action
//    item点击
    public static final String ITEM_INTENT = "com.redrum.todo.ITEM_INTENT";
    //    checkbox点击
    public static final String ITEM_CHECKED_ACTION = "com.redrum.todo.ITEM_CHECKED_ACTION";
    //    右上角跳转主页
    public static final String JUMP2HOME = "com.redrum.todo.JUMP2HOME";
    //    右上角跳转添加
    public static final String JUMP2ADD = "com.redrum.todo.JUMP2ADD";
    //    点击文本跳转详情
    public static final String JUMP2DETAIL = "com.redrum.todo.JUMP2DETAIL";
    //    右上角刷新
    public static final String LIST_UPDATE = "com.redrum.todo.LIST_UPDATE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // 创建RemoteViews对象，加载/res/layout目录下的widget_layout.xml文件
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent list_intent = new Intent(context, DesktopService.class);
        // 使用intent更新rv中的list_view组件
        rv.setRemoteAdapter(R.id.widget_list, list_intent);

//         设置列表项为空时，直接显示empty_view组件
        rv.setEmptyView(R.id.widget_list, R.id.widget_empty);

//        跳转主页
        Intent home_intent = new Intent(context, DesktopProvider.class);
        home_intent.setAction(JUMP2HOME);
//        设置监听intent
        // 将Intent包装成PendingIntent
        PendingIntent home_pending_intent = PendingIntent.getBroadcast(context,
                0, home_intent, 0);
        rv.setPendingIntentTemplate(R.id.widget_home, home_pending_intent);
//        设置点击发送intent
        rv.setOnClickPendingIntent(R.id.widget_home, home_pending_intent);

//        跳转添加
        Intent add_intent = new Intent(context, DesktopProvider.class);
        add_intent.setAction(JUMP2ADD);
        // 将Intent包装成PendingIntent
        PendingIntent add_pending_intent = PendingIntent.getBroadcast(context,
                0, add_intent, 0);
        rv.setPendingIntentTemplate(R.id.widget_add, add_pending_intent);
        rv.setOnClickPendingIntent(R.id.widget_add, add_pending_intent);

//        刷新
        Intent refresh_intent = new Intent(context, DesktopProvider.class);
        refresh_intent.setAction(LIST_UPDATE);
        // 将Intent包装成PendingIntent
        PendingIntent refresh_pending_intent = PendingIntent.getBroadcast(context,
                0, refresh_intent, 0);
        rv.setPendingIntentTemplate(R.id.widget_refresh, refresh_pending_intent);
        rv.setOnClickPendingIntent(R.id.widget_refresh, refresh_pending_intent);

//        列表响应
        Intent item_intent = new Intent(context, DesktopProvider.class);
        item_intent.setAction(DesktopProvider.ITEM_INTENT);
        // 将Intent包装成PendingIntent
        PendingIntent item_pending_intent = PendingIntent.getBroadcast(context,
                0, item_intent, 0);
        rv.setPendingIntentTemplate(R.id.widget_list, item_pending_intent);


        // 使用AppWidgetManager通过RemoteViews更新AppWidgetProvider
        appWidgetManager.updateAppWidget(new ComponentName(context,
                DesktopProvider.class), rv);  // ②
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // 重写该方法，将该组件当成BroadcastReceiver使用
//    对接收到的intent进行响应
    @Override
    public void onReceive(Context context, Intent intent) {
        if (JUMP2HOME.equals(intent.getAction())) {
            Intent home_intent = new Intent(context, SplashActivity.class);
//            设置创建新进程
            home_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(home_intent);
        } else if (JUMP2ADD.equals(intent.getAction())) {
            Intent home_intent = new Intent(context, MainActivity.class);
            home_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(home_intent);

            Intent add_intent = new Intent(context, TodoEdit.class);
            add_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle data = new Bundle();
            data.putSerializable("action", "add");
            add_intent.putExtras(data);
            context.startActivity(add_intent);
        } else if (ITEM_INTENT.equals(intent.getAction())) {
            if (JUMP2DETAIL.equals(intent.getSerializableExtra("type"))) {
                Intent home_intent = new Intent(context, MainActivity.class);
                home_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(home_intent);

                Intent detail_intent = new Intent(context, TodoDetail.class);
                detail_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle data = new Bundle();
                data.putSerializable("Info", intent.getSerializableExtra("Info"));
                detail_intent.putExtras(data);
                context.startActivity(detail_intent);
            } else if (ITEM_CHECKED_ACTION.equals(intent.getSerializableExtra("type"))) {
                // 数据库
                DBHelper dbHelper = new DBHelper(context, context.getString(R.string.db), null, 1);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Todo todo = (Todo) intent.getSerializableExtra("Info");
                todo.setChecked(1);
                DBHelper.updateData(db, todo);

               Message message = new Message();
                Bundle data = new Bundle();
                data.putSerializable("info", todo);
                message.setData(data);
                message.what = 114;
                Handler handler = MainActivity.handler;
                handler.sendMessage(message);

//                调用notifyAppWidgetViewDataChanged刷新listview
                AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                ComponentName cn = new ComponentName(context, DesktopProvider.class);
                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_list);
            }
        } else if (LIST_UPDATE.equals(intent.getAction())) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, DesktopProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_list);
        }
        super.onReceive(context, intent);
    }
}
