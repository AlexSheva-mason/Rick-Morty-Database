package com.shevaalex.android.rickmortydatabase.ui.location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.shevaalex.android.rickmortydatabase.databinding.ItemLocationBinding;
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel;

import me.zhanghai.android.fastscroll.PopupTextProvider;

public class LocationAdapter
        extends PagedListAdapter<LocationModel, LocationAdapter.LocationViewHolder>
        implements PopupTextProvider {
    private final OnLocationClickListener onLocationClickListener;

    LocationAdapter(OnLocationClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.onLocationClickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<LocationModel> DIFF_CALLBACK
            = new DiffUtil.ItemCallback<LocationModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull LocationModel oldItem, @NonNull LocationModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull LocationModel oldItem, @NonNull LocationModel newItem) {
            return newItem.equals(oldItem);
        }
    };

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate view binding class and pass it to ViewHolder
        ItemLocationBinding binding = ItemLocationBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LocationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationModel currentLocation = getItem(position);
        if (currentLocation != null) {
            holder.binding.locationNameValue.setText(currentLocation.getName());
            if (holder.binding.locationDimensionValue != null
                    &&  holder.binding.locationTypeValue != null) {
                holder.binding.locationDimensionValue.setText(currentLocation.getDimension());
                holder.binding.locationTypeValue.setText(currentLocation.getType());
            }
        }
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        LocationModel currentLocation = getItem(position);
        if (currentLocation != null) {
            return currentLocation.getName().substring(0, 1);
        } else {
            return "";
        }
    }

    class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemLocationBinding binding;

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
