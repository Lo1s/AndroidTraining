package com.example.android.androidtraining;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by jslapnicka on 29.7.2015.
 */
public class AppWidgetProviderTest extends AppWidgetProvider {

    // TODO: Try the configuration activity which is opened when the widget is created for the first time
    // http://developer.android.com/guide/topics/appwidgets/index.html#Configuring
    // TODO: Try the advanced widget collections (e.g. scrollview within widget)

    private static int count = 0;
    private static final String ACTION_UPDATE_CLICK = "com.example.android.androidtraining.action.ACTION_UPDATE_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;
        String countString = getMessage();

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.textView_widget, countString);
            remoteViews.setOnClickPendingIntent(R.id.button_widget, getPendingSelfIntent(context, ACTION_UPDATE_CLICK));

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private void onUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName componentName = new ComponentName(context.getPackageName(), getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_UPDATE_CLICK.equals(intent.getAction())) {
            onUpdate(context);
        }
    }

    private static String getMessage() {
        return "Count: " + String.valueOf(MainActivity.incrementCount());
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
