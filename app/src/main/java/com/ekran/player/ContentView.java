package com.ekran.player;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.MediaPlayer.OnCompletionListener;
import static com.ekran.player.MainActivity.sharedPrefs;
import static com.ekran.player.MainActivity.myPrefs;

public class ContentView extends AppCompatActivity  implements
        TextureView.SurfaceTextureListener {

    public static String orientation = "0";
    public static String panelId;
    public static String panelName;
    public static String userName;
    public static String userPassword;
    public static String bearerToken;
    public static String imgTime;

    private TextureView videoPlayer;
    private MediaPlayer mp;

    private ImageSwitcher mImageSwitcher;
    private int imgCurIndex = 0;
    private int imgCount = 0;
    private List<String> mImage = new ArrayList<>();
    ImageView imageView;
    private int firstStart = 0;

    private int flag = 0;

    // текущий файл
    private static int currentFile = 0;
    // количество файлов
    private static int countFile = 0;
    ArrayList<String>  listVideo = new ArrayList<>();
    private static Api api = null;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Log.e("key", "back");
        }
        return true;
    }

    private ArrayList<String> getListVideo() {
        final File rootDir = new File("/data/data/com.ekran.player/files");
        ArrayList<String> list = new ArrayList<>();
        File[] filesArray = rootDir.listFiles();
        countFile = 0;
        for (File f : filesArray) {
            if (f.isFile() && !f.getName().contains("_ftp") && f.getName().contains("mp4") &&
                    !f.getName().contains("g.mp4") && !f.getName().contains("v.mp4")) {
                list.add(f.getName());
                countFile++;
            }
        }
        if (countFile == 0 ) {
            list.add(orientation.equals("0") ? "g.mp4" : "v.mp4");
            countFile = 1;
        }
        countFile--;
        Collections.sort(list);
        return list;
    }

    private ArrayList<String> getListImg() {
        final File rootDir = new File("/data/data/com.ekran.player/files");
        ArrayList<String> list = new ArrayList<>();
        File[] filesArray = rootDir.listFiles();
        imgCount = 0;
        for (File f : filesArray) {
            if (f.isFile() && !f.getName().contains("_ftp") &&
                    (f.getName().contains(".png") ||
                            f.getName().contains(".jpg") || f.getName().contains(".jpeg"))
            ) {
                list.add(f.getName());
                imgCount++;
            }
        }
        imgCount--;
        Collections.sort(list);
        return list;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mp.setSurface(new Surface(surface));
        if (orientation.equals("1")) {
            updateTextureViewScaling(width,height);
            updateImgViewScaling(width,height);
        }

        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    listVideo = getListVideo();
                    if (currentFile >= countFile) {
                        currentFile = 0;
                        if (mImage.size() > 0) {
                            flag = 1;
                            videoPlayer.setVisibility(View.GONE);
                            mp.stop();
                            mp.reset();
                            mp.setDataSource("/data/data/com.ekran.player/files/" + listVideo.get(currentFile));
                            mp.prepare();
                            mp.start();
                            mp.pause();
                            mImage = getListImg();
                            setImg(0);
                            //imgCurIndex++;
                        }
                    }
                    else {
                        currentFile++;
                    }
                    if (flag == 0) {
                        mp.stop();
                        mp.reset();
                        mp.setDataSource("/data/data/com.ekran.player/files/" + listVideo.get(currentFile));
                        mp.prepare();
                        mp.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setImg(int idx) {
        //Uri uri = Uri.fromFile(new File("/data/data/com.ekran.player/files/" + mImage.get(idx)));
        //mImageSwitcher.setImageURI(uri);
        Bitmap bitmap = BitmapFactory.decodeFile("/data/data/com.ekran.player/files/" + mImage.get(idx));
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap newBitmap;
        // если изображение горизонтально
        if (width > height) {
            if (width > 3500 || height > 2000) {
                newBitmap = Bitmap.createScaledBitmap(bitmap, width / 3, height / 3, false);
            } else {
                newBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            }
        }
        // изображение вертикальное
        else {
            if (height > 3500 || width > 2000) {
                newBitmap = Bitmap.createScaledBitmap(bitmap, width / 3, height / 3, false);
            } else {
                newBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            }
        }

        Drawable drawable = new BitmapDrawable(newBitmap);
        mImageSwitcher.setImageDrawable(drawable);
    }
    private void updateTextureViewScaling(int viewWidth, int viewHeight) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoPlayer.getLayoutParams();
        params.width = viewHeight;
        params.height = viewWidth;
        params.gravity = Gravity.CENTER;
        videoPlayer.setLayoutParams(params);
    }

    private void updateImgViewScaling(int viewWidth, int viewHeight) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mImageSwitcher.getLayoutParams();
        params.width = viewHeight;
        params.height = viewWidth;
        params.gravity = Gravity.CENTER;
        mImageSwitcher.setLayoutParams(params);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface = null;
        mp.setDisplay(null);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content_view);

        sharedPrefs = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);

        if (sharedPrefs.contains("orientation")) {
            orientation = sharedPrefs.getString("orientation", "0");
        }
        if (sharedPrefs.contains("imgTime")) {
            imgTime = (sharedPrefs.getString("imgTime", "0"));
        }

        if (sharedPrefs.contains("panelId")) {
            panelId = sharedPrefs.getString("panelId", "0");
        }

        if (sharedPrefs.contains("panelName")) {
            panelName = sharedPrefs.getString("panelName", "0");
        }

        if (sharedPrefs.contains("username")) {
            userName = sharedPrefs.getString("username", "0");
        }

        if (sharedPrefs.contains("password")) {
            userPassword = sharedPrefs.getString("password", "0");
        }

        if (sharedPrefs.contains("token")) {
            bearerToken = sharedPrefs.getString("token", "0");
        }
