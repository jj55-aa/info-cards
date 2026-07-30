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
    // Data URL: ghfast.top proxies raw.githubusercontent.com
    private static final String URL = "https://ghfast.top/https://raw.githubusercontent.com/jj55-aa/info-cards/master/info-cards.json";

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
            v.setTextViewText(R.id.widget_subtitle, "点击刷新");
            Intent i = new Intent(ctx, InfoCardsWidgetProvider.class);
            i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{id});
            v.setOnClickPendingIntent(R.id.widget_root,
                PendingIntent.getBroadcast(ctx, id, i,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
            mgr.updateAppWidget(id, v);
            fetch(ctx, mgr, id);
        }
    }

    void fetch(Context ctx, AppWidgetManager mgr, int id) {
        new Thread(() -> {
            try {
                java.net.HttpURLConnection c = (java.net.HttpURLConnection) new java.net.URL(URL).openConnection();
                c.setConnectTimeout(5000); c.setReadTimeout(5000);
                byte[] b = new byte[4096]; StringBuilder s = new StringBuilder();
                java.io.InputStream in = c.getInputStream(); int n;
                while ((n = in.read(b)) != -1) s.append(new String(b,0,n));
                in.close(); c.disconnect();
                int count = new org.json.JSONObject(s.toString()).getJSONArray("cards").length();
                String text = count > 0 ? count + " 张卡片" : "暂无信息";
                new Handler(Looper.getMainLooper()).post(() -> {
                    RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.info_widget);
                    v.setTextViewText(R.id.widget_subtitle, text);
                    mgr.updateAppWidget(id, v);
                });
            } catch (Exception e) { /* silently ignore, keep showing old text */ }
        }).start();
    }
}
