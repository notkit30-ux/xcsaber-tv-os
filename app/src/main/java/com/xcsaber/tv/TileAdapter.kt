package com.xcsaber.tv

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Shows [Tile]s as focusable cards. Used for the home rows (fillWidth = false)
 * and for the All apps grid (fillWidth = true).
 */
class TileAdapter(private val fillWidth: Boolean = false) : RecyclerView.Adapter<TileAdapter.Holder>() {

    private var items: List<Tile> = emptyList()

    fun submit(newItems: List<Tile>) {
        items = newItems
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val card: View = view.findViewById(R.id.tile_card)
        val icon: ImageView = view.findViewById(R.id.tile_icon)
        val label: TextView = view.findViewById(R.id.tile_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tile, parent, false)
        if (fillWidth) {
            val lp = view.layoutParams
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            view.layoutParams = lp
        }
        val holder = Holder(view)
        holder.card.setOnFocusChangeListener { v, hasFocus ->
            val scale = if (hasFocus) 1.08f else 1f
            v.animate().scaleX(scale).scaleY(scale).setDuration(120).start()
            v.translationZ = if (hasFocus) 12f else 0f
        }
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val tile = items[position]
        val context = holder.itemView.context

        holder.label.text = tile.label

        // Each view gets its own copy of the drawable because the same app can appear in several rows.
        val drawable = tile.icon?.constantState?.newDrawable(context.resources) ?: tile.icon
        holder.icon.setImageDrawable(drawable)
        if (tile.isBanner) {
            holder.icon.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.icon.setPadding(0, 0, 0, 0)
        } else {
            holder.icon.scaleType = ImageView.ScaleType.FIT_CENTER
            val pad = context.dp(14)
            holder.icon.setPadding(pad, pad, pad, pad)
        }

        holder.card.setOnClickListener { tile.onClick() }

        val longClick = tile.onLongClick
        if (longClick != null) {
            holder.card.setOnLongClickListener {
                longClick()
                true
            }
        } else {
            holder.card.setOnLongClickListener(null)
        }
        holder.card.setOnKeyListener { _, keyCode, event ->
            if (longClick != null && keyCode == KeyEvent.KEYCODE_MENU && event.action == KeyEvent.ACTION_UP) {
                longClick()
                true
            } else {
                false
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
