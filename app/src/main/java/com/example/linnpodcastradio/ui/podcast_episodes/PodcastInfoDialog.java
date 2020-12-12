package com.example.linnpodcastradio.ui.podcast_episodes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import com.example.linnpodcastradio.R;
import com.example.linnpodcastradio.databinding.PodcastInfoDialogBinding;
import com.example.linnpodcastradio.model.Podcast;
import com.example.linnpodcastradio.model.PodcastEpisode;
import com.example.linnpodcastradio.tools.MP3Player;
import com.example.linnpodcastradio.viewmodel.PodcastViewModel;
import java.util.List;

public class PodcastInfoDialog extends DialogFragment {
    private final Podcast podcast;
    private final PodcastViewModel viewModel;
    private PodcastEpisodesRecyclerAdapter adapter;
    private MP3Player mp3Player;

    public PodcastInfoDialog(Podcast podcast, PodcastViewModel viewModel) {
        this.podcast = podcast;
        this.viewModel = viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PodcastInfoDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.podcast_info_dialog,container,false);
        binding.setPodcast(podcast);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        mp3Player = new MP3Player();
        setupBackButton(view);
        setupRecycler(view);
        setupObserver();
        return view;
    }

    private void setupBackButton(View view){
        ImageButton backButton = view.findViewById(R.id.podcast_info_dialog_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp3Player.stop();
                dismiss();
            }
        });
    }

    private void setupRecycler(View view){
        RecyclerView recyclerView = view.findViewById(R.id.podcast_info_dialog_episode_recycler);
        adapter = new PodcastEpisodesRecyclerAdapter(recyclerView,mp3Player);
        recyclerView.setAdapter(adapter);
    }

    private void setupObserver(){
        viewModel.getCurrentPodcastEpisodes().observe(this, new Observer<List<PodcastEpisode>>() {
            @Override
            public void onChanged(List<PodcastEpisode> episodes) {
                adapter.setItems(episodes);
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog){
        mp3Player.stop();
        super.onCancel(dialog);
    }

}
