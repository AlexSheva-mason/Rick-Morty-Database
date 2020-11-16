package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.ItemEpisodeBinding;
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel;

import me.zhanghai.android.fastscroll.PopupTextProvider;

public class EpisodeAdapter
        extends PagedListAdapter<EpisodeModel, EpisodeAdapter.EpisodeViewHolder>
        implements PopupTextProvider {
    private final OnEpisodeClickListener clickListener;
    private final Context context;

    EpisodeAdapter(Context context, OnEpisodeClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<EpisodeModel> DIFF_CALLBACK
            = new DiffUtil.ItemCallback<EpisodeModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull EpisodeModel oldItem, @NonNull EpisodeModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull EpisodeModel oldItem, @NonNull EpisodeModel newItem) {
            return newItem.equals(oldItem);
        }
    };

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate view binding class and pass it to ViewHolder
        ItemEpisodeBinding binding = ItemEpisodeBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EpisodeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        EpisodeModel currentEpisode = getItem(position);
        if (currentEpisode != null) {
            holder.binding.episodeNameValue
                    .setText(String.format(context.getResources().getString(R.string.episode_name_placeholder), currentEpisode.getName()));
            if (holder.binding.episodeAirDateValue != null) {
                holder.binding.episodeAirDateValue.setText(currentEpisode.getAirDate());
            }
            holder.binding.episodeCodeValue.setText(currentEpisode.getCode());
        }
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        EpisodeModel currentEpisode = getItem(position);
        if (currentEpisode != null) {
            return "s" + currentEpisode.getCode().substring(2, 3)
                    + "e" + currentEpisode.getCode().substring(4);
        } else {
            return "";
        }
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemEpisodeBinding binding;

        EpisodeViewHolder(ItemEpisodeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
                clickListener.onEpisodeClick(getAbsoluteAdapterPosition(), v);
            }
        }
    }

    public interface OnEpisodeClickListener {
        void onEpisodeClick(int position, View v);
    }
}
