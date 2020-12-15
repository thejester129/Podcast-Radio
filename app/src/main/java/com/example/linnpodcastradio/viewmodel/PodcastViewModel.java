package com.example.linnpodcastradio.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.linnpodcastradio.model.Podcast;
import com.example.linnpodcastradio.model.PodcastEpisode;
import com.example.linnpodcastradio.repository.PodcastRepository;

import java.util.ArrayList;
import java.util.List;

public class PodcastViewModel extends ViewModel {

    private final static String SEARCH_URL = "https://itunes.apple.com/search?term=";
    private PodcastRepository podcastRepository;
    private MutableLiveData<List<Podcast>> liveTopPodcasts = new MutableLiveData<>();
    private MutableLiveData<List<Podcast>> liveSearchResults = new MutableLiveData<>();
    private MutableLiveData<Boolean> podcastsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> currentPodcastEpisodesLoading = new MutableLiveData<>();
    private MutableLiveData<List<PodcastEpisode>> liveCurrentEpisodes = new MutableLiveData<>();
    private List<Podcast> topPodcasts;

    public PodcastViewModel() {
        podcastRepository = new PodcastRepository();
        createObservers();
    }

    public void refresh(){
        topPodcasts = new ArrayList<>();
        podcastsLoading.setValue(true);
        podcastRepository.getLiveTopPodcasts(liveTopPodcasts);
    }

    private void createObservers(){
        liveTopPodcasts.observeForever(new Observer<List<Podcast>>() {
            @Override
            public void onChanged(List<Podcast> podcasts) {
                if(!podcasts.isEmpty()){
                    podcastsLoading.setValue(false);
                    topPodcasts = podcasts;
                }
            }
        });
        liveCurrentEpisodes.observeForever(new Observer<List<PodcastEpisode>>() {
            @Override
            public void onChanged(List<PodcastEpisode> episodes) {
                if(!episodes.isEmpty()){
                    currentPodcastEpisodesLoading.setValue(false);
                }
            }
        });
        liveSearchResults.observeForever(new Observer<List<Podcast>>() {
            @Override
            public void onChanged(List<Podcast> podcasts) {
                if(!podcasts.isEmpty()){
                    podcastsLoading.setValue(false);
                }
            }
        });
    }

    public void openPodcast(Podcast podcast){
        currentPodcastEpisodesLoading.setValue(true);
        podcastRepository.getPodcastEpisodes(podcast, liveCurrentEpisodes);
    }

    public void searchPodcasts(String query){
        podcastsLoading.setValue(true);
        String link = getSearchLink(query);
        podcastRepository.getLiveSearchResults(link, liveSearchResults);
    }

    private String getSearchLink(String query){
        String [] searchTerms = query.split(" ");
        StringBuilder searchString = new StringBuilder();
        for(int i = 0; i < searchTerms.length - 1; i++){
            searchString.append(searchTerms[i]);
            searchString.append("+");
        }
        searchString.append(searchTerms[searchTerms.length - 1]);
        return SEARCH_URL + searchString + "&entity=podcast";
    }

    public void clearSearch(){
        List<Podcast> searchResults = new ArrayList<>();
        liveSearchResults.setValue(searchResults);
    }

    public MutableLiveData<List<Podcast>> getLiveTopPodcasts() {
        return liveTopPodcasts;
    }

    public MutableLiveData<List<Podcast>> getLiveSearchPodcasts() {
        return liveSearchResults;
    }

    public MutableLiveData<List<PodcastEpisode>> getLiveCurrentEpisodes() {
        return liveCurrentEpisodes;
    }

    public MutableLiveData<Boolean> getPodcastsLoading() {
        return podcastsLoading;
    }

    public MutableLiveData<Boolean> getCurrentPodcastEpisodesLoading() {
        return currentPodcastEpisodesLoading;
    }

    public List<Podcast>getTopPodcasts(){
        return topPodcasts;
    }

}


