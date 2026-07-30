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
            RemoteViews v = makeViews(ctx, id, "加载中...");
            mgr.updateAppWidget(id, v);
            loadData(ctx, mgr, id);
        }
    }

    static RemoteViews makeViews(Context ctx, int id, String subtitle) {
        RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
        v.setTextViewText(R.id.widget_subtitle, subtitle);
        Intent intent = new Intent(ctx, InfoCardsWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{id});
        v.setOnClickPendingIntent(R.id.widget_root,
            PendingIntent.getBroadcast(ctx, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        return v;
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
                String text = count > 0 ? count + " 张卡片 " + java.time.LocalTime.now().toString().substring(0,5) : "暂无信息";
                new Handler(Looper.getMainLooper()).post(() ->
                    mgr.updateAppWidget(id, makeViews(ctx, id, text)));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                    mgr.updateAppWidget(id, makeViews(ctx, id, "失败: " + e.getMessage().substring(0, Math.min(20, e.getMessage().length())))));
            }
        }).start();
    }
}
