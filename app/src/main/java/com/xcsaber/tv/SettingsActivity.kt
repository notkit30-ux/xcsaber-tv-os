package com.xcsaber.tv

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/** Launcher options plus one-press shortcuts to the TV's own system settings. */
class SettingsActivity : Activity() {

    private lateinit var prefs: LauncherPrefs
    private lateinit var list: RecyclerView
    private val adapter = SettingsAdapter()
    private var firstFocusDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = LauncherPrefs(this)
        list = findViewById(R.id.settings_list)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        list.itemAnimator = null

        render()
    }

    private fun render() {
        val hiddenCount = prefs.hidden.size
        val rows = listOf(
            SettingRow("Wi-Fi & network", "Connect to a network") {
                SystemSettings.open(this, Settings.ACTION_WIFI_SETTINGS)
            },
            SettingRow("Bluetooth & remotes", "Pair a remote, keyboard or speaker") {
                SystemSettings.open(this, Settings.ACTION_BLUETOOTH_SETTINGS)
            },
            SettingRow("Display", "Screen and picture options") {
                SystemSettings.open(this, Settings.ACTION_DISPLAY_SETTINGS)
            },
            SettingRow("Sound", "Volume and audio output") {
                SystemSettings.open(this, Settings.ACTION_SOUND_SETTINGS)
            },
            SettingRow("Manage apps", "Uninstall apps and review permissions") {
                SystemSettings.open(this, Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS)
            },
            SettingRow("All system settings", "Open the TV's own settings") {
                SystemSettings.open(this, Settings.ACTION_SETTINGS)
            },
            SettingRow("Choose your home screen", "Make XCSABER TV OS the default for the Home button") {
                SystemSettings.open(this, Settings.ACTION_HOME_SETTINGS)
            },
            SettingRow(
                "Show hidden apps",
                if (hiddenCount == 0) "No apps are hidden" else "$hiddenCount hidden. Press OK to show them again"
            ) {
                prefs.clearHidden()
                Toast.makeText(this, "Hidden apps are visible again.", Toast.LENGTH_SHORT).show()
                render()
            },
            SettingRow("Clear recent apps", "Empties the Recent row on the home screen") {
                prefs.clearRecents()
                Toast.makeText(this, "Recent apps cleared.", Toast.LENGTH_SHORT).show()
            },
            SettingRow("About XCSABER TV OS", "Version and privacy") { showAbout() }
        )
        adapter.submit(rows)

        if (!firstFocusDone) {
            firstFocusDone = true
            list.post { list.requestFocus() }
        }
    }

    private fun showAbout() {
        val version = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            "1.0.0"
        }
        AlertDialog.Builder(this)
            .setTitle("XCSABER TV OS $version")
            .setMessage(
                "An ad-free, private home screen for your Android TV.\n\n" +
                    "• No ads and no tracking\n" +
                    "• Requests no permissions, including internet\n" +
                    "• Favorites, hidden apps and recents stay on this TV only"
            )
            .setPositiveButton("OK", null)
            .show()
    }
}

data class SettingRow(val title: String, val subtitle: String, val action: () -> Unit)

class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.Holder>() {

    private var rows: List<SettingRow> = emptyList()

    fun submit(newRows: List<SettingRow>) {
        rows = newRows
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.setting_title)
        val subtitle: TextView = view.findViewById(R.id.setting_subtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val row = rows[position]
        holder.title.text = row.title
        holder.subtitle.text = row.subtitle
        holder.itemView.setOnClickListener { row.action() }
    }

    override fun getItemCount(): Int = rows.size
}
