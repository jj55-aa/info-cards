package io.github.jj55_aa.info_cards;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;

public class InfoCardsWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) {
            showLoading(context, manager, id);
            fetchCards(context, manager, id);
        }
    }

    private void showLoading(Context ctx, AppWidgetManager mgr, int id) {
        RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
        Intent intent = new Intent(ctx, InfoCardsWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{id});
        views.setOnClickPendingIntent(R.id.widget_root,
            PendingIntent.getBroadcast(ctx, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        mgr.updateAppWidget(id, views);
    }

    private void fetchCards(Context ctx, AppWidgetManager mgr, int id) {
        new Thread(() -> {
            try {
                URL url = new URL("https://jj55-aa.github.io/info-cards/info-cards.json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                StringBuilder sb = new StringBuilder();
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                conn.disconnect();

                JSONObject json = new JSONObject(sb.toString());
                org.json.JSONArray cards = json.getJSONArray("cards");

                RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                views.setTextViewText(R.id.widget_subtitle, cards.length() + " 张卡片 · 点击刷新");
                views.removeAllViews(R.id.card_container);

                for (int i = 0; i < Math.min(cards.length(), 5); i++) {
                    JSONObject c = cards.getJSONObject(i);
                    String src = c.getString("source") + " " + c.getString("time");
                    String sum = c.getString("summary");
                    String p = c.getString("priority");
                    int color = p.equals("high") ? 0xffff4d4d :
                                p.equals("low") ? 0xff4d94ff : 0xfff0a500;

                    RemoteViews row = new RemoteViews(ctx.getPackageName(), R.layout.info_card_row);
                    row.setTextViewText(R.id.card_source, src);
                    row.setTextViewText(R.id.card_summary, sum);
                    row.setInt(R.id.card_border, "setBackgroundColor", color);
                    views.addView(R.id.card_container, row);
                }

                new Handler(Looper.getMainLooper()).post(() -> mgr.updateAppWidget(id, views));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                    views.setTextViewText(R.id.widget_subtitle, "加载失败 · 点击重试");
                    mgr.updateAppWidget(id, views);
                });
            }
        }).start();
    }
}
