package com.shevaalex.android.rickmortydatabase.ui.character;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.databinding.EpisodeItemBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;

import java.util.ArrayList;
import java.util.List;


public class EpisodeAuxAdapter extends RecyclerView.Adapter<EpisodeAuxAdapter.EpisodeViewHolder> {
    private final OnEpisodeListener onEpisodeListener;
    private List<Episode> mEpisodeList = new ArrayList<>();

    EpisodeAuxAdapter(OnEpisodeListener onEpisodeListener){
        this.onEpisodeListener = onEpisodeListener;
    }

    void setEpisodeList (List<Episode> mEpisodeList) {
        this.mEpisodeList = mEpisodeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EpisodeItemBinding binding = EpisodeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EpisodeViewHolder(onEpisodeListener, binding);
    }

    @Override
    public int getItemCount() {
        return mEpisodeList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode clickedEpisode = mEpisodeList.get(position);
        // using View Binding class to set views
        holder.binding.episodeNameValue.setText(clickedEpisode.getName());
        holder.binding.episodeCodeValue.setText(clickedEpisode.getCode());
        holder.binding.episodeAirDateValue.setText(clickedEpisode.getAirDate());
    }

    static class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnEpisodeListener onEpisodeListener;
        private EpisodeItemBinding binding;

        EpisodeViewHolder(OnEpisodeListener onEpisodeListener, EpisodeItemBinding binding) {
            super(binding.getRoot());
            this.onEpisodeListener = onEpisodeListener;
            itemView.setOnClickListener(this);
            this.binding = binding;
        }

        @Override
        public void onClick(View v) {
            if (getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
                onEpisodeListener.onEpisodeClick(getAbsoluteAdapterPosition(), v);
            }
        }
    }

    public interface OnEpisodeListener {
        void onEpisodeClick(int position, View v);
    }
}
