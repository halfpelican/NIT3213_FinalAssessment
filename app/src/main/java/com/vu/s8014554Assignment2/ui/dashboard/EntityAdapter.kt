package com.vu.s8014554Assignment2.ui.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vu.s8014554Assignment2.R
import com.vu.s8014554Assignment2.data.model.Entity

/**
 * Binds the dashboard's artwork list to the RecyclerView.
 *
 * Holds its own copy of the data, supplied after the network call through
 * [updateData]. Navigation is not handled here: the fragment passes in
 * [onItemClick], so the adapter stays responsible only for display.
 *
 * @param onItemClick Invoked with the tapped entity so the caller can navigate.
 */
class EntityAdapter(
    private val onItemClick: (Entity) -> Unit
) : RecyclerView.Adapter<EntityAdapter.EntityViewHolder>() {

    /** Current list. A var because [updateData] replaces it when data arrives. */
    private var entities: List<Entity> = emptyList()

    /**
     * Caches the row's views so they are looked up once per row rather than on
     * every bind — the reason RecyclerView uses the ViewHolder pattern.
     */
    inner class EntityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val artworkTitleText: TextView = view.findViewById(R.id.artworkTitleText)
        private val artistText: TextView = view.findViewById(R.id.artistText)
        private val mediumYearText: TextView = view.findViewById(R.id.mediumYearText)

        /**
         * Shows one artwork's summary. The description is deliberately left out;
         * it belongs on the details screen.
         */
        fun bind(entity: Entity) {
            artworkTitleText.text = entity.artworkTitle
            artistText.text = entity.artist
            // getString formats the Int year; assigning an Int to text would be
            // read as a string resource ID and crash.
            mediumYearText.text = itemView.context.getString(
                R.string.entity_medium_year, entity.medium, entity.year
            )
            itemView.setOnClickListener { onItemClick(entity) }
        }
    }

    /** Inflates one row layout and wraps it in a ViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entity, parent, false)
        return EntityViewHolder(view)
    }

    /** Binds the entity at [position] to a recycled ViewHolder. */
    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    override fun getItemCount() = entities.size

    /**
     * Replaces the list and refreshes the RecyclerView.
     *
     * notifyDataSetChanged redraws everything, which is fine here because the
     * data is fetched once. A list that changed piecemeal would use DiffUtil or
     * ListAdapter so only the affected rows are rebound.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newEntities: List<Entity>) {
        entities = newEntities
        notifyDataSetChanged()
    }
}