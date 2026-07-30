package io.github.jj55_aa.info_cards

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0f0f0f"))
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        val title = TextView(this).apply {
            text = "📋 信息卡"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        layout.addView(title)

        val desc = TextView(this).apply {
            text = "\n这是一个桌面小组件\n\n长按桌面空白处\n添加「信息卡」小组件\n即可在桌面查看工作信息\n"
            textSize = 15f
            setTextColor(Color.parseColor("#888888"))
            gravity = Gravity.CENTER
        }
        layout.addView(desc)

        val btn = Button(this).apply {
            text = "我知道了"
            setBackgroundColor(Color.parseColor("#4d94ff"))
            setTextColor(Color.WHITE)
            setOnClickListener { finish() }
        }
        layout.addView(btn)

        setContentView(layout)
    }
}