/*
        Log.e("orientation", orientation);
        Log.e("imgTime", imgTime);
        //Log.e("panelId", panelId);
        Log.e("panelName", panelName);
        Log.e("username", userName);
        Log.e("password", userPassword);
        Log.e("token", bearerToken);
*/
        api = new Api();

        mImageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);

        mImageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
        /* анимация -- 1
        Animation slideInLeftAnimation = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        Animation slideOutRight = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        slideOutRight.setDuration(1500);
        slideInLeftAnimation.setDuration(1500);
        mImageSwitcher.setInAnimation(slideInLeftAnimation);
        mImageSwitcher.setOutAnimation(slideOutRight); */
        // -------
        // анимация -- 2
        Animation inAnimation = new AlphaAnimation(0, 1);
        inAnimation.setDuration(1000);
        Animation outAnimation = new AlphaAnimation(1, 0);
        outAnimation.setDuration(1000);
        mImageSwitcher.setInAnimation(inAnimation);
        mImageSwitcher.setOutAnimation(outAnimation);
        // -------
        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {

                imageView = new ImageView(ContentView.this);

                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                FrameLayout.LayoutParams params = new ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

                imageView.setLayoutParams(params);
                return imageView;
            }
        });

        listVideo = getListVideo();
        mImage = getListImg();

        videoPlayer =  (TextureView) findViewById(R.id.videoPlayer);
        videoPlayer.setSurfaceTextureListener((TextureView.SurfaceTextureListener) this);
        videoPlayer.setAnimation(outAnimation);
        if (orientation.equals("0") || orientation == null) {
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //videoPlayer.setRotation(0);
        }
        else {
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            videoPlayer.setRotation(90);
            mImageSwitcher.setRotation(90);
        }

        mp = new MediaPlayer();

        try {
            mp.setDataSource("/data/data/com.ekran.player/files/" + listVideo.get(0));
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        final Timer nextImg = new Timer();
        nextImg.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mImage = getListImg();
                                if (imgCurIndex <= imgCount && flag == 1 && imgCount >= 0) {
                                    videoPlayer.setVisibility(View.GONE);
                                    // при старте если только одна картинка
                                    if (firstStart == 0 && flag == 1)  setImg(imgCurIndex);
                                    // если одна картинка
                                    if (!(listVideo.size() == 0) && !(mImage.size() == 1)) {
                                        setImg(imgCurIndex);
                                        firstStart  = 1;
                                    }
                                    imgCurIndex++;
                                }
                                else {
                                    imgCurIndex = 0;
                                    listVideo = getListVideo();
                                    if (listVideo.size() == 1  && mImage.size() != 0 && ((listVideo.get(0).contains("g.mp4")) || (listVideo.get(0).contains("v.mp4")))) {
                                        Log.e("0 video",  "restart img: " + mImage.size());
                                        if (mImage.size() == 1) {
                                            setImg(0);
                                        }
                                        mImage = getListImg();
                                        if (mImage.size() == 0) {
                                            flag = 0;
                                        }
                                    }
                                    else {
                                        mImageSwitcher.setImageDrawable(null);
                                        if (flag == 1) {
                                            flag = 0;
                                            //mp.reset();
                                            //mp.setDataSource("/data/data/com.ekran.player/files/" + listVideo.get(0));
                                            // mp.prepare();
                                            mp.start();
                                            videoPlayer.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }, 0, 1000  * Long.parseLong(imgTime)
        );
    }
}
