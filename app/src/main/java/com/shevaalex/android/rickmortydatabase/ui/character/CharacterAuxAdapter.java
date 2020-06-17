package com.shevaalex.android.rickmortydatabase.ui.character;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.ItemCharacterSmallBinding;
import com.shevaalex.android.rickmortydatabase.source.database.CharacterSmall;
import com.shevaalex.android.rickmortydatabase.utils.CharacterAdapterUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class CharacterAuxAdapter extends RecyclerView.Adapter<CharacterAuxAdapter.CharacterViewHolder> {
    private final OnCharacterListener onCharacterListener;
    private List<CharacterSmall> mCharacterList = new ArrayList<>();
    private final Context context;

    public CharacterAuxAdapter(OnCharacterListener onCharacterListener, Context context){
        this.context = context;
        this.onCharacterListener = onCharacterListener;
    }

    public void setCharacterList (List<CharacterSmall> mCharacterList) {
        this.mCharacterList = mCharacterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCharacterSmallBinding binding = ItemCharacterSmallBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CharacterViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mCharacterList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        CharacterSmall currentCharacter = mCharacterList.get(position);
        // using View Binding class to set views without calling findViewById
        Picasso.get()
                .load(currentCharacter.getImgUrl())
                .placeholder(R.drawable.picasso_placeholder_error)
                .error(R.drawable.picasso_placeholder_error)
                .into(holder.binding.characterImage);
        holder.binding.characterNameValue.setText(currentCharacter.getName());
        holder.binding.characterGenderValue.setText(currentCharacter.getGender());
        holder.binding.characterSpeciesValue.setText(currentCharacter.getSpecies());
        holder.binding.characterStatusValue.setText(currentCharacter.getStatus());
        if (!currentCharacter.getStatus().equals("unknown")) {
            int color = CharacterAdapterUtil.getStatusColour(currentCharacter.getStatus(), context);
            holder.binding.characterStatusValue.setTextColor(color);
        } else {
            holder.binding.characterStatusValue.setTextColor(CharacterAdapterUtil.fetchThemeColor(R.attr.colorOnBackground, context));
        }
    }

    class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemCharacterSmallBinding binding;

        CharacterViewHolder(ItemCharacterSmallBinding binding) {
            super(binding.getRoot());
            itemView.setOnClickListener(this);
            this.binding = binding;
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
