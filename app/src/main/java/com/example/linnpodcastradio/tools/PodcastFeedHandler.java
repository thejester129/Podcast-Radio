package com.example.linnpodcastradio.tools;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PodcastFeedHandler extends DefaultHandler {

    private List<String> podcastIDs = new ArrayList<>();
    private Stack<String> elementStack = new Stack();
    private Stack<String> objectStack = new Stack();

    public List<String> getPodcastIDs(){
        return podcastIDs;
    }

    private String currentElement() {
        return this.elementStack.peek();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.elementStack.push(qName);

//        if(qName.equals("item")){
//            String id = "";
//            this.objectStack.push(id);
//        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.elementStack.pop();
        if (qName.equals("item")) {
//            String id = this.objectStack.pop();
//            podcastIDs.add(id);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        String value = new String(ch, start, length).trim();
        if (value.length() == 0) {
            return;
        }
        if(currentElement().equals("guid")){
            podcastIDs.add(parseId(value));
        }
    }

    private String parseId(String guid){
        String[] split = guid.split("id");
        System.out.println("Podcast id " + split[1]);
        return split[1];
    }
}
