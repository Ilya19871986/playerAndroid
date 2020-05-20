package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static android.media.MediaPlayer.*;

public class ContentView extends AppCompatActivity {

    VideoView videoPlayer;
    // текущий файл
    private static int currentFile = 0;
    // количество файлов
    private static int countFile = 0;
    ArrayList<String>  listVideo = new ArrayList<>();

    private Uri getMedia(String mediaName) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName);
    }

    // получаем список файлов
    private ArrayList<String> getListVideo() {
        ArrayList<String> res = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        countFile  = fields.length - 1;
        for(int count = 0; count <= countFile; count++) {
            res.add(fields[count].getName());
        }
        return  res;
    }

    private void initializePlayer() {
        Uri videoUri = getMedia(listVideo.get(currentFile));
        videoPlayer.setVideoURI(videoUri);
        videoPlayer.start();
        videoPlayer.setOnCompletionListener(
                new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (currentFile == countFile ) {
                            currentFile = 0;
                        } else {
                            currentFile++;
                        }
                        //Toast.makeText(ContentView.this, "currentFile", Toast.LENGTH_SHORT).show();

                        Uri videoUri = getMedia(listVideo.get(currentFile));
                        videoPlayer.setVideoURI(videoUri);

                        videoPlayer.seekTo(0);
                        videoPlayer.start();
                    }
                }
        );
    }

    private void releasePlayer() {
        videoPlayer.stopPlayback();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);

        listVideo = getListVideo();

        videoPlayer =  findViewById(R.id.videoPlayer);
        videoPlayer.start();
    }
}
