package com.example.linnpodcastradio.tools;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.linnpodcastradio.model.PodcastEpisode;

import java.io.IOException;

public class PodcastPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {
    private MediaPlayer mediaPlayer;
    private PodcastEpisode episode;

    public PodcastPlayer(){
        mediaPlayer = new MediaPlayer();
    }

    public void startEpisode(PodcastEpisode episode){
        stop();
        this.episode = episode;
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(episode.getMp3Link());
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            episode.setStarted(true);
            episode.setPlaying(true);
        }
        catch (IllegalArgumentException | IOException | IllegalStateException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        mediaPlayer.start();
        episode.setPlaying(true);
    }

    public void pause(){
        mediaPlayer.pause();
        episode.setPlaying(false);
    }

    public void stop(){
        mediaPlayer.stop();
        if(episode!=null) {
            episode.setStarted(false);
            episode.setPlaying(false);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaplayer) {
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
}
