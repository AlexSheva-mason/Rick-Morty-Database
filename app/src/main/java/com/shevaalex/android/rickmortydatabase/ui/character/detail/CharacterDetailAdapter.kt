package com.shevaalex.android.rickmortydatabase.ui.character.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.databinding.ItemEpisodeBinding
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity

class CharacterDetailAdapter(
        private val placeHolderString: String,
        private val episodeListener: EpisodeListener
) : RecyclerView.Adapter<CharacterDetailAdapter.EpisodeViewHolder>() {

    private var episodeList: List<EpisodeEntity>? = null

    fun setEpisodeList(episodeList: List<EpisodeEntity>) {
        this.episodeList = episodeList
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
        val episode: EpisodeEntity? = episodeList?.get(position)
        episode?.let {
            holder.bind(
                    episode = it,
                    placeHolderString = placeHolderString,
                    episodeListener = episodeListener
            )
        }
    }

    override fun getItemCount(): Int {
        return episodeList?.size ?: 0
    }

    class EpisodeViewHolder(
            private val itemBind: ItemEpisodeBinding
    ) : RecyclerView.ViewHolder(itemBind.root) {

        fun bind(episode: EpisodeEntity, placeHolderString: String, episodeListener: EpisodeListener) {
            val context = itemBind.root.context
            itemBind.root.setOnClickListener {
                episodeListener.onEpisodeClick(episode)
            }
            Glide.with(context)
                    .load(episode.imageUrl)
                    .override(284, 200)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.episode_placeholder)
                    )
                    .into(itemBind.episodeImage)
            itemBind.episodeNameValue.text = placeHolderString.format(episode.name)
            itemBind.episodeCodeValue.text = episode.code
            itemBind.episodeAirDateValue?.text = episode.airDate
        }

    }

    interface EpisodeListener {
        fun onEpisodeClick(episode: EpisodeEntity)
    }
}