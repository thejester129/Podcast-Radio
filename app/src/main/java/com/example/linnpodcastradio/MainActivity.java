package com.example.linnpodcastradio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.linnpodcastradio.ui.podcast_home.PodcastHomeFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            openHomeFragment();
        }

    }

    private void openHomeFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PodcastHomeFragment fragment = new PodcastHomeFragment();
        transaction.replace(R.id.activity_main, fragment);
        transaction.commit();
    }

}