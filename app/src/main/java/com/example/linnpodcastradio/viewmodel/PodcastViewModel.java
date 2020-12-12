package com.example.linnpodcastradio.viewmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linnpodcastradio.model.Podcast;
import com.example.linnpodcastradio.model.PodcastEpisode;
import com.example.linnpodcastradio.tools.HttpHandler;
import com.example.linnpodcastradio.tools.PodcastEpisodeHandler;
import com.example.linnpodcastradio.tools.PodcastFeedHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class PodcastViewModel extends ViewModel {
    private final static String TOP_TEN_URL = "https://rss.itunes.apple.com/api/v1/us/podcasts/top-podcasts/all/10/explicit.rss";
    private final static String LOOKUP_URL = "https://itunes.apple.com/lookup?id=";
    private final static String SEARCH_URL = "https://itunes.apple.com/search?term=";
    private MutableLiveData<List<Podcast>> liveTopPodcasts = new MutableLiveData<>();
    private MutableLiveData<List<Podcast>> liveSearchPodcasts = new MutableLiveData<>();
    private List<Podcast> topPodcasts;
    private List<Podcast> searchPodcasts;
    private MutableLiveData<List<PodcastEpisode>> currentPodcastEpisodes = new MutableLiveData<>();
    private MutableLiveData<Boolean> podcastsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> currentPodcastEpisodesLoading = new MutableLiveData<>();

    public PodcastViewModel() {
        refresh();
    }

    public void refresh(){
        topPodcasts = new ArrayList<>();
        podcastsLoading.setValue(true);
        new ReadTopPodcastIDsTask().execute();
    }

    private final class ReadTopPodcastIDsTask extends AsyncTask<Void, Void, List<String>> {
        List<String> result;
        @Override
        protected List<String> doInBackground(Void... voids) {
            try {
                URL url = new URL(TOP_TEN_URL);
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
            JSONObject podcastJSON = getPodcastJSON(url);
            return convertJSONToPodcastObject(podcastJSON);
        }
        @Override
        protected void onPostExecute(Podcast podcast) {
            podcast.setPosition(topPodcasts.size() + 1);
            topPodcasts.add(podcast);
            liveTopPodcasts.setValue(topPodcasts);
            podcastsLoading.setValue(false);
            new GetArtworkBitmapTask().execute(podcast);
            super.onPostExecute(podcast);
        }
    }

    private JSONObject getPodcastJSON(String url){
        JSONObject podcastObject = new JSONObject();
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(url);
        System.out.println("JSON String " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray jsonArray = (JSONArray) jsonObj.get("results");
                podcastObject = jsonArray.getJSONObject(0);
            }
            catch (final JSONException e) {
                System.out.println( "Json parsing error: " + e.getMessage());
            }
        }
        else {
            System.out.println("Couldn't get json from server.");
        }
        return podcastObject;
    }

    private JSONArray getPodcastListJSON(String url){
        JSONArray jsonArray = new JSONArray();
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(url);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                jsonArray = (JSONArray) jsonObj.get("results");
            }
            catch (final JSONException e) {
                System.out.println( "Json parsing error: " + e.getMessage());
            }
        }
        else {
            System.out.println("Couldn't get json from server.");
        }
        return jsonArray;
    }

    private Podcast convertJSONToPodcastObject(JSONObject podcastJSON){
        Podcast podcast = new Podcast();
        ObjectMapper mapper = new ObjectMapper();
        try {
            podcast = mapper.readValue(podcastJSON.toString(), Podcast.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to convert podcast JSON");
        }
        return podcast;
    }

    private final class GetArtworkBitmapTask extends AsyncTask<Podcast, Void, Bitmap> {
        Podcast podcast;
        Bitmap bitmap = null;
        @Override
        protected Bitmap doInBackground(Podcast... params) {
            try {
                podcast = params[0];
                URL url = new URL(podcast.getArtworkUrl());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }
            catch (IOException e) {
                System.out.println("Error setting podcast artwork " + e.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            podcast.setBitmap(bitmap);
            liveTopPodcasts.setValue(topPodcasts);
            super.onPostExecute(bitmap);
        }
    }

    public void openPodcast(Podcast podcast){
        currentPodcastEpisodes.setValue(new ArrayList<>());
        currentPodcastEpisodesLoading.setValue(true);
        new ReadPodcastEpisodesTask().execute(podcast);
    }

    private final class ReadPodcastEpisodesTask extends AsyncTask<Podcast, Void, List<PodcastEpisode>> {
        private List<PodcastEpisode> result;
        private Podcast podcast;
        @Override
        protected List<PodcastEpisode> doInBackground(Podcast... params) {
            podcast = params[0];
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                PodcastEpisodeHandler podcastEpisodeHandler = new PodcastEpisodeHandler();
                URL url = new URL(podcast.getFeedUrl());
                saxParser.parse(new InputSource(url.openStream()), podcastEpisodeHandler);
                result = podcastEpisodeHandler.getPodcastEpisodes();
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
            currentPodcastEpisodes.setValue(result);
            currentPodcastEpisodesLoading.setValue(false);
            super.onPostExecute(episodes);
        }
    }

    public void searchPodcasts(String query){
        String link = getSearchLink(query);
        new SearchPodcastJSONTask().execute(link);
        podcastsLoading.setValue(true);
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

    private final class SearchPodcastJSONTask extends AsyncTask<String, Void, List<Podcast>> {
        List<Podcast> result = new ArrayList<>();
        @Override
        protected List<Podcast> doInBackground(String... params) {
            String url = params[0];
            JSONArray podcastJSONArray = getPodcastListJSON(url);
            for(int i = 0; i < podcastJSONArray.length(); i++){
                try {
                    JSONObject podcastJSON = podcastJSONArray.getJSONObject(i);
                    Podcast podcast = convertJSONToPodcastObject(podcastJSON);
                    podcast.setPosition(i+1);
                    result.add(podcast);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<Podcast> podcasts) {
            searchPodcasts = podcasts;
            liveSearchPodcasts.setValue(searchPodcasts);
            podcastsLoading.setValue(false);
            for(Podcast podcast : podcasts){
                new GetSearchArtworkBitmapTask().execute(podcast);
            }
            super.onPostExecute(podcasts);
        }
    }

    private final class GetSearchArtworkBitmapTask extends AsyncTask<Podcast, Void, Bitmap> {
        Podcast podcast;
        Bitmap bitmap = null;
        @Override
        protected Bitmap doInBackground(Podcast... params) {
            try {
                podcast = params[0];
                URL url = new URL(podcast.getArtworkUrl());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }
            catch (IOException e) {
                System.out.println("Error setting podcast artwork " + e.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            podcast.setBitmap(bitmap);
            liveSearchPodcasts.setValue(searchPodcasts);
            super.onPostExecute(bitmap);
        }
    }

    public void clearSearch(){
        searchPodcasts = new ArrayList<>();
        liveSearchPodcasts.setValue(searchPodcasts);
    }

    public MutableLiveData<List<Podcast>> getLiveTopPodcasts() {
        return liveTopPodcasts;
    }
    public MutableLiveData<List<Podcast>> getLiveSearchPodcasts() {
        return liveSearchPodcasts;
    }

    public MutableLiveData<List<PodcastEpisode>> getCurrentPodcastEpisodes() {
        return currentPodcastEpisodes;
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


