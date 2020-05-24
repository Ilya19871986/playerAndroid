package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ekran.player.model.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DatabaseAdapter adapter;
    private long userId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ContentView.class);

        adapter = new DatabaseAdapter(this);

        adapter.open(); adapter.delete(1);
        List<User> users = adapter.getUsers();

        // если есть авторизация
        if (adapter.getCount() != 0)  {
            Log.e("token:", users.get(0).getToken());
            startActivity(intent);
        }
        adapter.close();
    }

    public void Auth(View view) {
        EditText login = findViewById(R.id.login);
        EditText pass = findViewById(R.id.pass);
        EditText panelName = findViewById(R.id.panelName);

        Api api = new Api();
        api.AuthCreatePanel(login.getText().toString(), pass.getText().toString(), panelName.getText().toString());
    }
}
