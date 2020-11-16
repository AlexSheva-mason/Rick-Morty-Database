package com.shevaalex.android.rickmortydatabase.ui.character.list;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.databinding.ItemCharacterBinding;
import com.shevaalex.android.rickmortydatabase.R;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel;
import com.shevaalex.android.rickmortydatabase.utils.TextColourUtil;
import com.squareup.picasso.Picasso;


public class CharacterAdapter
        extends PagedListAdapter<CharacterModel, CharacterAdapter.CharacterViewHolder> {
    private final OnCharacterListener onCharacterListener;
    private final Context context;

    CharacterAdapter(Context context,
                     OnCharacterListener onClickListener) {
        super(DIFF_CALLBACK);
        this.onCharacterListener = onClickListener;
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<CharacterModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CharacterModel>() {

                @Override
                public boolean areItemsTheSame(@NonNull CharacterModel oldItem,
                                               @NonNull CharacterModel newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull CharacterModel oldItem,
                                                  @NonNull CharacterModel newItem) {
                    return newItem.equals(oldItem);
                }
            };

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate view binding class and pass it to ViewHolder
        ItemCharacterBinding binding = ItemCharacterBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CharacterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CharacterViewHolder holder, int position) {
        CharacterModel currentCharacter = getItem(position);
        if (currentCharacter != null) {
            // using View Binding class to set views without calling findViewById
            Picasso.get()
                    .load(currentCharacter.getImageUrl())
                    .placeholder(R.drawable.picasso_placeholder_error)
                    .error(R.drawable.picasso_placeholder_error)
                    .into(holder.characterItemBinding.characterImage);
            holder.characterItemBinding.characterName.setText(currentCharacter.getName());
            if (holder.characterItemBinding.characterGenderValue != null) {
                holder.characterItemBinding.characterGenderValue.setText(currentCharacter.getGender());
            }
            holder.characterItemBinding.characterSpeciesValue.setText(currentCharacter.getSpecies());
            holder.characterItemBinding.characterStatusValue.setText(currentCharacter.getStatus());
            if (!currentCharacter.getStatus().equals(context.getResources().getString(R.string.species_unknown))) {
                int color = TextColourUtil.getStatusColour(currentCharacter.getStatus(), context);
                holder.characterItemBinding.characterStatusValue.setTextColor(color);
            } else {
                if (context.getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT) {
                    holder
                            .characterItemBinding
                            .characterStatusValue
                            .setTextColor(TextColourUtil.fetchThemeColor(R.attr.colorOnBackground, context));
                } else {
                    holder
                            .characterItemBinding
                            .characterStatusValue
                            .setTextColor(TextColourUtil.fetchThemeColor(R.attr.colorOnPrimary, context));
                }
            }
            if (holder.characterItemBinding.characterLastLocationValue != null) {
                LinkedLocationModel lastLoc = currentCharacter.getLastLocation();
                holder.characterItemBinding.characterLastLocationValue.setText(lastLoc.getName());
            }
        }
    }

    class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemCharacterBinding characterItemBinding;

        CharacterViewHolder(ItemCharacterBinding binding) {
            super(binding.getRoot());
            itemView.setOnClickListener(this);
            characterItemBinding = binding;
        }

        @Override
        public void onClick(View v) {
            if (getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
                onCharacterListener.onCharacterClick(getAbsoluteAdapterPosition(), v);
            }
        }
    }

    public interface OnCharacterListener {
        void onCharacterClick(int position, View v);
    }
}
