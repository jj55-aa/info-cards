package io.github.jj55_aa.info_cards

import android.app.Activity
import android.os.Bundle

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Widget-only app — add widget via home screen
        finish()
    }
}
