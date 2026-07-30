package io.github.jj55_aa.info_cards

import android.app.Activity
import android.os.Bundle

// ponytail: shell activity — widget lives on home screen, app has no UI
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}
