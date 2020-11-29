package com.shevaalex.android.rickmortydatabase.ui.location.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shevaalex.android.rickmortydatabase.databinding.ItemLocationBinding
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel

class LocationAdapter(
        private val locationListener: LocationListener
): PagedListAdapter<LocationModel, LocationAdapter.LocationViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<LocationModel>(){
            override fun areItemsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
                return newItem == oldItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val itemBind = ItemLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return LocationViewHolder(itemBind)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        location?.let {
            holder.bind(it, locationListener)
        }
    }

    class LocationViewHolder(
            private val itemBind: ItemLocationBinding
    ): RecyclerView.ViewHolder(itemBind.root) {

        fun bind(location: LocationModel, locationListener: LocationListener) {
            itemBind.root.setOnClickListener {
                locationListener.onLocationClick(location)
            }
            itemBind.locationNameValue.text = location.name
            itemBind.locationDimensionValue?.let {
                it.text = location.dimension
            }
            itemBind.locationTypeValue?.let {
                it.text = location.type
            }
        }

    }

    interface LocationListener{
        fun onLocationClick(location: LocationModel)
    }

}