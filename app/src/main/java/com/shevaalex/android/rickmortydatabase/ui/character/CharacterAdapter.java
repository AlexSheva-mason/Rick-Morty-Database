package com.shevaalex.android.rickmortydatabase.ui.character;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.shevaalex.android.rickmortydatabase.database.Character;
import com.shevaalex.android.rickmortydatabase.R;
import com.squareup.picasso.Picasso;


public class CharacterAdapter extends PagedListAdapter<Character, CharacterAdapter.CharacterViewHolder> implements SectionTitleProvider {
    private OnCharacterListener onCharacterListener;

    CharacterAdapter(OnCharacterListener onClickListener) {
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
                    return oldItem.getName().equals(newItem.getName()) && oldItem.getTimeCreated().equals(newItem.getTimeCreated());
                }
            };

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View characterView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(characterView, onCharacterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CharacterViewHolder holder, int position) {
        Character currentCharacter = getItem(position);
        if (currentCharacter != null) {
            Picasso.get().load(currentCharacter.getImgUrl()).error(R.drawable.picasso_placeholder_error)
                    .fit().centerCrop().into(holder.imageView);
            holder.nameCharacter.setText(currentCharacter.getName());
            holder.genderCharacter.setText(currentCharacter.getGender());
            holder.speciesCharacter.setText(currentCharacter.getSpecies());
            holder.statusCharacter.setText(currentCharacter.getStatus());
            holder.lastLocationCharacter.setText(currentCharacter.getLastKnownLocation());
        }
    }

    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        //TODO set timing for bubble visibility!
        Character currentChar = getItem(position);
        if (currentChar != null) {
            return currentChar.getName().substring(0, 1);
        } else {
            return " ";
        }
    }

    static class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView nameCharacter;
        private TextView genderCharacter;
        private TextView speciesCharacter;
        private TextView statusCharacter;
        private TextView lastLocationCharacter;
        private OnCharacterListener onCharacterListener;

        CharacterViewHolder(@NonNull View itemView, OnCharacterListener onCharacterListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.character_image);
            nameCharacter = itemView.findViewById(R.id.character_name);
            genderCharacter = itemView.findViewById(R.id.character_gender_value);
            speciesCharacter = itemView.findViewById(R.id.character_species_value);
            statusCharacter = itemView.findViewById(R.id.character_status_value);
            lastLocationCharacter = itemView.findViewById(R.id.character_last_location_value);
            this.onCharacterListener = onCharacterListener;
            itemView.setOnClickListener(this);
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
