package com.shevaalex.android.rickmortydatabase.ui.location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.shevaalex.android.rickmortydatabase.databinding.LocationItemBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Location;

public class LocationAdapter extends PagedListAdapter<Location, LocationAdapter.LocationViewHolder> implements SectionTitleProvider {
    private final OnLocationClickListener onLocationClickListener;

    LocationAdapter(OnLocationClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.onLocationClickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<Location> DIFF_CALLBACK = new DiffUtil.ItemCallback<Location>() {
        @Override
        public boolean areItemsTheSame(@NonNull Location oldItem, @NonNull Location newItem) { return oldItem.getId() == newItem.getId();  }
        @Override
        public boolean areContentsTheSame(@NonNull Location oldItem, @NonNull Location newItem) { return newItem.equals(oldItem);      }
    };

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate view binding class and pass it to ViewHolder
        LocationItemBinding binding = LocationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LocationViewHolder(onLocationClickListener, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location currentLocation = getItem(position);
        if (currentLocation != null) {
            holder.binding.locationNameValue.setText(currentLocation.getName());
            holder.binding.locationDimensionValue.setText(currentLocation.getDimension());
            holder.binding.locationTypeValue.setText(currentLocation.getType());
        }
    }

    @Override
    public String getSectionTitle(int position) {
        Location currentLocation = getItem(position);
        if (currentLocation != null) {
            return currentLocation.getName().substring(0, 1);
        } else {
            return "";
        }
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnLocationClickListener onLocationClickListener;
        private LocationItemBinding binding;

        LocationViewHolder(OnLocationClickListener clickListener, LocationItemBinding binding) {
            super(binding.getRoot());
            this.onLocationClickListener = clickListener;
            itemView.setOnClickListener(this);
            this.binding = binding;
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                onLocationClickListener.onLocationClick(getAdapterPosition(), v);
            }
        }
    }

    public interface OnLocationClickListener {
        void onLocationClick(int position, View v);
    }
}
