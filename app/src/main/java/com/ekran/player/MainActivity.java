package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ekran.player.model.Content;
import com.ekran.player.model.User;
import com.ekran.player.model.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DatabaseAdapter adapter;
    private long userId=0;
    public static User user = null;
    public static String version = "3.0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ContentView.class);

        adapter = new DatabaseAdapter(this);
        adapter.open();
        //List<Version> versions = adapter.getVers(); Log.e("ver", versions.get(0).getVersion());
        //adapter.insertVersion(new Version(1, "3.0.0", "0"));
        //adapter.updateVersion(new Version(1, "3.0.0", "0"));


        //adapter.delAllContent(); adapter.delAllUser();
        // если есть авторизация
        if (adapter.getCount() != 0)  {
            startActivity(intent);
        }
    }

    public void Auth(View view) throws IOException {
        EditText login = (EditText) findViewById(R.id.login);
        EditText pass = (EditText) findViewById(R.id.pass);
        EditText panelName = (EditText) findViewById(R.id.panelName);
        Button button = (Button) findViewById(R.id.entering);
        copyFile();
        Api api = new Api(this);
        api.AuthCreatePanel(login.getText().toString(), pass.getText().toString(), panelName.getText().toString());
        // начальная установка версии и ориентации
        adapter.insertVersion(new Version(1, version, "0"));
        button.setClickable(false);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Перезагрузка!", Toast.LENGTH_LONG);
        toast.show();

        Intent mStartActivity = new Intent(this, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        finish();
    }

    public void copyFile() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.krug);
        byte[] buffer = new byte[is.available()];
        File folder = new File("/data/data/com.ekran.player/files");
        if (!folder.exists()) folder.mkdirs();
        OutputStream os = new FileOutputStream(new File("/data/data/com.ekran.player/files/krug.mp4"));
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
