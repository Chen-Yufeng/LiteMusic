package swipedelmenu.mcxtzhang.litemusic;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import static swipedelmenu.mcxtzhang.litemusic.service.MediaPlayerService.ACTION_NEXT;
import static swipedelmenu.mcxtzhang.litemusic.service.MediaPlayerService.ACTION_PAUSE;
import static swipedelmenu.mcxtzhang.litemusic.service.MediaPlayerService.ACTION_PLAY;
import static swipedelmenu.mcxtzhang.litemusic.service.MediaPlayerService.ACTION_PREVIOUS;

/**
 * Implementation of App Widget functionality.
 */
public class ControlWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.control_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.widget_pause,playbackAction(1,context));
        views.setOnClickPendingIntent(R.id.widget_skip_to_previous,playbackAction(3,context));
        views.setOnClickPendingIntent(R.id.widget_skip_to_next,playbackAction(2,context));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
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

    private static PendingIntent playbackAction(int actionNumber, Context context) {
        Intent playbackAction = new Intent();
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                playbackAction.putExtra("todo", 0);
                return PendingIntent.getBroadcast(context, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                playbackAction.putExtra("todo", 1);
                return PendingIntent.getBroadcast(context, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                playbackAction.putExtra("todo", 2);
                return PendingIntent.getBroadcast(context, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                playbackAction.putExtra("todo", 3);
                return PendingIntent.getBroadcast(context, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;

    }

}