package com.shevaalex.android.rickmortydatabase.ui.character;

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

import com.shevaalex.android.rickmortydatabase.source.database.CharacterSmall;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.shevaalex.android.rickmortydatabase.utils.TextColourUtil;
import com.squareup.picasso.Picasso;


public class CharacterAdapter extends PagedListAdapter<CharacterSmall, CharacterAdapter.CharacterViewHolder> {
    private final OnCharacterListener onCharacterListener;
    private final CharacterViewModel viewModel;
    private final Context context;
    CharacterAdapter(OnCharacterListener onClickListener, CharacterViewModel viewModel, Context context) {
        super(DIFF_CALLBACK);
        this.onCharacterListener = onClickListener;
        this.viewModel = viewModel;
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<CharacterSmall> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CharacterSmall>() {

                @Override
                public boolean areItemsTheSame(@NonNull CharacterSmall oldItem, @NonNull CharacterSmall newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull CharacterSmall oldItem, @NonNull CharacterSmall newItem) {
                    return newItem.equals(oldItem);
                }
            };

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate view binding class and pass it to ViewHolder
        ItemCharacterBinding binding = ItemCharacterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CharacterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CharacterViewHolder holder, int position) {
        CharacterSmall currentCharacter = getItem(position);
        if (currentCharacter != null) {
            // using View Binding class to set views without calling findViewById
            Picasso.get()
                    .load(currentCharacter.getImgUrl())
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
                if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    holder.characterItemBinding.characterStatusValue.setTextColor(TextColourUtil.fetchThemeColor(R.attr.colorOnBackground, context));
                } else {
                    holder.characterItemBinding.characterStatusValue.setTextColor(TextColourUtil.fetchThemeColor(R.attr.colorOnPrimary, context));
                }
            }
            if (viewModel != null && holder.characterItemBinding.characterLastLocationValue != null) {
                int lastLocId = currentCharacter.getLastKnownLocation();
                if (lastLocId != 0 && viewModel.getLocationById(lastLocId) != null) {
                    Location lastLoc = viewModel.getLocationById(lastLocId);
                    holder.characterItemBinding.characterLastLocationValue.setText(lastLoc.getName());
                } else {
                    holder.characterItemBinding.characterLastLocationValue.setText(R.string.tv_character_last_loc_unknown_value);
                }
            }
        }
    }

    class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemCharacterBinding characterItemBinding;

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
