package io.github.jj55_aa.info_cards;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

public class InfoCardsWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
            Intent intent = new Intent(ctx, InfoCardsWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{id});
            views.setOnClickPendingIntent(R.id.widget_root,
                PendingIntent.getBroadcast(ctx, id, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
            mgr.updateAppWidget(id, views);
            DataLoader.load(ctx, mgr, id);
        }
    }
}

class DataLoader {

    // ponytail: 5 fixed card slots, no addView/removeAllViews — RemoteViews.addView crashes launcher
    private static final int[] CARD_ROOTS = {R.id.card_0, R.id.card_1, R.id.card_2, R.id.card_3, R.id.card_4};
    private static final int[] CARD_BORDERS = {R.id.card_0_border, R.id.card_1_border, R.id.card_2_border, R.id.card_3_border, R.id.card_4_border};
    private static final int[] CARD_SOURCES = {R.id.card_0_source, R.id.card_1_source, R.id.card_2_source, R.id.card_3_source, R.id.card_4_source};
    private static final int[] CARD_SUMMARIES = {R.id.card_0_summary, R.id.card_1_summary, R.id.card_2_summary, R.id.card_3_summary, R.id.card_4_summary};

    static void load(Context ctx, AppWidgetManager mgr, int id) {
        new Thread(() -> {
            Handler main = new Handler(Looper.getMainLooper());
            try {
                java.net.URL url = new java.net.URL("https://jj55-aa.github.io/info-cards/info-cards.json");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000); conn.setReadTimeout(5000);
                java.io.InputStream is = conn.getInputStream();
                byte[] buf = new byte[4096]; StringBuilder sb = new StringBuilder();
                int n; while ((n = is.read(buf)) != -1) sb.append(new String(buf, 0, n));
                is.close(); conn.disconnect();

                org.json.JSONArray arr = new org.json.JSONObject(sb.toString()).getJSONArray("cards");
                RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                views.setTextViewText(R.id.widget_subtitle, arr.length() + " 张 · 点击刷新");

                int max = Math.min(arr.length(), 5);
                for (int i = 0; i < 5; i++) {
                    if (i < max) {
                        org.json.JSONObject c = arr.getJSONObject(i);
                        String p = c.getString("priority");
                        int color = p.equals("high") ? 0xffff4d4d : p.equals("low") ? 0xff4d94ff : 0xfff0a500;
                        views.setInt(CARD_BORDERS[i], "setBackgroundColor", color);
                        views.setTextViewText(CARD_SOURCES[i], c.getString("source") + " " + c.getString("time"));
                        views.setTextViewText(CARD_SUMMARIES[i], c.getString("summary"));
                        views.setInt(CARD_ROOTS[i], "setVisibility", 0); // VISIBLE
                    } else {
                        views.setInt(CARD_ROOTS[i], "setVisibility", 8); // GONE
                    }
                }
                main.post(() -> mgr.updateAppWidget(id, views));
            } catch (Exception ignored) {
                RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                views.setTextViewText(R.id.widget_subtitle, "加载失败 · 点击重试");
                for (int i = 0; i < 5; i++) {
                    views.setInt(CARD_ROOTS[i], "setVisibility", 8);
                }
                main.post(() -> mgr.updateAppWidget(id, views));
            }
        }).start();
    }
}
