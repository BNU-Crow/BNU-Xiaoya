package com.xuhongxu.xiaoya;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.xuhongxu.xiaoya.Service.TimetableAppWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class TimetableListAppWidget extends AppWidgetProvider {

    public static final String SYNC_CLICKED = "update_widget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_timetable_list);

        Intent intent = new Intent(context, TimetableAppWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.listview, intent);
        views.setEmptyView(R.id.listview, R.id.empty_view);

        views.setOnClickPendingIntent(R.id.refresh_button, getPendingSelfIntent(context, SYNC_CLICKED));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, TimetableStackAppWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(SYNC_CLICKED)) {
            ComponentName thisAppWidget = new ComponentName(context, TimetableStackAppWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int []ids = manager.getAppWidgetIds(thisAppWidget);
            manager.notifyAppWidgetViewDataChanged(ids, R.id.listview);
            onUpdate(context, manager, ids);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

