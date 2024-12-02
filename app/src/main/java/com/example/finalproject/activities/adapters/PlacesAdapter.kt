package com.example.finalproject.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.models.Place

class PlacesAdapter(
    private val onEdit: (Place) -> Unit,
    private val onDelete: (Place) -> Unit
) : ListAdapter<Place, PlacesAdapter.PlaceViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Place>() {
            override fun areItemsTheSame(oldItem: Place, newItem: Place) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Place, newItem: Place) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.item_title)
        private val description: TextView = itemView.findViewById(R.id.item_description)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(place: Place) {
            name.text = place.name
            description.text = place.description

            editButton.setOnClickListener { onEdit(place) }
            deleteButton.setOnClickListener { onDelete(place) }
        }
    }
}
