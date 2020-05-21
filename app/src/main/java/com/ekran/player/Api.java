package com.ekran.player;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ekran.player.model.User;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ekran.player.MainActivity.adapter;

public class Api {
  private final String apiAddr = "http://193.124.58.144:4444";
    OkHttpClient httpClient = new OkHttpClient();
    String serverUsername = null;
    String serverToken = null;
    String MyPanel = null;

  public void AuthCreatePanel(String username, String password, final String panelName) {

      MyPanel = panelName;

      RequestBody formBody = new FormBody.Builder().add("coupon", "").build();

      HttpUrl.Builder httpBuider = HttpUrl.parse(apiAddr + "/token?username=" + username + "&password=" + password).newBuilder();

      Request request = new Request.Builder().url(httpBuider.build()).post(formBody).build();

      httpClient.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
              String mMessage = e.getMessage().toString();
              Log.e("failure Response", mMessage);
              call.cancel();
          }
          @Override
          public void onResponse(Call call, Response response) throws IOException {
              String mMessage = response.body().string();
              if (response.isSuccessful()){
                  try {
                      JSONObject json = new JSONObject(mMessage);
                      serverUsername = json.getString("username");
                      serverToken = json.getString("access_token");
                      Log.e("user", serverUsername);
                      Log.e("token", serverToken);
                      adapter.open();
                      adapter.insert(new User(1, serverUsername, serverToken, MyPanel));
                      adapter.close();

                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }
}
