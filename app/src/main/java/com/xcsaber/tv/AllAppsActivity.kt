package com.xcsaber.tv

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

/** Every app on the TV in one grid. Hold OK (or press Menu) on an app to pin, hide or inspect it. */
class AllAppsActivity : Activity() {

    private lateinit var prefs: LauncherPrefs
    private lateinit var grid: RecyclerView
    private lateinit var titleView: TextView

    private val adapter = TileAdapter(fillWidth = true)
    private val executor = Executors.newSingleThreadExecutor()
    private var apps: List<AppEntry> = emptyList()
    private var firstFocusDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_apps)

        prefs = LauncherPrefs(this)
        grid = findViewById(R.id.apps_grid)
        titleView = findViewById(R.id.apps_title)

        // Each card is about 220dp wide including spacing; fit as many columns as the screen allows.
        val widthDp = resources.configuration.screenWidthDp
        val columns = ((widthDp - 96) / 220).coerceIn(3, 8)
        grid.layoutManager = GridLayoutManager(this, columns)
        grid.adapter = adapter
        grid.itemAnimator = null

        executor.execute {
            val loaded = AppRepository.getApps(applicationContext, false)
            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    apps = loaded
                    render()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (apps.isNotEmpty()) render()
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    private fun render() {
        val hidden = prefs.hidden
        val visible = apps.filter { it.key !in hidden }
        titleView.text = "All apps  ·  ${visible.size}"
        adapter.submit(visible.map { appTile(it) })

        if (!firstFocusDone && visible.isNotEmpty()) {
            firstFocusDone = true
            grid.post { grid.requestFocus() }
        }
    }

    private fun appTile(app: AppEntry): Tile = Tile(
        id = app.key,
        label = app.label,
        icon = app.icon,
        isBanner = app.isBanner,
        onClick = { AppActions.launch(this, prefs, app) },
        onLongClick = { AppActions.showOptions(this, prefs, app) { render() } }
    )
}
