package com.xcsaber.tv

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

/** Things you can do with an app tile: open it, or pin/hide/inspect it. */
object AppActions {

    fun launch(activity: Activity, prefs: LauncherPrefs, app: AppEntry): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN)
                .setComponent(app.component)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            activity.startActivity(intent)
            prefs.addRecent(app.key)
            true
        } catch (e: Exception) {
            Toast.makeText(activity, "Couldn't open ${app.label}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun showOptions(activity: Activity, prefs: LauncherPrefs, app: AppEntry, onChanged: () -> Unit) {
        val isFavorite = prefs.isFavorite(app.key)
        val options = arrayOf(
            if (isFavorite) "Remove from favorites" else "Pin to favorites",
            "Hide from launcher",
            "App info (uninstall, permissions)"
        )
        AlertDialog.Builder(activity)
            .setTitle(app.label)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        prefs.toggleFavorite(app.key)
                        onChanged()
                    }
                    1 -> {
                        prefs.hide(app.key)
                        onChanged()
                    }
                    2 -> openAppInfo(activity, app.packageName)
                    else -> Unit
                }
            }
            .show()
    }

    private fun openAppInfo(activity: Activity, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(activity, "App info isn't available on your TV.", Toast.LENGTH_SHORT).show()
        }
    }
}
