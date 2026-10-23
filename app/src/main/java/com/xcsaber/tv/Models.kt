package com.xcsaber.tv

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable

/** An installed app that can be launched from the TV home screen. */
data class AppEntry(
    val label: String,
    val component: ComponentName,
    val icon: Drawable,
    /** True when [icon] is a 16:9 TV banner, false when it is a square app icon. */
    val isBanner: Boolean
) {
    val key: String get() = component.flattenToString()
    val packageName: String get() = component.packageName
}

/** One focusable card on screen (an app or a shortcut). */
data class Tile(
    val id: String,
    val label: String,
    val icon: Drawable?,
    val isBanner: Boolean,
    val onClick: () -> Unit,
    val onLongClick: (() -> Unit)? = null
)

/** Converts density-independent pixels to pixels. */
fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density + 0.5f).toInt()
