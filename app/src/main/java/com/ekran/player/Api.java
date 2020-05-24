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
    String password = null;
    String panelId = null;

  public  void RefreshToken(String username, final String password, final String panelName) {
      RequestBody formBody = new FormBody.Builder().add("coupon", "").build();
      HttpUrl.Builder httpBuilder = HttpUrl.parse(apiAddr + "/token?username=" + username + "&password=" + password).newBuilder();
      Request request = new Request.Builder().url(httpBuilder.build()).post(formBody).build();
      httpClient.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
              String mMessage = e.getMessage();
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

                      Log.e("Обновление токена", serverToken);
                      adapter.open();
                      adapter.update(new User(1, serverUsername, password, serverToken, panelName));
                      adapter.close();
                      Log.e("-", "Обновление токена завершено");

                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }

  public void createPanel(String username, final String panelName, String token) {
      HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
              "/panel/createPanel?panelName=" + panelName + "&username=" + username).newBuilder();
      Request requestCreatePanel = new Request.Builder().url(httpBuilderCreatePanel.build())
              .header("Authorization", "Bearer " + token)
              .build();

      httpClient.newCall(requestCreatePanel).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
              String mMessage = e.getMessage();
              Log.e("failure Response", mMessage);
              call.cancel();
          }
          @Override
          public void onResponse(Call call, Response response) throws IOException {
              String mMessage = response.body().string();
              if (response.isSuccessful()){
                  try {
                      JSONObject json = new JSONObject(mMessage);
                      panelId = json.getString("id");
                      adapter.open();
                      User user = adapter.getUser(1);
                      user.setId(Long.parseLong(panelId));
                      adapter.update(user);
                      adapter.close();
                      Log.e("id ", panelId);

                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }

  public void AuthCreatePanel(String username, final String password, final String panelName) {

      RequestBody formBody = new FormBody.Builder().add("coupon", "").build();
      HttpUrl.Builder httpBuilder = HttpUrl.parse(apiAddr + "/token?username=" + username + "&password=" + password).newBuilder();
      Request request = new Request.Builder().url(httpBuilder.build()).post(formBody).build();

      httpClient.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
              String mMessage = e.getMessage();
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
                      adapter.insert(new User(1, serverUsername, password, serverToken, panelName));
                      adapter.close();
                      createPanel(serverUsername, panelName, serverToken);
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }
}
