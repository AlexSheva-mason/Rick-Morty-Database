package com.shevaalex.android.rickmortydatabase.ui.character;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.EpisodeItemBinding;
import com.shevaalex.android.rickmortydatabase.databinding.RvCharacterDetailHeaderBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class EpisodeAuxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Character headerCharacter;
    private Location originLocation;
    private Location lastLocation;
    private final OnEpisodeListener onEpisodeListener;
    private List<Episode> mEpisodeList = new ArrayList<>();
    private Context context;

    EpisodeAuxAdapter(OnEpisodeListener onEpisodeListener, Context context){
        this.onEpisodeListener = onEpisodeListener;
        this.context = context;
    }

    void setEpisodeList (List<Episode> mEpisodeList) {
        this.mEpisodeList = mEpisodeList;
        notifyDataSetChanged();
    }

    void setHeaderCharacter (Character headerCharacter) {
        this.headerCharacter = headerCharacter;
    }

    void setOriginLocation (Location originLocation) {
        this.originLocation = originLocation;
    }

    void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            // inflating Header view
            RvCharacterDetailHeaderBinding binding = RvCharacterDetailHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new HeaderViewHolder(binding);
        } else {
            EpisodeItemBinding binding = EpisodeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EpisodeViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (headerCharacter != null) {
                Picasso.get().load(headerCharacter.getImgUrl()).placeholder(R.drawable.picasso_placeholder_default).error(R.drawable.picasso_placeholder_error).
                        fit().centerInside().into(headerViewHolder.binding.characterImage);
                headerViewHolder.binding.characterStatusValue.setText(headerCharacter.getStatus());
                headerViewHolder.binding.characterSpeciesValue.setText(headerCharacter.getSpecies());
                if (!headerCharacter.getType().isEmpty()) {
                    headerViewHolder.binding.characterTypeValue.setText(context.getResources()
                            .getString(R.string.character_type_placeholder, headerCharacter.getType()));
                } else {
                    headerViewHolder.binding.characterTypeValue.setVisibility(View.GONE);
                }
                headerViewHolder.binding.characterGenderValue.setText(headerCharacter.getGender());
                if (originLocation != null) {
                    headerViewHolder.binding.characterOriginValue.setVisibility(View.GONE);
                    headerViewHolder.binding.buttonOriginLocation.setText(originLocation.getName());
                } else {
                    headerViewHolder.binding.buttonOriginLocation.setVisibility(View.GONE);
                    headerViewHolder.binding.characterOriginValue.setVisibility(View.VISIBLE);
                }
                if (lastLocation != null) {
                    headerViewHolder.binding.characterLastLocationValue.setVisibility(View.GONE);
                    headerViewHolder.binding.buttonLastLocation.setText(lastLocation.getName());
                } else {
                    headerViewHolder.binding.buttonLastLocation.setVisibility(View.GONE);
                    headerViewHolder.binding.characterLastLocationValue.setVisibility(View.VISIBLE);
                }
            }
        } else if (holder instanceof EpisodeViewHolder) {
            EpisodeViewHolder episodeHolder = (EpisodeViewHolder) holder;
            Episode currentEpisode = mEpisodeList.get(position - 1);
            // using View Binding class to set views
            if (currentEpisode != null) {
                episodeHolder.binding.episodeNameValue.setText(currentEpisode.getName());
                episodeHolder.binding.episodeCodeValue.setText(currentEpisode.getCode());
                episodeHolder.binding.episodeAirDateValue.setText(currentEpisode.getAirDate());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEpisodeList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }


    class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private EpisodeItemBinding binding;

        EpisodeViewHolder(EpisodeItemBinding binding) {
            super(binding.getRoot());
            itemView.setOnClickListener(this);
            this.binding = binding;
        }

        @Override
        public void onClick(View v) {
            if (getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
                onEpisodeListener.onEpisodeClick(getAbsoluteAdapterPosition() - 1, v);
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RvCharacterDetailHeaderBinding binding;
        HeaderViewHolder (RvCharacterDetailHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.buttonOriginLocation.setOnClickListener(this);
            binding.buttonLastLocation.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Location clickedLocation = null;
            if (v.getId() == binding.buttonLastLocation.getId()) {
                clickedLocation = lastLocation;
            } else if (v.getId() == binding.buttonOriginLocation.getId()) {
                clickedLocation = originLocation;
            }
            CharacterDetailFragmentDirections.ToLocationDetailFragmentAction2 action =
                    CharacterDetailFragmentDirections.toLocationDetailFragmentAction2();
            if (clickedLocation != null) {
                action.setLocationName(clickedLocation.getName()).setLocationDimension(clickedLocation.getDimension())
                        .setLocationType(clickedLocation.getType()).setLocationResidents(clickedLocation.getResidentsList())
                        .setLocationId(clickedLocation.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }

    public interface OnEpisodeListener {
        void onEpisodeClick(int position, View v);
    }
}
