package com.example.linnpodcastradio.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.example.linnpodcastradio.model.Podcast;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ArtworkRepository {
    MutableLiveData<List<Podcast>>liveTopPodcasts;
    MutableLiveData<List<Podcast>>liveSearchResults;
    List<Podcast>topPodcasts;
    List<Podcast>searchResults;

    public ArtworkRepository(){

    }

    public void loadPodcastArtwork(Podcast podcast, MutableLiveData<List<Podcast>>liveTopPodcasts, List<Podcast>topPodcasts){
        this.topPodcasts = topPodcasts;
        this.liveTopPodcasts = liveTopPodcasts;
        new GetArtworkBitmapTask().execute(podcast);
    }

    private final class GetArtworkBitmapTask extends AsyncTask<Podcast, Void, Bitmap> {
        Podcast podcast;
        @Override
        protected Bitmap doInBackground(Podcast... params) {
            podcast = params[0];
            return getBitmapFromLink(podcast.getArtworkLink());
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            podcast.setBitmap(bitmap);
            liveTopPodcasts.setValue(topPodcasts);
            super.onPostExecute(bitmap);
        }
    }

    public void loadSearchResultArtworks(List<Podcast>searchResults, MutableLiveData<List<Podcast>>liveSearchResults){
        this.searchResults = searchResults;
        this.liveSearchResults = liveSearchResults;
        for(Podcast podcast : searchResults){
            new GetSearchArtworkBitmapTask().execute(podcast);
        }
    }

    private final class GetSearchArtworkBitmapTask extends AsyncTask<Podcast, Void, Bitmap> {
        Podcast podcast;
        @Override
        protected Bitmap doInBackground(Podcast... params) {
            podcast = params[0];
            return getBitmapFromLink(podcast.getArtworkLink());
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            podcast.setBitmap(bitmap);
            liveSearchResults.setValue(searchResults);
            super.onPostExecute(bitmap);
        }
    }

    private Bitmap getBitmapFromLink(String link){
        Bitmap bitmap = null;
        try{
            URL url = new URL(link);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }
        catch (IOException e) {
            System.out.println("Error setting podcast artwork " + e.getMessage());
        }
        return bitmap;
    }
}
