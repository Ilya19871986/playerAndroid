package com.ekran.player;


import android.util.Log;

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

public class Api {
  private final String apiAddr = "http://193.124.58.144:4444";
    OkHttpClient httpClient = new OkHttpClient();
    String serverUsername = null;
    String serverToken = null;

  public void AuthCreatePanel(String username, String password) {

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
                      Log.e("user", serverUsername.toString());
                      Log.e("token", serverToken.toString());
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }
}
