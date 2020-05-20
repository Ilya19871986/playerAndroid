package com.ekran.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ekran.player.model.User;

public class MainActivity extends AppCompatActivity {

    private DatabaseAdapter adapter;
    private long userId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new DatabaseAdapter(this);
        adapter.open();
    }

    public void go(View view) {
        Intent intent = new Intent(this, ContentView.class);

        EditText login = (EditText) findViewById(R.id.login);
        EditText pass = (EditText) findViewById(R.id.pass);

        Api api = new Api();
        api.AuthCreatePanel(login.getText().toString(), pass.getText().toString());
        User user = new User(1, "ilya", "token", "test");
        //adapter.insert(user);
        user = adapter.getUser(1);
        adapter.close();
        Log.e("username", user.getUsername());
        startActivity(intent);
    }
}
