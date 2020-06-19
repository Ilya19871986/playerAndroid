package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.ekran.player.model.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.MediaPlayer.*;
import static com.ekran.player.MainActivity.adapter;
import static com.ekran.player.MainActivity.orientation;
import static com.ekran.player.MainActivity.user;

public class ContentView extends AppCompatActivity  implements
        TextureView.SurfaceTextureListener {

    TextureView videoPlayer;
    MediaPlayer mp;
    // текущий файл
    private static int currentFile = 0;
    // количество файлов
    private static int countFile = 0;
    ArrayList<String>  listVideo = new ArrayList<>();
    private static Api api = null;

    private ArrayList<String> getListVideo() {
        final File rootDir = new File("/data/data/com.ekran.player/files");
        ArrayList<String> list = new ArrayList<>();
        File[] filesArray = rootDir.listFiles();
        countFile = 0;
        for (File f : filesArray) {
            if (f.isFile() && !f.getName().contains("_ftp") && f.getName().contains("mp4") &&
                    !f.getName().contains("g.mp4") && !f.getName().contains("v.mp4")) {
                Log.e("file", f.getName());
                list.add(f.getName());
                countFile++;
            }
        }
        if (countFile == 0) {
            list.add(orientation.equals("0") ? "g.mp4" : "v.mp4");
            countFile = 1;
        }
        countFile--;
        return list;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mp.setSurface(new Surface(surface));
        if (orientation.equals("1"))  updateTextureViewScaling(width,height);
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    listVideo = getListVideo();
                    if (currentFile >= countFile) {
                        currentFile = 0;
                    }
                    else {
                        currentFile++;
                    }
                    mp.reset();
                    mp.setDataSource("/data/data/com.ekran.player/files/" + listVideo.get(currentFile));
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateTextureViewScaling(int viewWidth, int viewHeight) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoPlayer.getLayoutParams();
        params.width = viewHeight;
        params.height = viewWidth;
        params.gravity = Gravity.CENTER;
        videoPlayer.setLayoutParams(params);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new Api();
        api.checkVersion();

        List<User> users = adapter.getUsers();
        user = users.get(1);

        orientation = String.valueOf(adapter.getVers().get(0).getOrientation());
        // orientation = "1";

        setContentView(R.layout.activity_content_view);

        listVideo = getListVideo();

        videoPlayer =  (TextureView) findViewById(R.id.videoPlayer);
        videoPlayer.setSurfaceTextureListener((TextureView.SurfaceTextureListener) this);

        if (orientation.equals("0") || orientation == null) {
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //videoPlayer.setRotation(0);
        }
        else {
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            videoPlayer.setRotation(-90);
        }

        mp = new MediaPlayer();

        try {
            mp.setDataSource("/data/data/com.ekran.player/files/" + listVideo.get(0));
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        user = adapter.getUser(1);

        final Timer refreshTokenTimer = new Timer();
        refreshTokenTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        api.RefreshToken(user.getUsername(), user.getPassword(), user.getPanelName());
                    }
                }, 0, 1000  * 60 * 60
        );

         final Timer getContentVideo = new Timer();
         getContentVideo.schedule(
                 new TimerTask() {
                     @Override
                     public void run() {
                         api.GetListToDelete();
                         api.GetListToUpload();
                         api.checkVersion();
                     }
                 }, 0, 1000 * 60 //* 5
         );
    }
}
