
package com.xcsaber.tv

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val bg = Color.rgb(8, 11, 18)
    private val purple = Color.rgb(124, 92, 252)
    private val white = Color.WHITE
    private val muted = Color.rgb(165, 173, 190)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(48), dp(28), dp(48), dp(28))
            setBackgroundColor(bg)
            gravity = Gravity.CENTER_VERTICAL
        }

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val logo = TextView(this).apply {
            text = "X"
            textSize = 34f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(purple)
        }

        val brand = TextView(this).apply {
            text = "CSABER"
            textSize = 25f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
        }

        top.addView(logo)
        top.addView(brand)
        root.addView(top)

        val subtitle = TextView(this).apply {
            text = "YOUR TV. YOUR WORLD."
            textSize = 12f
            letterSpacing = 0.18f
            setTextColor(muted)
            setPadding(0, dp(4), 0, dp(26))
        }
        root.addView(subtitle)

        val heading = TextView(this).apply {
            text = "Welcome to XCSABER"
            textSize = 30f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
            setPadding(0, 0, 0, dp(8))
        }
        root.addView(heading)

        val description = TextView(this).apply {
            text = "A cleaner, simpler home for your Android TV."
            textSize = 16f
            setTextColor(muted)
            setPadding(0, 0, 0, dp(28))
        }
        root.addView(description)

        val cards = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        cards.addView(makeCard("Search the Web", "Open web search") {
            openSearch()
        })

        cards.addView(makeCard("XCSABER Browser", "Private browsing") {
            openXcsaberBrowser()
        })

        cards.addView(makeCard("Settings", "Android TV settings") {
            openSettings()
        })

        root.addView(cards)

        val footer = TextView(this).apply {
            text = "XCSABER TV OS  •  Designed for Android TV"
            textSize = 12f
            setTextColor(muted)
            setPadding(0, dp(32), 0, 0)
        }
        root.addView(footer)

        setContentView(root)
    }

    private fun makeCard(
        title: String,
        detail: String,
        action: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = "$title\n\n$detail"
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            setBackgroundColor(Color.rgb(25, 29, 43))
            isFocusable = true
            isFocusableInTouchMode = true
            setOnFocusChangeListener { view, focused ->
                view.setBackgroundColor(
                    if (focused) purple else Color.rgb(25, 29, 43)
                )
            }
            setOnClickListener { action() }

            val params = LinearLayout.LayoutParams(
                0,
                dp(130),
                1f
            )
            params.setMargins(dp(5), 0, dp(5), 0)
            layoutParams = params
        }
    }

    private fun openSearch() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/search?q=")
        )
        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(
                this,
                "No browser is available yet.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openXcsaberBrowser() {
        val intent = packageManager.getLaunchIntentForPackage(
            "com.xcsaber.app"
        )

        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Install XCSABER Browser first.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openSettings() {
        try {
            startActivity(
                Intent(android.provider.Settings.ACTION_SETTINGS)
            )
        } catch (_: Exception) {
            Toast.makeText(
                this,
                "Settings could not be opened.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
