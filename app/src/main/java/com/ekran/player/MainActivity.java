package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ekran.player.model.User;
import com.ekran.player.model.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ekran.player.Api.reload;

public class MainActivity extends AppCompatActivity {

    public static DatabaseAdapter adapter = null;
    private long userId=0;
    public static User user = null;
    public static String version = "3.0.0";
    public static String orientation = "0";
    public static int statusFtp = 0; // 1 - идет загрузка
    public  static  String BearerToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(this, ContentView.class);
        adapter = new DatabaseAdapter(this);
        adapter.open();

         //adapter.delAllContent(); adapter.delAllUser(); adapter.delVersion();
        // если есть авторизация
        if (adapter.getCount() > 0)  {
            List<User> users = adapter.getUsers();
             BearerToken = users.get(1).getToken();
             Log.e("token", BearerToken);
            startActivity(intent);
        }
        //updateApp();
        /*
        final Timer reloadAfterChangeSettings = new Timer();
        reloadAfterChangeSettings.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (reload) {
                                    reload = false;
                                    restart();
                                }
                            }
                        });
                    }},0,1000 * 60
                );*/
    }

    public void updateApp() {
        try {
            File file = new File("/data/data/com.ekran.player/files/apprelease.apk");
            if (file.exists()) {

                file.setReadable(true, false);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);
                Log.e("update", "ok");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Auth(View view) throws IOException {
        EditText login = (EditText) findViewById(R.id.login);
        EditText pass = (EditText) findViewById(R.id.pass);
        EditText panelName = (EditText) findViewById(R.id.panelName);
        Button button = (Button) findViewById(R.id.entering);

        if (login.getText().toString() != "" && pass.getText().toString() != "" && panelName.getText().toString() != "") {
            button.setClickable(false);
            copyFile();
            Api api = new Api();
            api.AuthCreatePanel(login.getText().toString(), pass.getText().toString(), panelName.getText().toString());
            // начальная установка версии и ориентации
            adapter.insertVersion(new Version(1, version, "0", "5"));

            restart();
        }
    }

    private void restart() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Перезагрузка!", Toast.LENGTH_LONG);
        toast.show();

        Intent mStartActivity = new Intent(this, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,
                mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        finish();
    }

    public void copyFile() throws IOException {
        File folder = new File("/data/data/com.ekran.player/files");
        if (!folder.exists()) folder.mkdirs();
            InputStream is = getResources().openRawResource(R.raw.g);
            byte[] buffer = new byte[is.available()];
            OutputStream os = new FileOutputStream(new File("/data/data/com.ekran.player/files/g.mp4"));
            is.read(buffer, 0, buffer.length);
            os.write(buffer, 0, buffer.length);
            is.close(); os.close(); is = null; os = null;

            is = getResources().openRawResource(R.raw.v);
            buffer = new byte[is.available()];
            os = new FileOutputStream(new File("/data/data/com.ekran.player/files/v.mp4"));
            is.read(buffer, 0, buffer.length);
            os.write(buffer, 0, buffer.length);
            is.close(); os.close(); is = null; os = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.close();
    }
}
