package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.shevaalex.android.rickmortydatabase.databinding.EpisodeItemBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;

public class EpisodeAdapter extends PagedListAdapter<Episode, EpisodeAdapter.EpisodeViewHolder> implements SectionTitleProvider {
    private OnEpisodeClickListener clickListener;

    EpisodeAdapter (OnEpisodeClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<Episode> DIFF_CALLBACK = new DiffUtil.ItemCallback<Episode>() {
        @Override
        public boolean areItemsTheSame(@NonNull Episode oldItem, @NonNull Episode newItem) {
            return false;
        }
        @Override
        public boolean areContentsTheSame(@NonNull Episode oldItem, @NonNull Episode newItem) {
            return false;
        }
    };

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate view binding class and pass it to ViewHolder
        EpisodeItemBinding binding = EpisodeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EpisodeViewHolder(clickListener, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode currentEpisode = getItem(position);
        if (currentEpisode != null) {
            holder.binding.episodeNameValue.setText(currentEpisode.getName());
            holder.binding.episodeAirDateValue.setText(currentEpisode.getAirDate());
            holder.binding.episodeCodeValue.setText(currentEpisode.getCode());
        }
    }

    @Override
    public String getSectionTitle(int position) {
        Episode currentEpisode = getItem(position);
        if (currentEpisode != null) {
            return "s" + currentEpisode.getCode().substring(2,3) + "e" + currentEpisode.getCode().substring(4);
        } else {
            return "";
        }
    }

    static class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnEpisodeClickListener clickListener;
        private EpisodeItemBinding binding;

        EpisodeViewHolder (OnEpisodeClickListener clickListener, EpisodeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                clickListener.onEpisodeClick(getAdapterPosition(), v);
            }
        }
    }

    public interface OnEpisodeClickListener {
        void onEpisodeClick (int position, View v);
    }
}
