package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ekran.player.model.Content;
import com.ekran.player.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DatabaseAdapter adapter;
    private long userId=0;
    public static User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ContentView.class);

        adapter = new DatabaseAdapter(this);

        adapter.open();
        adapter.delAllContent(); adapter.delAllUser();
        // если есть авторизация
        if (adapter.getCount() != 0)  {
            startActivity(intent);
        }
    }

    public void Auth(View view) throws IOException {
        EditText login = (EditText) findViewById(R.id.login);
        EditText pass = (EditText) findViewById(R.id.pass);
        EditText panelName = (EditText) findViewById(R.id.panelName);
        copyFile();
        Api api = new Api();
        api.AuthCreatePanel(login.getText().toString(), pass.getText().toString(), panelName.getText().toString());

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
