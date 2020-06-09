package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.ekran.player.model.User;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.MediaPlayer.*;
import static com.ekran.player.MainActivity.adapter;
import static com.ekran.player.MainActivity.user;

public class ContentView extends AppCompatActivity {

    VideoView videoPlayer;
    // текущий файл
    private static int currentFile = 0;
    // количество файлов
    private static int countFile = 0;
    ArrayList<String>  listVideo = new ArrayList<>();
    private static Api api = null;

    public static int flag = 0;

    private Uri getMedia(String mediaName) {
        //return Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName);
        return Uri.parse("/data/data/com.ekran.player/files/" + mediaName);
    }

    private ArrayList<String> getListVideo() {
        final File rootDir = new File("/data/data/com.ekran.player/files");
        ArrayList<String> list = new ArrayList<>();
        File[] filesArray = rootDir.listFiles();

        countFile = 0;
        for (File f : filesArray) {
            if (f.isFile() && !f.getName().contains("_ftp") && f.getName().contains("mp4") && !f.getName().contains("krug.mp4")) {
                Log.e("file", f.getName());
                list.add(f.getName());
                countFile++;
            }
        }
        if (countFile == 0) {
            list.add("krug.mp4");
            countFile = 1;
        }
        countFile--;
        return list;
    }

    private void initializePlayer() {
        if (listVideo != null) {
            Uri videoUri = getMedia(listVideo.get(currentFile));
            videoPlayer.setVideoURI(videoUri);
            videoPlayer.start();
            flag = 1;
        }
        if (videoPlayer != null) {
            videoPlayer.setOnCompletionListener(
                    new OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            try {
                                listVideo = getListVideo();
                                if (currentFile >= countFile ) {
                                    currentFile = 0;
                                } else {
                                    currentFile++;
                                }
                                if (listVideo != null) {
                                    Uri videoUri = getMedia(listVideo.get(currentFile));
                                    videoPlayer.setVideoURI(videoUri);
                                    videoPlayer.seekTo(0);
                                    videoPlayer.start();
                                    flag = 1;
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }
    }

    private void releasePlayer() {
        if (videoPlayer != null) videoPlayer.stopPlayback();
    }

    @Override
    protected void onStart() {
        try
        {
            super.onStart();
            initializePlayer();
        }
        catch (Exception exception) {
            Log.e("onStart", exception.getMessage());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        api = new Api(this);

        List<User> users = adapter.getUsers();
        user = users.get(1);

        setContentView(R.layout.activity_content_view);

        listVideo = getListVideo();
        if (listVideo != null) {
            videoPlayer =  findViewById(R.id.videoPlayer);
            videoPlayer.start();
        }
        user = adapter.getUser(1);

        final Timer refreshTokenTimer = new Timer();
        refreshTokenTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        api.RefreshToken(user.getUsername(), user.getPassword(), user.getPanelName());
                        api.checkVersion();
                    }
                }, 0, 1000  * 5//* 60 * 60
        );

         final Timer getContentVideo = new Timer();
         getContentVideo.schedule(
                 new TimerTask() {
                     @Override
                     public void run() {
                         api.GetListToDelete();
                         api.GetListToUpload();
                     }
                 }, 0, 1000 * 60 * 1
         );
    }
}
