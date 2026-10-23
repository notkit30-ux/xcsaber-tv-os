package com.xcsaber.tv

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

/** Opens system settings screens. Some TV boxes remove screens, so it falls back safely. */
object SystemSettings {

    fun open(context: Context, action: String) {
        if (tryStart(context, action)) return
        if (tryStart(context, Settings.ACTION_SETTINGS)) return
        Toast.makeText(context, "This screen isn't available on your TV.", Toast.LENGTH_LONG).show()
    }

    private fun tryStart(context: Context, action: String): Boolean {
        return try {
            context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (e: Exception) {
            false
        }
    }
}
