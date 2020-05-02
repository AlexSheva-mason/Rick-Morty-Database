package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.CharacterItemSmallBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class CharacterAuxAdapter extends RecyclerView.Adapter<CharacterAuxAdapter.CharacterViewHolder> {
    private final OnCharacterListener onCharacterListener;
    private List<Character> mCharacterList = new ArrayList<>();

    CharacterAuxAdapter (OnCharacterListener onCharacterListener){
        this.onCharacterListener = onCharacterListener;
    }

    void setCharacterList (List<Character> mCharacterList) {
        this.mCharacterList = mCharacterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterItemSmallBinding binding = CharacterItemSmallBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CharacterViewHolder(onCharacterListener, binding);
    }

    @Override
    public int getItemCount() {
        return mCharacterList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character currentCharacter = mCharacterList.get(position);
        // using View Binding class to set views without calling findViewById
        Picasso.get().load(currentCharacter.getImgUrl()).error(R.drawable.picasso_placeholder_error)
                .fit().centerCrop().into(holder.binding.characterImage);
        holder.binding.characterNameValue.setText(currentCharacter.getName());
        holder.binding.characterGenderValue.setText(currentCharacter.getGender());
        holder.binding.characterSpeciesValue.setText(currentCharacter.getSpecies());
        holder.binding.characterStatusValue.setText(currentCharacter.getStatus());
    }

    static class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnCharacterListener onCharacterListener;
        private CharacterItemSmallBinding binding;

        CharacterViewHolder(OnCharacterListener onCharacterListener, CharacterItemSmallBinding binding) {
            super(binding.getRoot());
            this.onCharacterListener = onCharacterListener;
            itemView.setOnClickListener(this);
            this.binding = binding;
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
