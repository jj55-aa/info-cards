package io.github.jj55_aa.info_cards

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// ---- Data ----

data class Card(
    val id: Int,
    val source: String,
    val time: String,
    val summary: String,
    val priority: String
)

private const val DATA_URL = "https://jj55-aa.github.io/info-cards/info-cards.json"

private suspend fun fetchCards(): List<Card> = withContext(Dispatchers.IO) {
    try {
        val conn = URL(DATA_URL).openConnection() as HttpURLConnection
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000
        conn.requestMethod = "GET"
        conn.setRequestProperty("Cache-Control", "no-cache")
        val text = conn.inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()
        val arr: JSONArray = JSONObject(text).getJSONArray("cards")
        (0 until arr.length()).map { i ->
            val c = arr.getJSONObject(i)
            Card(
                id = c.getInt("id"),
                source = c.optString("source", ""),
                time = c.optString("time", ""),
                summary = c.optString("summary", ""),
                priority = c.optString("priority", "mid")
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

private fun borderColor(p: String): ColorProvider = when (p) {
    "high" -> ColorProvider(0xffff4d4d.toInt())
    "mid"  -> ColorProvider(0xfff0a500.toInt())
    "low"  -> ColorProvider(0xff4d94ff.toInt())
    else   -> ColorProvider(0xff444444.toInt())
}

// ---- Action (tap to refresh) ----

class RefreshAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: Bundle) {
        InfoCardsWidgetReceiver.updateWidget(context)
    }
}

// ---- Glance Widget ----

class InfoCardsGlanceWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Expandable

    @Composable
    override fun Content() {
        val cards = InfoCardsWidgetReceiver.cachedCards
        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(ColorProvider(0xff0f0f0f.toInt()))
                    .padding(12.dp)
                    .clickable(onClick = actionRunCallback<RefreshAction>())
            ) {
                Text("📋 信息卡", style = TextStyle(fontSize = 15.sp, color = ColorProvider(0xffffffff.toInt()), fontWeight = FontWeight.Bold))
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text("点击刷新", style = TextStyle(fontSize = 10.sp, color = ColorProvider(0xff888888.toInt())))
                Spacer(modifier = GlanceModifier.height(10.dp))

                if (cards.isEmpty()) {
                    Text("暂无信息\n点击刷新加载", style = TextStyle(fontSize = 13.sp, color = ColorProvider(0xff555555.toInt())))
                } else {
                    cards.forEach { c ->
                        CardRow(c)
                        Spacer(modifier = GlanceModifier.height(8.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun CardRow(c: Card) {
        Row(
            modifier = GlanceModifier.fillMaxWidth()
                .background(ColorProvider(0xff1a1a1a.toInt()))
                .cornerRadius(8.dp)
        ) {
            Spacer(modifier = GlanceModifier.width(3.dp).height(60.dp)
                .background(borderColor(c.priority)).cornerRadius(2.dp))
            Column(modifier = GlanceModifier.padding(start = 10.dp, top = 8.dp, bottom = 8.dp, end = 10.dp).defaultWeight()) {
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    Text(c.source.ifEmpty { "信息" }, style = TextStyle(fontSize = 11.sp, color = ColorProvider(0xff888888.toInt())), modifier = GlanceModifier.defaultWeight())
                    Text(c.time, style = TextStyle(fontSize = 11.sp, color = ColorProvider(0xff888888.toInt())))
                }
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(c.summary, style = TextStyle(fontSize = 14.sp, color = ColorProvider(0xffdddddd.toInt())))
            }
        }
    }
}

// ---- Receiver ----

class InfoCardsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = InfoCardsGlanceWidget()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateWidget(context)
    }

    companion object {
        @Volatile
        var cachedCards: List<Card> = emptyList()
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        fun updateWidget(context: Context) {
            scope.launch {
                cachedCards = fetchCards()
                val manager = GlanceAppWidgetManager(context)
                val ids = manager.getGlanceIds(InfoCardsGlanceWidget::class.java)
                ids.forEach { InfoCardsGlanceWidget().update(context, it) }
            }
        }
    }
}
