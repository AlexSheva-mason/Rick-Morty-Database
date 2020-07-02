package com.shevaalex.android.rickmortydatabase.ui.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.shevaalex.android.rickmortydatabase.databinding.ItemLocationBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.shevaalex.android.rickmortydatabase.utils.UiTranslateUtils;

import me.zhanghai.android.fastscroll.PopupTextProvider;

public class LocationAdapter extends PagedListAdapter<Location, LocationAdapter.LocationViewHolder> implements PopupTextProvider {
    private final OnLocationClickListener onLocationClickListener;
    private Context context;

    LocationAdapter(Context context, OnLocationClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.onLocationClickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<Location> DIFF_CALLBACK = new DiffUtil.ItemCallback<Location>() {
        @Override
        public boolean areItemsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
            return oldItem.getId() == newItem.getId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
            return newItem.equals(oldItem);
        }
    };

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate view binding class and pass it to ViewHolder
        ItemLocationBinding binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LocationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location currentLocation = getItem(position);
        if (currentLocation != null) {
            holder.binding.locationNameValue.setText(UiTranslateUtils.getLocationNameLocalized(context, currentLocation));
            if (holder.binding.locationDimensionValue != null &&  holder.binding.locationTypeValue != null) {
                holder.binding.locationDimensionValue.setText(UiTranslateUtils.getLocationDimensionLocalized(context, currentLocation));
                holder.binding.locationTypeValue.setText(UiTranslateUtils.getLocationTypeLocalized(context, currentLocation));
            }
        }
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        Location currentLocation = getItem(position);
        if (currentLocation != null) {
            return currentLocation.getName().substring(0, 1);
        } else {
            return "";
        }
    }

    class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemLocationBinding binding;

        LocationViewHolder(ItemLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
                onLocationClickListener.onLocationClick(getAbsoluteAdapterPosition(), v);
            }
        }
    }

    public interface OnLocationClickListener {
        void onLocationClick(int position, View v);
    }
}
