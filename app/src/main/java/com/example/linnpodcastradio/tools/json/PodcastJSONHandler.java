package com.example.linnpodcastradio.tools.json;

import com.example.linnpodcastradio.model.Podcast;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PodcastJSONHandler {
    private final JSONParser jsonParser;

    public PodcastJSONHandler(JSONParser jsonParser){
        this.jsonParser = jsonParser;
    }

    public Podcast getPodcastFromURL(String url){
        JSONObject podcastJSON = getPodcastJSON(url);
        return convertJSONToPodcast(podcastJSON);
    }

    public Podcast convertJSONToPodcast(JSONObject podcastJSON){
        Podcast podcast = new Podcast();
        ObjectMapper mapper = new ObjectMapper();
        try {
            podcast = mapper.readValue(podcastJSON.toString(), Podcast.class);
        }
        catch (IOException e) {
            System.out.println("Failed to convert podcast JSON" + e.getMessage());
        }
        return podcast;
    }

    public List<Podcast> getPodcastsFromURL(String url){
        List<Podcast> podcasts = new ArrayList<>();
        JSONArray podcastJSONArray = readPodcastJSONResults(url);
        for(int i = 0; i < podcastJSONArray.length(); i++){
            try {
                JSONObject podcastJSON = podcastJSONArray.getJSONObject(i);
                Podcast podcast = convertJSONToPodcast(podcastJSON);
                podcast.setPosition(i + 1);
                podcasts.add(podcast);
            }
            catch (JSONException e) {
                System.out.println("Couldn't get podcast from JSON " + e.getMessage());
            }
        }
        return podcasts;
    }

    public JSONArray readPodcastJSONResults(String url){
        return jsonParser.readJSONArray(url,"results");
    }

    private JSONObject getPodcastJSON(String url){
        JSONArray jsonArray = readPodcastJSONResults(url);
        JSONObject podcastObject = new JSONObject();
        try {
            podcastObject = jsonArray.getJSONObject(0);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return podcastObject;
    }

}
