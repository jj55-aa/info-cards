package io.github.jj55_aa.info_cards;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
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
    static void load(Context ctx, AppWidgetManager mgr, int id) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("https://jj55-aa.github.io/info-cards/info-cards.json");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000); conn.setReadTimeout(5000);
                java.io.InputStream is = conn.getInputStream();
                byte[] buf = new byte[4096]; StringBuilder sb = new StringBuilder();
                int n; while ((n = is.read(buf)) != -1) sb.append(new String(buf,0,n));
                is.close(); conn.disconnect();

                org.json.JSONArray arr = new org.json.JSONObject(sb.toString()).getJSONArray("cards");
                RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                views.setTextViewText(R.id.widget_subtitle, arr.length() + " 张 · 点击刷新");
                views.removeAllViews(R.id.card_container);
                int max = Math.min(arr.length(), 5);
                for (int i = 0; i < max; i++) {
                    org.json.JSONObject c = arr.getJSONObject(i);
                    String p = c.getString("priority");
                    int color = p.equals("high") ? 0xffff4d4d : p.equals("low") ? 0xff4d94ff : 0xfff0a500;
                    RemoteViews row = new RemoteViews(ctx.getPackageName(), R.layout.info_card_row);
                    row.setTextViewText(R.id.card_source, c.getString("source") + " " + c.getString("time"));
                    row.setTextViewText(R.id.card_summary, c.getString("summary"));
                    row.setInt(R.id.card_border, "setBackgroundColor", color);
                    views.addView(R.id.card_container, row);
                }
                mgr.partiallyUpdateAppWidget(id, views);
            } catch (Exception ignored) {
                RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                views.setTextViewText(R.id.widget_subtitle, "加载失败 · 点击重试");
                mgr.partiallyUpdateAppWidget(id, views);
            }
        }).start();
    }
}
