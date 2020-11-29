package com.shevaalex.android.rickmortydatabase.ui.character.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shevaalex.android.rickmortydatabase.databinding.ItemEpisodeBinding
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel

class CharacterDetailAdapter(
        private val placeHolderString: String,
        private val episodeListener: EpisodeListener
) : RecyclerView.Adapter<CharacterDetailAdapter.EpisodeViewHolder>() {

    private var episodeList: List<EpisodeModel>? = null

    fun setEpisodeList(episodeList: List<EpisodeModel>) {
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
        val episode: EpisodeModel? = episodeList?.get(position)
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

        fun bind(episode: EpisodeModel, placeHolderString: String, episodeListener: EpisodeListener) {
            itemBind.root.setOnClickListener {
                episodeListener.onEpisodeClick(episode)
            }
            itemBind.episodeNameValue.text = placeHolderString.format(episode.name)
            itemBind.episodeCodeValue.text = episode.code
            itemBind.episodeAirDateValue?.text = episode.airDate
        }

    }

    interface EpisodeListener {
        fun onEpisodeClick(episode: EpisodeModel)
    }
}