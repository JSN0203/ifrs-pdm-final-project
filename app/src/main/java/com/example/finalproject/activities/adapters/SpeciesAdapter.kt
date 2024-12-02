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
import com.example.finalproject.models.Specie

class SpeciesAdapter (
    private val onEdit: (Specie) -> Unit,
    private val onDelete: (Specie) -> Unit
) : ListAdapter<Specie, SpeciesAdapter.SpecieViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Specie>() {
            override fun areItemsTheSame(oldItem: Specie, newItem: Specie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Specie, newItem: Specie): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class SpecieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.item_title)
        private val descriptionText: TextView = itemView.findViewById(R.id.item_description)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)


        fun bind(specie: Specie) {
            nameText.text = specie.name
            descriptionText.text = specie.description ?: "No description"
            editButton.setOnClickListener { onEdit(specie) }
            deleteButton.setOnClickListener { onDelete(specie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return SpecieViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecieViewHolder, position: Int) {
        val specie = getItem(position)
        holder.bind(specie)
    }
}
