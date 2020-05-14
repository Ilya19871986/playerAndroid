package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class ContentView extends AppCompatActivity {

    VideoView videoPlayer;

    private Uri getMedia(String mediaName) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName);
    }

    private void initializePlayer() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);

        videoPlayer =  findViewById(R.id.videoPlayer);
        int s = R.raw.ss;
        Uri myVideoUri= getMedia("ss");
        videoPlayer.setVideoURI(myVideoUri);
        videoPlayer.requestFocus();
        videoPlayer.start();
    }
}
