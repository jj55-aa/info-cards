package io.github.jj55_aa.info_cards;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InfoCardsWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            v.setTextViewText(R.id.widget_subtitle, "点击刷新 | " + time);
            Intent intent = new Intent(ctx, InfoCardsWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{id});
            v.setOnClickPendingIntent(R.id.widget_root,
                PendingIntent.getBroadcast(ctx, id, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
            mgr.updateAppWidget(id, v);
        }
    }
}
