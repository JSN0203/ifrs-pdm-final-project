package com.example.finalproject.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.ItemListBinding
import com.example.finalproject.databinding.ItemPlantDetailBinding
import com.example.finalproject.models.Plant

class PlantAdapter(
    private val onClick: (Plant) -> Unit
) : ListAdapter<Plant, PlantAdapter.PlantViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Plant>() {
            override fun areItemsTheSame(oldItem: Plant, newItem: Plant) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Plant, newItem: Plant) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlantDetailBinding.inflate(inflater, parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = getItem(position)
        holder.bind(plant)
    }

    inner class PlantViewHolder(private val binding: ItemPlantDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            binding.itemTitle.text = plant.nickname
            binding.root.setOnClickListener { onClick(plant) }
        }
    }
}
