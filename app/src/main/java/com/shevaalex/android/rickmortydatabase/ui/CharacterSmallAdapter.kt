package com.shevaalex.android.rickmortydatabase.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.databinding.ItemCharacterSmallBinding
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.utils.TextColourUtil

class CharacterSmallAdapter(
        private val characterListener: CharacterClickListener
) : RecyclerView.Adapter<CharacterSmallAdapter.CharacterSmallViewHolder>() {

    private var characterList: List<CharacterModel>? = null

    fun setCharacterList(characterList: List<CharacterModel>) {
        this.characterList = characterList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterSmallViewHolder {
        val itemBind = ItemCharacterSmallBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return CharacterSmallViewHolder(itemBind)
    }

    override fun onBindViewHolder(holder: CharacterSmallViewHolder, position: Int) {
        val character: CharacterModel? = characterList?.get(position)
        character?.let {
            holder.bind(
                    character = it,
                    characterListener = characterListener
            )
        }
    }

    override fun getItemCount(): Int {
        return characterList?.size ?: 0
    }

    class CharacterSmallViewHolder(
            private val itemBind: ItemCharacterSmallBinding
    ) : RecyclerView.ViewHolder(itemBind.root) {

        fun bind(character: CharacterModel, characterListener: CharacterClickListener) {
            val context = itemBind.root.context
            itemBind.root.setOnClickListener {
                characterListener.onCharacterClick(character)
            }
            Glide.with(context)
                    .load(character.imageUrl)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.picasso_placeholder_error)
                    )
                    .into(itemBind.characterImage)
            itemBind.characterNameValue.text = character.name
            itemBind.characterGenderValue?.text = character.gender
            itemBind.characterSpeciesValue?.text = character.species
            itemBind.characterStatusValue?.text = character.status
            if (character.status != context.resources.getString(R.string.species_unknown)) {
                val color = TextColourUtil.getStatusColour(character.status, context)
                itemBind.characterStatusValue?.setTextColor(color)
            } else {
                itemBind.characterStatusValue?.setTextColor(
                        TextColourUtil.fetchThemeColor(R.attr.colorOnBackground, context)
                )
            }
        }

    }

    interface CharacterClickListener {
        fun onCharacterClick(character: CharacterModel)
    }
}