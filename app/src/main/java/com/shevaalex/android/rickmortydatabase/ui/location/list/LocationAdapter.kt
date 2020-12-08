package com.shevaalex.android.rickmortydatabase.ui.location.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.databinding.ItemLocationBinding
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.TRANSITION_LOCATION

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
            val context = itemBind.root.context
            itemBind.root.setOnClickListener {
                locationListener.onLocationClick(location, itemBind.locationImage)
            }
            itemBind.locationImage.apply {
                transitionName = TRANSITION_LOCATION.plus(location.id)
                Glide.with(context)
                        .load("https://rickandmortyapi.com/api/character/avatar/249.jpeg")
                        .apply(RequestOptions()
                                .placeholder(R.drawable.image_placeholder_error)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                        )
                        .into(this)
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
        fun onLocationClick(location: LocationModel, locImageView: ImageView)
    }

}