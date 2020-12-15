package com.example.linnpodcastradio.tools.rss;

import com.example.linnpodcastradio.model.Podcast;
import com.example.linnpodcastradio.model.PodcastEpisode;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static org.junit.Assert.*;

public class PodcastEpisodeFeedHandlerTest {
    PodcastEpisodeFeedHandler podcastEpisodeFeedHandler;
    List<PodcastEpisode>correctEpisodes;
    @Before
    public void setUp() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        podcastEpisodeFeedHandler = new PodcastEpisodeFeedHandler();
        saxParser.parse(new File("samples/example-podcast.rss"), podcastEpisodeFeedHandler);
        initCorrectEpisodes();
    }

    private void initCorrectEpisodes(){
        correctEpisodes = new ArrayList<>();
        PodcastEpisode episode = new PodcastEpisode("Episode 7: And Now I Can See Everything","https://traffic.megaphone.fm/GLT5921712103.mp3","Wed, 18 Apr 2018");
        correctEpisodes.add(episode);
        episode = new PodcastEpisode("Episode 6: I Feel Like A Million Dollars","https://traffic.megaphone.fm/GLT6871187566.mp3?updated=1523994341","Wed, 18 Apr 2018");
        correctEpisodes.add(episode);
        episode = new PodcastEpisode("Episode 5: Tortilla!","https://traffic.megaphone.fm/GLT4273834999.mp3","Wed, 18 Apr 2018");
        correctEpisodes.add(episode);
        episode = new PodcastEpisode("Episode 4: She Likes to Camp Alone in the Finnish Winter","https://traffic.megaphone.fm/GLT1835944033.mp3","Wed, 18 Apr 2018");
        correctEpisodes.add(episode);
        episode = new PodcastEpisode("Episode 3: Why Are We Like This?","https://traffic.megaphone.fm/GLT8243970918.mp3","Wed, 18 Apr 2018");
        correctEpisodes.add(episode);
        episode = new PodcastEpisode("Episode 2: Every Day Goes By Faster and Faster","https://traffic.megaphone.fm/GLT1268300753.mp3","Wed, 18 Apr 2018");
        correctEpisodes.add(episode);
        episode = new PodcastEpisode("Episode 1: This Is the Way Up","https://traffic.megaphone.fm/GLT7178834586.mp3","Wed, 18 Apr 2018");
        correctEpisodes.add(episode);
    }

    @Test
    public void getPodcastEpisodes() {
        List<PodcastEpisode>podcastEpisodes = podcastEpisodeFeedHandler.getPodcastEpisodes();
        for(int i = 0; i < correctEpisodes.size(); i++){
            assertTrue(podcastsHaveSameFields(podcastEpisodes.get(i), correctEpisodes.get(i)));
        }
    }

    private boolean podcastsHaveSameFields(PodcastEpisode podcastEpisode1, PodcastEpisode podcastEpisode2){
        if(podcastEpisode1.getTitle().equals(podcastEpisode2.getTitle())
        || podcastEpisode1.getMp3Link().equals(podcastEpisode2.getMp3Link())
        || podcastEpisode1.getPubDate().equals(podcastEpisode2.getPubDate())){
            return true;
        }
        return false;
    }
}