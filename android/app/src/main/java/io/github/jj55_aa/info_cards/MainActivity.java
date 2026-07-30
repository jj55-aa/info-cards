package io.github.jj55_aa.info_cards;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#0f0f0f"));
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("📋 信息卡");
        title.setTextSize(22);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView desc = new TextView(this);
        desc.setText("\n这是一个桌面小组件\n\n长按桌面空白处\n添加「信息卡」小组件\n即可在桌面查看工作信息\n");
        desc.setTextSize(15);
        desc.setTextColor(Color.parseColor("#888888"));
        desc.setGravity(Gravity.CENTER);
        layout.addView(desc);

        Button btn = new Button(this);
        btn.setText("我知道了");
        btn.setBackgroundColor(Color.parseColor("#4d94ff"));
        btn.setTextColor(Color.WHITE);
        btn.setOnClickListener(v -> finish());
        layout.addView(btn);

        setContentView(layout);
    }
}
