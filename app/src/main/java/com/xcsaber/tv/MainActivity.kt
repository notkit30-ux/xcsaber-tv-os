package com.xcsaber.tv

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

/**
 * The XCSABER TV OS home screen: clock, quick access, favorites and recent apps.
 * It is registered as a HOME app, so the remote's Home button opens it once you choose it as default.
 */
class MainActivity : Activity() {

    private lateinit var prefs: LauncherPrefs
    private lateinit var scroll: ScrollView
    private lateinit var rvQuick: RecyclerView
    private lateinit var rvFavorites: RecyclerView
    private lateinit var rvRecent: RecyclerView
    private lateinit var hintFavorites: TextView
    private lateinit var hintRecent: TextView

    private val quickAdapter = TileAdapter()
    private val favoritesAdapter = TileAdapter()
    private val recentAdapter = TileAdapter()

    private val executor = Executors.newSingleThreadExecutor()
    private var apps: List<AppEntry> = emptyList()
    private var firstFocusDone = false

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadApps(force = true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = LauncherPrefs(this)
        scroll = findViewById(R.id.home_scroll)
        rvQuick = findViewById(R.id.rv_quick)
        rvFavorites = findViewById(R.id.rv_favorites)
        rvRecent = findViewById(R.id.rv_recent)
        hintFavorites = findViewById(R.id.hint_favorites)
        hintRecent = findViewById(R.id.hint_recent)

        setupRow(rvQuick, quickAdapter)
        setupRow(rvFavorites, favoritesAdapter)
        setupRow(rvRecent, recentAdapter)

        quickAdapter.submit(buildQuickTiles())
        loadApps(force = false)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(packageReceiver, filter)
        refreshRows()
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(packageReceiver)
        } catch (e: IllegalArgumentException) {
            // Not registered; nothing to do.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    /** Pressing Home while already on the home screen jumps back to the top. */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        scroll.smoothScrollTo(0, 0)
        rvQuick.requestFocus()
    }

    /** A launcher has nowhere to go "back" to, so Back just returns to the top row. */
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        scroll.smoothScrollTo(0, 0)
        rvQuick.requestFocus()
    }

    private fun setupRow(rv: RecyclerView, adapter: TileAdapter) {
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = adapter
        rv.itemAnimator = null
    }

    private fun buildQuickTiles(): List<Tile> = listOf(
        Tile("browser", "Browser", getDrawable(R.drawable.ic_tile_browser), false, { openBrowser() }),
        Tile(
            "apps", "All apps", getDrawable(R.drawable.ic_tile_apps), false,
            { startActivity(Intent(this, AllAppsActivity::class.java)) }
        ),
        Tile(
            "wifi", "Wi-Fi", getDrawable(R.drawable.ic_tile_wifi), false,
            { SystemSettings.open(this, Settings.ACTION_WIFI_SETTINGS) }
        ),
        Tile(
            "settings", "Settings", getDrawable(R.drawable.ic_tile_settings), false,
            { startActivity(Intent(this, SettingsActivity::class.java)) }
        )
    )

    private fun loadApps(force: Boolean) {
        executor.execute {
            val loaded = AppRepository.getApps(applicationContext, force)
            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    apps = loaded
                    refreshRows()
                }
            }
        }
    }

    private fun refreshRows() {
        val hidden = prefs.hidden
        val visibleByKey = apps.filter { it.key !in hidden }.associateBy { it.key }

        val favorites = prefs.favorites.mapNotNull { visibleByKey[it] }.map { appTile(it) }
        val recents = prefs.recents.mapNotNull { visibleByKey[it] }.map { appTile(it) }

        favoritesAdapter.submit(favorites)
        recentAdapter.submit(recents)

        rvFavorites.visibility = if (favorites.isEmpty()) View.GONE else View.VISIBLE
        hintFavorites.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
        rvRecent.visibility = if (recents.isEmpty()) View.GONE else View.VISIBLE
        hintRecent.visibility = if (recents.isEmpty()) View.VISIBLE else View.GONE

        if (!firstFocusDone) {
            firstFocusDone = true
            rvQuick.post { rvQuick.requestFocus() }
        }
    }

    private fun appTile(app: AppEntry): Tile = Tile(
        id = app.key,
        label = app.label,
        icon = app.icon,
        isBanner = app.isBanner,
        onClick = { AppActions.launch(this, prefs, app) },
        onLongClick = { AppActions.showOptions(this, prefs, app) { refreshRows() } }
    )

    /** Opens the XCSABER browser if it is installed; otherwise any browser on the TV. */
    private fun openBrowser() {
        val xcsaber = apps.firstOrNull {
            it.label.contains("xcsaber", ignoreCase = true) || it.packageName.startsWith("com.xcsaber")
        }
        if (xcsaber != null) {
            AppActions.launch(this, prefs, xcsaber)
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://duckduckgo.com"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "No browser found. Install the XCSABER browser app and it will open from here.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
