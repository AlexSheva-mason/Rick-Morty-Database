package com.shevaalex.android.rickmortydatabase.ui.character;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.ItemEpisodeBinding;
import com.shevaalex.android.rickmortydatabase.databinding.ItemHeaderRvCharacterDetailBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.shevaalex.android.rickmortydatabase.utils.TextColourUtil;
import com.shevaalex.android.rickmortydatabase.utils.UiTranslateUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class CharacterDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Character headerCharacter;
    private Location originLocation;
    private Location lastLocation;
    private final OnEpisodeListener onEpisodeListener;
    private final View.OnClickListener viewOnClickListener;
    private List<Episode> mEpisodeList = new ArrayList<>();
    private final Context context;

    CharacterDetailAdapter(OnEpisodeListener onEpisodeListener, View.OnClickListener viewOnClickListener, Context context){
        this.onEpisodeListener = onEpisodeListener;
        this.viewOnClickListener = viewOnClickListener;
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
            ItemHeaderRvCharacterDetailBinding binding = ItemHeaderRvCharacterDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new HeaderViewHolder(binding);
        } else {
            ItemEpisodeBinding binding = ItemEpisodeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EpisodeViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (headerCharacter != null) {
                if (headerViewHolder.binding.toolbarTitle != null
                        && headerViewHolder.binding.imageCharacterToolbar != null) {
                    headerViewHolder.binding.toolbarTitle.setText(UiTranslateUtils.getCharacterNameLocalized(context, headerCharacter));
                    Picasso.get()
                            .load(headerCharacter.getImgUrl())
                            .error(R.drawable.picasso_placeholder_error)
                            .into(headerViewHolder.binding.imageCharacterToolbar);
                    headerViewHolder.binding.imageCharacterToolbar.setOnClickListener(viewOnClickListener);
                }
                String charStatus = UiTranslateUtils.getCharacterStatusLocalized(context, headerCharacter);
                headerViewHolder.binding.characterStatusValue.setText(charStatus);
                if (!charStatus.equals(context.getResources().getString(R.string.species_unknown))) {
                    int color = TextColourUtil.getStatusColour(charStatus, context);
                    headerViewHolder.binding.characterStatusValue.setTextColor(color);
                } else {
                    headerViewHolder.binding.characterStatusValue.setTextColor(TextColourUtil.fetchThemeColor(R.attr.colorOnBackground, context));
                }
                headerViewHolder.binding.characterSpeciesValue.setText(UiTranslateUtils.getCharacterSpeciesLocalized(context, headerCharacter));
                headerViewHolder.binding.characterGenderValue.setText(UiTranslateUtils.getCharacterGenderLocalized(context, headerCharacter));
                if (originLocation != null) {
                    headerViewHolder.binding.characterOriginValue.setVisibility(View.GONE);
                    headerViewHolder.binding.buttonOriginLocation.setText(UiTranslateUtils.getLocationNameLocalized(context, originLocation));
                } else {
                    headerViewHolder.binding.buttonOriginLocation.setVisibility(View.GONE);
                    headerViewHolder.binding.characterOriginValue.setVisibility(View.VISIBLE);
                }
                if (lastLocation != null) {
                    headerViewHolder.binding.characterLastLocationValue.setVisibility(View.GONE);
                    headerViewHolder.binding.buttonLastLocation.setText(UiTranslateUtils.getLocationNameLocalized(context, lastLocation));
                } else {
                    headerViewHolder.binding.buttonLastLocation.setVisibility(View.GONE);
                    headerViewHolder.binding.characterLastLocationValue.setVisibility(View.VISIBLE);
                }
            }
        } else if (holder instanceof EpisodeViewHolder) {
            EpisodeViewHolder episodeHolder = (EpisodeViewHolder) holder;
            Episode currentEpisode = mEpisodeList.get(position - 1);
            // using ViewBinding class to set views
            if (currentEpisode != null) {
                episodeHolder.binding.episodeNameValue
                        .setText(String.format(context.getResources().getString(R.string.episode_name_placeholder), UiTranslateUtils.getEpisodeNameLocalized(context, currentEpisode)));
                episodeHolder.binding.episodeCodeValue.setText(currentEpisode.getCode());
                if (episodeHolder.binding.episodeAirDateValue != null) {
                    episodeHolder.binding.episodeAirDateValue.setText(UiTranslateUtils.getEpisodeAirDateLocalized(context, currentEpisode));
                }
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
        private ItemEpisodeBinding binding;

        EpisodeViewHolder(ItemEpisodeBinding binding) {
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
        private ItemHeaderRvCharacterDetailBinding binding;
        HeaderViewHolder (ItemHeaderRvCharacterDetailBinding binding) {
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
                action.setLocationName(UiTranslateUtils.getLocationNameLocalized(context, clickedLocation))
                        .setLocationDimension(UiTranslateUtils.getLocationDimensionLocalized(context, clickedLocation))
                        .setLocationType(UiTranslateUtils.getLocationTypeLocalized(context, clickedLocation))
                        .setLocationResidents(clickedLocation.getResidentsList())
                        .setLocationId(clickedLocation.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }

    public interface OnEpisodeListener {
        void onEpisodeClick(int position, View v);
    }
}
