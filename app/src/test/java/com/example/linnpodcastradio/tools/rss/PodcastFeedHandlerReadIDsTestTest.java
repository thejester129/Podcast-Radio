package com.example.linnpodcastradio.tools.rss;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static org.junit.Assert.*;

public class PodcastFeedHandlerReadIDsTestTest {
    PodcastFeedHandler podcastFeedHandler;
    List<String> correctIds;
    @Before
    public void setUp() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        podcastFeedHandler = new PodcastFeedHandler();
        saxParser.parse(new File("samples/top-podcasts.rss"), podcastFeedHandler);
        initCorrectIds();
    }

    private void initCorrectIds(){
            correctIds = new ArrayList<>();
            correctIds.add("1369393780");
            correctIds.add("1369393683");
            correctIds.add("1374669280");
            correctIds.add("1357657583");
            correctIds.add("1375719994");
            correctIds.add("1200361736");
            correctIds.add("1369567177");
            correctIds.add("1264843400");
            correctIds.add("360084272");
            correctIds.add("1357695290");
    }

    @Test
    public void getPodcastIDs() {
        List<String> ids = podcastFeedHandler.getPodcastIDs();
        for(int i =0; i< ids.size(); i++){
            assertEquals(ids.get(i), correctIds.get(i));
        }
    }
}