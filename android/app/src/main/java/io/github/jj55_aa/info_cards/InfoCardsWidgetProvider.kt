package io.github.jj55_aa.info_cards

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class InfoCardsWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.info_widget)
            appWidgetManager.updateAppWidget(id, views)
            fetchAndUpdate(context, appWidgetManager, id)
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
                    val containerId = R.id.card_container
                    views.removeAllViews(containerId)
                    for (i in 0 until minOf(cards.length(), 5)) {
                        val c = cards.getJSONObject(i)
                        val p = c.optString("priority", "mid")
                        val borderColor = when(p) { "high" -> 0xffff4d4d.toInt() "low" -> 0xff4d94ff.toInt() else -> 0xfff0a500.toInt() }
                        val source = "${c.optString("source")} ${c.optString("time")}"
                        val summary = c.optString("summary")
                        val rowView = RemoteViews(context.packageName, R.layout.info_card_row)
                        rowView.setTextViewText(R.id.card_source, source)
                        rowView.setTextViewText(R.id.card_summary, summary)
                        rowView.setInt(R.id.card_border, "setBackgroundColor", borderColor)
                        views.addView(containerId, rowView)
                    }
                    val intent = Intent(context, InfoCardsWidgetProvider::class.java).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
                    }
                    val pi = PendingIntent.getBroadcast(context, widgetId, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    views.setOnClickPendingIntent(R.id.widget_root, pi)
                    handler.post { manager.updateAppWidget(widgetId, views) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
