package io.github.jj55_aa.info_cards;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class InfoCardsWidgetProvider extends AppWidgetProvider {
    // Format: {source, summary, source, summary, ...}
    private static final String[] CARDS = {
        "7.31 周五 彭泽街道 15:30 🟡",
        "来访接待（21人）| 一楼/五楼展厅/深元411 | 杨宇飞",
    };

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
            int[][] ids_map = {
                {R.id.slot_0, R.id.slot_0_src, R.id.slot_0_sum, R.id.slot_0_bar},
                {R.id.slot_1, R.id.slot_1_src, R.id.slot_1_sum, R.id.slot_1_bar},
                {R.id.slot_2, R.id.slot_2_src, R.id.slot_2_sum, R.id.slot_2_bar},
            };
            int slots = Math.min(CARDS.length / 2, ids_map.length);
            for (int i = 0; i < slots; i++) {
                v.setInt(ids_map[i][0], "setVisibility", 0); // visible
                v.setTextViewText(ids_map[i][1], CARDS[i * 2]);
                v.setTextViewText(ids_map[i][2], CARDS[i * 2 + 1]);
                // color bar: yellow for mid, red for high, blue for low
                int color = CARDS[i * 2].contains("🔴") ? 0xffff4d4d :
                            CARDS[i * 2].contains("🔵") ? 0xff4d94ff : 0xfff0a500;
                v.setInt(ids_map[i][3], "setBackgroundColor", color);
            }
            mgr.updateAppWidget(id, v);
        }
    }
}
