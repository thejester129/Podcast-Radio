package com.example.linnpodcastradio.tools.json;

import com.example.linnpodcastradio.tools.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
    private HttpHandler httpHandler;
    public JSONParser(HttpHandler httpHandler){
        this.httpHandler = httpHandler;
    }

    public JSONArray readJSONArray(String url, String arrayName){
        JSONArray jsonArray = new JSONArray();
        String jsonStr = httpHandler.makeServiceCall(url);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                jsonArray = (JSONArray) jsonObj.get(arrayName);
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
}
