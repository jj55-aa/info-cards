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
            loadData(ctx, mgr, id);
        }
    }

    static void loadData(Context ctx, AppWidgetManager mgr, int id) {
        new Thread(() -> {
            try {
                java.net.URL u = new java.net.URL("https://jj55-aa.github.io/info-cards/info-cards.json");
                java.net.HttpURLConnection c = (java.net.HttpURLConnection) u.openConnection();
                c.setConnectTimeout(5000); c.setReadTimeout(5000);
                byte[] b = new byte[4096]; StringBuilder s = new StringBuilder();
                java.io.InputStream in = c.getInputStream(); int n;
                while ((n = in.read(b)) != -1) s.append(new String(b,0,n));
                in.close(); c.disconnect();
                int count = new org.json.JSONObject(s.toString()).getJSONArray("cards").length();
                Handler h = new Handler(Looper.getMainLooper());
                h.post(() -> {
                    RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                    v.setTextViewText(R.id.widget_subtitle, count + " 张卡片 · 点击刷新");
                    mgr.updateAppWidget(id, v);
                });
            } catch (Exception e) {
                Handler h = new Handler(Looper.getMainLooper());
                h.post(() -> {
                    RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                    v.setTextViewText(R.id.widget_subtitle, "加载失败 · 点击重试");
                    mgr.updateAppWidget(id, v);
                });
            }
        }).start();
    }
}
