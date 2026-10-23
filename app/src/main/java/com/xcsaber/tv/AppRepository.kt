package com.xcsaber.tv

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 * Finds the apps installed on the TV. TV apps (leanback) come first and show their banner;
 * phone-style apps (such as a sideloaded browser) are included too so nothing is missing.
 * Everything stays on the device. Nothing is sent anywhere.
 */
object AppRepository {

    @Volatile
    private var cache: List<AppEntry>? = null

    /** Call from a background thread: loading icons for every app can take a moment. */
    @Synchronized
    fun getApps(context: Context, force: Boolean): List<AppEntry> {
        val cached = cache
        if (!force && cached != null) return cached
        val fresh = query(context)
        cache = fresh
        return fresh
    }

    private fun query(context: Context): List<AppEntry> {
        val pm = context.packageManager
        val ownPackage = context.packageName
        val seenPackages = HashSet<String>()
        val result = ArrayList<AppEntry>()

        val categories = listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)
        for (category in categories) {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(category)
            val resolved = try {
                pm.queryIntentActivities(intent, 0)
            } catch (e: Exception) {
                emptyList()
            }
            for (info in resolved) {
                val activity = info.activityInfo ?: continue
                val pkg = activity.packageName
                if (pkg == ownPackage) continue
                if (!seenPackages.add(pkg)) continue

                val banner = if (category == Intent.CATEGORY_LEANBACK_LAUNCHER) {
                    loadBanner(activity, pm)
                } else {
                    null
                }
                val icon = banner ?: info.loadIcon(pm)
                result.add(
                    AppEntry(
                        label = info.loadLabel(pm).toString(),
                        component = ComponentName(pkg, activity.name),
                        icon = icon,
                        isBanner = banner != null
                    )
                )
            }
        }
        return result.sortedBy { it.label.lowercase() }
    }

    private fun loadBanner(info: ActivityInfo, pm: PackageManager): Drawable? {
        return try {
            info.loadBanner(pm)
        } catch (e: Exception) {
            null
        }
    }
}
