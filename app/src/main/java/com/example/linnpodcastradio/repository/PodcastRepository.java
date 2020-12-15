package com.example.linnpodcastradio.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.example.linnpodcastradio.model.Podcast;
import com.example.linnpodcastradio.model.PodcastEpisode;
import com.example.linnpodcastradio.tools.HttpHandler;
import com.example.linnpodcastradio.tools.json.JSONParser;
import com.example.linnpodcastradio.tools.rss.PodcastEpisodeFeedHandler;
import com.example.linnpodcastradio.tools.rss.PodcastFeedHandler;
import com.example.linnpodcastradio.tools.json.PodcastJSONHandler;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class PodcastRepository {
    private final static String TOP_TEN_URL = "https://rss.itunes.apple.com/api/v1/us/podcasts/top-podcasts/all/10/explicit.rss";
    private final static String LOOKUP_URL = "https://itunes.apple.com/lookup?id=";
    private MutableLiveData<List<Podcast>> liveTopPodcasts;
    private MutableLiveData<List<Podcast>> liveSearchResults;
    private MutableLiveData<List<PodcastEpisode>> liveCurrentPodcastEpisodes;
    private List<Podcast> topPodcasts;
    private List<Podcast> searchResults;
    private List<PodcastEpisode> currentPodcastEpisodes;
    private PodcastJSONHandler podcastJSONHandler;
    private ArtworkRepository artworkRepository;

    public PodcastRepository(){
        initPodcastJSONParser();
        artworkRepository = new ArtworkRepository();
    }

    private void initPodcastJSONParser(){
        JSONParser jsonParser = new JSONParser(new HttpHandler());
        podcastJSONHandler = new PodcastJSONHandler(jsonParser);
    }

    public MutableLiveData<List<Podcast>> getLiveTopPodcasts(MutableLiveData<List<Podcast>> liveTopPodcasts){
        this.liveTopPodcasts = liveTopPodcasts;
        topPodcasts = new ArrayList<>();
        liveTopPodcasts.setValue(topPodcasts);
        new ReadTopPodcastsFromFeed().execute(TOP_TEN_URL);
        return liveTopPodcasts;
    }

    private final class ReadTopPodcastsFromFeed extends AsyncTask<String, Void, List<String>> {
        List<String> result;
        @Override
        protected List<String> doInBackground(String... params) {
            try {
                String feedUrl = params[0];
                URL url = new URL(feedUrl);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                PodcastFeedHandler podcastFeedHandler = new PodcastFeedHandler();
                saxParser.parse(new InputSource(url.openStream()), podcastFeedHandler);
                result = podcastFeedHandler.getPodcastIDs();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<String> podcastIDs) {
            readPodcastJSONObjects(podcastIDs);
            super.onPostExecute(podcastIDs);
        }
    }

    private void readPodcastJSONObjects(List<String> podcastIDs){
        for(String podcastID : podcastIDs){
            new ReadPodcastJSONTask().execute(podcastID);
        }
    }

    private final class ReadPodcastJSONTask extends AsyncTask<String, Void, Podcast> {
        @Override
        protected Podcast doInBackground(String... params) {
            String podcastID = params[0];
            String url = LOOKUP_URL + podcastID;
            return podcastJSONHandler.getPodcastFromURL(url);
        }
        @Override
        protected void onPostExecute(Podcast podcast) {
            if(podcast.getTrackName() != null){
                podcast.setPosition(topPodcasts.size() + 1);
                topPodcasts.add(podcast);
                liveTopPodcasts.setValue(topPodcasts);
                artworkRepository.loadPodcastArtwork(podcast, liveTopPodcasts, topPodcasts);
            }
            super.onPostExecute(podcast);
        }
    }

    public MutableLiveData<List<PodcastEpisode>> getPodcastEpisodes(Podcast podcast, MutableLiveData<List<PodcastEpisode>> liveCurrentPodcastEpisodes){
        this.liveCurrentPodcastEpisodes = liveCurrentPodcastEpisodes;
        currentPodcastEpisodes = new ArrayList<>();
        liveCurrentPodcastEpisodes.setValue(currentPodcastEpisodes);
        new ReadPodcastEpisodesTask().execute(podcast);
        return liveCurrentPodcastEpisodes;
    }

    private final class ReadPodcastEpisodesTask extends AsyncTask<Podcast, Void, List<PodcastEpisode>> {
        private List<PodcastEpisode> result = new ArrayList<>();
        private Podcast podcast;
        @Override
        protected List<PodcastEpisode> doInBackground(Podcast... params) {
            podcast = params[0];
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                PodcastEpisodeFeedHandler podcastEpisodeFeedHandler = new PodcastEpisodeFeedHandler();
                URL url = new URL(podcast.getFeedUrl());
                saxParser.parse(new InputSource(url.openStream()), podcastEpisodeFeedHandler);
                result = podcastEpisodeFeedHandler.getPodcastEpisodes();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<PodcastEpisode> episodes) {
            for(PodcastEpisode episode : episodes){
                episode.setPodcast(podcast);
            }
            currentPodcastEpisodes = episodes;
            liveCurrentPodcastEpisodes.setValue(currentPodcastEpisodes);
            super.onPostExecute(episodes);
        }
    }

    public MutableLiveData<List<Podcast>> getLiveSearchResults(String query, MutableLiveData<List<Podcast>> liveSearchResults){
        this.liveSearchResults = liveSearchResults;
        searchResults = new ArrayList<>();
        liveSearchResults.setValue(searchResults);
        new SearchPodcastJSONTask().execute(query);
        return liveSearchResults;
    }

    private final class SearchPodcastJSONTask extends AsyncTask<String, Void, List<Podcast>> {
        List<Podcast> result = new ArrayList<>();
        @Override
        protected List<Podcast> doInBackground(String... params) {
            String url = params[0];
            result = podcastJSONHandler.getPodcastsFromURL(url);
            return result;
        }
        @Override
        protected void onPostExecute(List<Podcast> podcasts) {
            searchResults = podcasts;
            liveSearchResults.setValue(searchResults);
            artworkRepository.loadSearchResultArtworks(searchResults, liveSearchResults);
            super.onPostExecute(podcasts);
        }
    }

}
