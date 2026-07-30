package io.github.jj55_aa.info_cards

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class InfoCardsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.info_widget)
            // Set up refresh tap
            val intent = Intent(context, InfoCardsWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(id))
            }
            views.setOnClickPendingIntent(R.id.widget_root,
                PendingIntent.getBroadcast(context, id, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            manager.updateAppWidget(id, views)
            // Fetch data in background
            fetchAndUpdate(context, manager, id)
        }
    }

    companion object {
        private val executor = Executors.newSingleThreadExecutor()
        private val handler = Handler(Looper.getMainLooper())
        private const val DATA_URL = "https://jj55-aa.github.io/info-cards/info-cards.json"

        fun fetchAndUpdate(context: Context, manager: AppWidgetManager, widgetId: Int) {
            executor.execute {
                try {
                    val text = URL(DATA_URL).openConnection().run {
                        (this as HttpURLConnection).connectTimeout = 10000
                        readTimeout = 10000
                        setRequestProperty("Cache-Control", "no-cache")
                        inputStream.bufferedReader().readText()
                    }
                    val cards = JSONObject(text).getJSONArray("cards")
                    val views = RemoteViews(context.packageName, R.layout.info_widget)
                    val container = R.id.card_container
                    views.removeAllViews(container)

                    for (i in 0 until minOf(cards.length(), 5)) {
                        val c = cards.getJSONObject(i)
                        val p = c.optString("priority", "mid")
                        val row = RemoteViews(context.packageName, R.layout.info_card_row)
                        row.setTextViewText(R.id.card_source, "${c.optString("source")} ${c.optString("time")}")
                        row.setTextViewText(R.id.card_summary, c.optString("summary"))
                        row.setInt(R.id.card_border, "setBackgroundColor",
                            when(p) { "high" -> 0xffff4d4d.toInt() "low" -> 0xff4d94ff.toInt() else -> 0xfff0a500.toInt() })
                        views.addView(container, row)
                    }

                    views.setTextViewText(R.id.widget_subtitle, cards.length().toString() + " 张卡片 · 点击刷新")
                    handler.post { manager.updateAppWidget(widgetId, views) }
                } catch (e: Exception) {
                    val views = RemoteViews(context.packageName, R.layout.info_widget)
                    views.setTextViewText(R.id.widget_subtitle, "加载失败 · 点击重试")
                    handler.post { manager.updateAppWidget(widgetId, views) }
                }
            }
        }
    }
}
