package com.shevaalex.android.rickmortydatabase.ui.character.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shevaalex.android.rickmortydatabase.databinding.ItemEpisodeBinding
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel

class CharacterDetailAdapter(
        private val placeHolderString: String,
        private val episodeListener: EpisodeListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var episodeList: List<EpisodeModel>? = null

    fun setEpisodeList(episodeList: List<EpisodeModel>) {
        this.episodeList = episodeList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBind = ItemEpisodeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return EpisodeViewHolder(itemBind)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val episode: EpisodeModel? = episodeList?.get(position)
        when (holder) {
            is EpisodeViewHolder -> episode?.let { holder.bind(
                    episode = it,
                    placeHolderString = placeHolderString,
                    episodeListener = episodeListener
            ) }
        }
    }

    override fun getItemCount(): Int {
        return episodeList?.size?:0
    }

    class EpisodeViewHolder(
            private val itemBind: ItemEpisodeBinding
    ): RecyclerView.ViewHolder(itemBind.root) {

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