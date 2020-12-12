package com.example.linnpodcastradio.tools;

import com.example.linnpodcastradio.model.PodcastEpisode;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PodcastEpisodeHandler extends DefaultHandler {
    private List<PodcastEpisode> podcastEpisodes = new ArrayList<>();
    private Stack<String> elementStack = new Stack();
    private Stack<PodcastEpisode> episodeStack = new Stack();
    private Stack<String> linkStack = new Stack();

    public List<PodcastEpisode> getPodcastEpisodes(){
        return podcastEpisodes;
    }

    private String currentElement() {
        return this.elementStack.peek();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.elementStack.push(qName);

        if(qName.equals("item")){
            PodcastEpisode episode = new PodcastEpisode();
            this.episodeStack.push(episode);
        }
        else if(qName.equals("enclosure")){
            this.linkStack.push(attributes.getValue("url"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.elementStack.pop();
        if (qName.equals("item")) {
            PodcastEpisode episode = this.episodeStack.pop();
            episode.setMp3Link(linkStack.pop());
            podcastEpisodes.add(episode);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        String value = new String(ch, start, length).trim();
        if (value.length() == 0 || episodeStack.isEmpty()) {
            return;
        }
        PodcastEpisode episode = this.episodeStack.peek();
        switch (currentElement()) {
            case "title":
                episode.setTitle(value);
                break;
            case "pubDate":
                episode.setPubDate(cropPubDate(value));
                break;
        }
    }

    private String cropPubDate(String pubDate){
        return pubDate.substring(0, Math.min(pubDate.length(), 16));
    }
}
