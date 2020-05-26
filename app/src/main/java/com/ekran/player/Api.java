package com.ekran.player;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ekran.player.model.Content;
import com.ekran.player.model.User;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

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
                      long x = adapter.insert(user);
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

  public void GetListToUpload() {
      adapter.open();
      List<User> users = adapter.getUsers();
      String token = users.get(1).getToken();
      long id = users.get(1).getId();
      adapter.close();

      HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
              "/content/toUpload?PanelId=" + id).newBuilder();
      Request requestContent = new Request.Builder()
              .url(httpBuilderCreatePanel.build())
              .header("Authorization", "Bearer " + token)
              .build();
      httpClient.newCall(requestContent).enqueue(new Callback() {
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
                      Gson gson = new Gson();
                      List<Content> list = gson.fromJson(mMessage, new TypeToken<List<Content>>() {}.getType());
                      adapter.open();
                      adapter.delAllContent();
                      for (Content c : list) {
                          Log.e("contet:", c.toString());
                          adapter.insertContent(c);
                      }
                      Log.e("count: ", String.valueOf(adapter.getCountContent()));
                      adapter.close();
                      //Log.e("FileName: ", fileName);
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }
}
