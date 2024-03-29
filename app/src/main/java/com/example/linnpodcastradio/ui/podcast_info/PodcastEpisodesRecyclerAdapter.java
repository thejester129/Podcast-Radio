package com.example.linnpodcastradio.ui.podcast_info;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linnpodcastradio.R;
import com.example.linnpodcastradio.model.Podcast;
import com.example.linnpodcastradio.model.PodcastEpisode;
import com.example.linnpodcastradio.tools.PodcastPlayer;

import java.util.List;

public class PodcastEpisodesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<PodcastEpisode> episodes;
    private RecyclerView recyclerView;
    private PodcastPlayer podcastPlayer;
    private Podcast podcast;

    public PodcastEpisodesRecyclerAdapter(RecyclerView recyclerView, Podcast podcast,PodcastPlayer podcastPlayer) {
        this.recyclerView = recyclerView;
        this.podcast = podcast;
        this.podcastPlayer = podcastPlayer;
    }

    public void setItems(List<PodcastEpisode> episodes){
        this.episodes = episodes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.podcast_episode_recycler_header, parent, false);
            return new HeaderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.podcast_episode_recycler_item, parent, false);
            view.setOnClickListener(podcastOnClickListener);
            return new ItemViewHolder(view);
        }
    }

    private final View.OnClickListener podcastOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = recyclerView.getChildAdapterPosition(v) - 1;
            PodcastEpisode episode = episodes.get(pos);
            if(!episode.isStarted()){
                podcastPlayer.startEpisode(episode);
            }
            else{
                if(episode.isPlaying()){
                    podcastPlayer.pause();
                }
                else{
                    podcastPlayer.play();
                }
            }
            notifyDataSetChanged();
        }
    };

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.artist.setText(podcast.getArtistName());
            headerViewHolder.title.setText(podcast.getTrackName());
            headerViewHolder.genre.setText(podcast.getPrimaryGenreName());
            headerViewHolder.country.setText(podcast.getCountry());
            if(podcast.getBitmap() != null){
                headerViewHolder.artwork.setImageBitmap(podcast.getBitmap());
            }
            else{
                headerViewHolder.artwork.setImageResource(R.drawable.ic_artwork_default);
            }
        }
        else{
            PodcastEpisode episode = episodes.get(position - 1);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.title.setText(episode.getTitle());
            itemViewHolder.pubDate.setText(episode.getPubDate());
            itemViewHolder.playButton.setVisibility(!episode.isStarted() || !episode.isPlaying() ? View.VISIBLE : View.GONE);
            itemViewHolder.pauseButton.setVisibility(episode.isPlaying() ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        // + 1 to compensate for header
        if(episodes == null)
            return 1;
        return episodes.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private ImageView artwork;
        private TextView title;
        private TextView artist;
        private TextView genre;
        private TextView country;

        private HeaderViewHolder(View itemView) {
            super(itemView);
            artwork = itemView.findViewById(R.id.podcast_episode_recycler_header_artwork);
            title = itemView.findViewById(R.id.podcast_episode_recycler_header_title);
            artist = itemView.findViewById(R.id.podcast_episode_recycler_header_artist);
            genre = itemView.findViewById(R.id.podcast_episode_recycler_header_genre);
            country = itemView.findViewById(R.id.podcast_episode_recycler_header_country);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView pubDate;
        private ImageView playButton;
        private ImageView pauseButton;

        private ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.podcast_episode_recycler_item_title);
            pubDate = itemView.findViewById(R.id.podcast_episode_recycler_item_pub_date);
            playButton = itemView.findViewById(R.id.podcast_episode_recycler_item_play_button);
            pauseButton = itemView.findViewById(R.id.podcast_episode_recycler_item_pause_button);
        }
    }
}