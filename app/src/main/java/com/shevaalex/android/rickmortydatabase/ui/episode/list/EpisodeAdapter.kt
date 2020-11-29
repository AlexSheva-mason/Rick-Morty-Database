package com.shevaalex.android.rickmortydatabase.ui.episode.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shevaalex.android.rickmortydatabase.databinding.ItemEpisodeBinding
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel

class EpisodeAdapter(
        private val placeHolderString: String,
        private val episodeListener: EpisodeListener
): PagedListAdapter<EpisodeModel, EpisodeAdapter.EpisodeViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<EpisodeModel>() {
            override fun areItemsTheSame(oldItem: EpisodeModel, newItem: EpisodeModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EpisodeModel, newItem: EpisodeModel): Boolean {
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
    ): RecyclerView.ViewHolder(itemBind.root) {

        fun bind(episode: EpisodeModel, episodeListener: EpisodeListener, placeHolderString: String) {
            itemBind.root.setOnClickListener {
                episodeListener.onEpisodeClick(episode)
            }
            itemBind.episodeNameValue.text = placeHolderString.format(episode.name)
            itemBind.episodeAirDateValue?.let {
                it.text = episode.airDate
            }
            itemBind.episodeCodeValue.text = episode.code
        }

    }

    interface EpisodeListener{
        fun onEpisodeClick(episode: EpisodeModel)
    }

}