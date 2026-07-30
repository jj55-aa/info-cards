package io.github.jj55_aa.info_cards;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class InfoCardsWidgetProvider extends AppWidgetProvider {
    private static final String[] CARDS = new String[]{
        "7.31 周五 彭泽街道 · 15:30",
        "来访接待（21人）| 一楼/五楼展厅/深元411 | 杨宇飞",
    };

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CARDS.length; i += 2) {
                sb.append(CARDS[i]).append("\n").append(CARDS[i+1]);
                if (i + 2 < CARDS.length) sb.append("\n\n");
            }
            v.setTextViewText(R.id.widget_subtitle, sb.toString());
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
