package io.github.jj55_aa.info_cards;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class InfoCardsWidgetProvider extends AppWidgetProvider {
    private static final String[] CARDS = {
        "🟡 7.31 周五 15:30",
        "彭泽街道来访接待（21人）\n一楼·五楼展厅·深元411",
    };

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CARDS.length; i += 2) {
                if (i > 0) sb.append("\n");
                sb.append(CARDS[i]).append("\n").append(CARDS[i+1]);
            }
            v.setTextViewText(R.id.widget_subtitle, sb.toString());
            mgr.updateAppWidget(id, v);
        }
    }
}
