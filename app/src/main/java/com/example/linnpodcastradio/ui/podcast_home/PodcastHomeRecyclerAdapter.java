package com.example.linnpodcastradio.ui.podcast_home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linnpodcastradio.model.Podcast;
import com.example.linnpodcastradio.R;
import com.example.linnpodcastradio.ui.podcast_info.PodcastInfoDialog;
import com.example.linnpodcastradio.viewmodel.PodcastViewModel;

import java.util.List;

public class PodcastHomeRecyclerAdapter extends RecyclerView.Adapter<PodcastHomeRecyclerAdapter.ViewHolder>{

    private List<Podcast> podcasts;
    private RecyclerView recyclerView;
    private FragmentManager fragmentManager;
    private PodcastViewModel viewModel;

    public PodcastHomeRecyclerAdapter(RecyclerView recyclerView, PodcastViewModel viewModel, FragmentManager fragmentManager) {
        this.recyclerView = recyclerView;
        this.fragmentManager = fragmentManager;
        this.viewModel = viewModel;
    }

    public void setItems(List<Podcast> podcasts){
        this.podcasts = podcasts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PodcastHomeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.podcast_recycler_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                Podcast podcast = podcasts.get(pos);
                viewModel.openPodcast(podcast);
                openPodcastInfoDialog(podcast);
            }
        });
        PodcastHomeRecyclerAdapter.ViewHolder holder = new PodcastHomeRecyclerAdapter.ViewHolder(view);
        return holder;
    }

    private void openPodcastInfoDialog(Podcast podcast){
        DialogFragment dialog = new PodcastInfoDialog(podcast,viewModel);
        dialog.show(fragmentManager,"PodcastInfoDialog");
    }

    @Override
    public void onBindViewHolder(@NonNull PodcastHomeRecyclerAdapter.ViewHolder holder, int position) {
        Podcast podcast = podcasts.get(position);
        String string = podcast.getPosition() + ".";
        holder.position.setText(string);
        holder.trackName.setText(podcast.getTrackName());
        holder.artistName.setText(podcast.getArtistName());
        if(podcast.getBitmap() == null){
            holder.artwork.setImageResource(R.drawable.ic_artwork_default);
        }
        else{
            holder.artwork.setImageBitmap(podcast.getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        if(podcasts == null)
            return 0;
        return podcasts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView position;
        private TextView trackName;
        private TextView artistName;
        private ImageView artwork;

        private ViewHolder(View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.podcast_recycler_item_position);
            trackName = itemView.findViewById(R.id.podcast_recycler_item_title);
            artistName = itemView.findViewById(R.id.podcast_recycler_item_artist);
            artwork = itemView.findViewById(R.id.podcast_recycler_item_artwork);
        }
    }
}
