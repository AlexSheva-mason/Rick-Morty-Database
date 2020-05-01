package com.shevaalex.android.rickmortydatabase.ui.character;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.CharacterItemBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.squareup.picasso.Picasso;


public class CharacterAdapter extends PagedListAdapter<Character, CharacterAdapter.CharacterViewHolder> implements SectionTitleProvider {
    private final OnCharacterListener onCharacterListener;
    private CharacterViewModel viewModel;

    public CharacterAdapter(OnCharacterListener onClickListener, CharacterViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.onCharacterListener = onClickListener;
        this.viewModel = viewModel;
    }

    public CharacterAdapter(OnCharacterListener onClickListener) {
        super(DIFF_CALLBACK);
        this.onCharacterListener = onClickListener;
    }


    private static final DiffUtil.ItemCallback<Character> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Character>() {

                @Override
                public boolean areItemsTheSame(@NonNull Character oldItem, @NonNull Character newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Character oldItem, @NonNull Character newItem) {
                    return newItem.equals(oldItem);
                }
            };

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate view binding class and pass it to ViewHolder
        CharacterItemBinding binding = CharacterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CharacterViewHolder(onCharacterListener, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CharacterViewHolder holder, int position) {
        Character currentCharacter = getItem(position);
        if (currentCharacter != null) {
            // using View Binding class to set views without calling findViewById
            Picasso.get().load(currentCharacter.getImgUrl()).error(R.drawable.picasso_placeholder_error)
                    .fit().centerCrop().into(holder.characterItemBinding.characterImage);
            holder.characterItemBinding.characterName.setText(currentCharacter.getName());
            holder.characterItemBinding.characterGenderValue.setText(currentCharacter.getGender());
            holder.characterItemBinding.characterSpeciesValue.setText(currentCharacter.getSpecies());
            holder.characterItemBinding.characterStatusValue.setText(currentCharacter.getStatus());
            if (viewModel != null) {
                Location lastLoc = viewModel.getLocationById(currentCharacter.getLastKnownLocation());
                if (lastLoc != null) {
                    holder.characterItemBinding.characterLastLocationValue.setText(lastLoc.getName());
                }
            }
        }
    }

    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        Character currentChar = getItem(position);
        if (currentChar != null) {
            return currentChar.getName().substring(0, 1);
        } else {
            return " ";
        }
    }

    static class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnCharacterListener onCharacterListener;
        private CharacterItemBinding characterItemBinding;

        CharacterViewHolder(OnCharacterListener onCharacterListener, CharacterItemBinding binding) {
            super(binding.getRoot());
            this.onCharacterListener = onCharacterListener;
            itemView.setOnClickListener(this);
            characterItemBinding = binding;
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                onCharacterListener.onCharacterClick(getAdapterPosition(), v);
            }
        }
    }

    public interface OnCharacterListener {
        void onCharacterClick(int position, View v);
    }
}
