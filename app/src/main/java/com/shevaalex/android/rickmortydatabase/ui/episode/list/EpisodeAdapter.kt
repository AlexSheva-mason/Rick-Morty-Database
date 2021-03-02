package com.shevaalex.android.rickmortydatabase.ui.episode.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.databinding.ItemEpisodeBinding
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.TRANSITION_EPISODE

class EpisodeAdapter(
        private val placeHolderString: String,
        private val episodeListener: EpisodeListener
) : PagedListAdapter<EpisodeEntity, EpisodeAdapter.EpisodeViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EpisodeEntity>() {
            override fun areItemsTheSame(oldItem: EpisodeEntity, newItem: EpisodeEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EpisodeEntity, newItem: EpisodeEntity): Boolean {
                return newItem == oldItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val itemBind = ItemEpisodeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return EpisodeViewHolder(itemBind)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = getItem(position)
        episode?.let {
            holder.bind(episode, episodeListener, placeHolderString)
        }
    }

    class EpisodeViewHolder(
            private val itemBind: ItemEpisodeBinding
    ) : RecyclerView.ViewHolder(itemBind.root) {

        fun bind(episode: EpisodeEntity, episodeListener: EpisodeListener, placeHolderString: String) {
            val context = itemBind.root.context
            itemBind.root.setOnClickListener {
                episodeListener.onEpisodeClick(episode, itemBind.episodeItem)
            }
            itemBind.episodeItem.transitionName = TRANSITION_EPISODE.plus(episode.id)
            Glide.with(context)
                    .load(episode.imageUrl)
                    .override(284, 200)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.episode_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                    )
                    .into(itemBind.episodeImage)
            itemBind.episodeNameValue.text = placeHolderString.format(episode.name)
            itemBind.episodeAirDateValue?.let {
                it.text = episode.airDate
            }
            itemBind.episodeCodeValue.text = episode.code
        }

    }

    interface EpisodeListener {
        fun onEpisodeClick(episode: EpisodeEntity, episodeCard: MaterialCardView)
    }

}