package com.shevaalex.android.rickmortydatabase.ui.character.list

import android.content.res.Configuration
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
import com.shevaalex.android.rickmortydatabase.databinding.ItemCharacterBinding
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.TextColourUtil

class CharacterAdapter(
        private val characterListener: CharacterListener
) : PagedListAdapter<CharacterModel, CharacterAdapter.CharacterViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<CharacterModel>() {
            override fun areItemsTheSame(oldItem: CharacterModel, newItem: CharacterModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CharacterModel, newItem: CharacterModel): Boolean {
                return newItem == oldItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val itemBind = ItemCharacterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return CharacterViewHolder(itemBind)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        character?.let {
            holder.bind(it, characterListener)
        }
    }

    class CharacterViewHolder(
            private val itemBind: ItemCharacterBinding
    ) : RecyclerView.ViewHolder(itemBind.root) {

        fun bind(character: CharacterModel, characterListener: CharacterListener) {
            val context = itemBind.root.context
            itemBind.root.setOnClickListener {
                characterListener.onCharacterClick(character, itemBind.characterItem)
            }
            itemBind.characterItem.transitionName = Constants.TRANSITION_CHARACTER.plus(character.id)
            itemBind.characterImage.apply {
                Glide.with(context)
                        .load(character.imageUrl)
                        .apply(RequestOptions()
                                .placeholder(R.drawable.character_placeholder)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                        )
                        .into(this)
            }
            itemBind.characterName.text = character.name
            itemBind.characterGenderValue?.let {
                it.text = character.gender
            }
            itemBind.characterSpeciesValue.text = character.species
            itemBind.characterStatusValue.text = character.status
            val color: Int
            color = if (character.status != context.resources.getString(R.string.species_unknown)) {
                TextColourUtil.getStatusColour(character.status, context)
            } else {
                if (context.resources.configuration.orientation
                        == Configuration.ORIENTATION_PORTRAIT) {
                    TextColourUtil.fetchThemeColor(R.attr.colorOnBackground, context)
                } else {
                    TextColourUtil.fetchThemeColor(R.attr.colorOnPrimary, context)
                }
            }
            itemBind.characterStatusValue.setTextColor(color)
            itemBind.characterLastLocationValue?.let {
                it.text = character.lastLocation.name
            }
        }

    }

    interface CharacterListener {
        fun onCharacterClick(character: CharacterModel, characterCard: MaterialCardView)
    }

}