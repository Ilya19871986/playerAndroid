package com.ekran.player;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ekran.player.model.Content;
import com.ekran.player.model.User;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
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

    public static String BearerToken = null;

    String panel = null;

    public Api() {
    }

    FtpLoader ftpLoader = new FtpLoader();

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
                      BearerToken = serverToken;
                      adapter.update(new User(1, serverUsername, password, serverToken, panelName));
                      //Log.e("-", "Обновление токена завершено");
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
              .header("Authorization", "Bearer " + BearerToken)
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
                      User user = adapter.getUser(1);
                      user.setId(Long.parseLong(panelId));
                      long x = adapter.insert(user);
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
                      BearerToken = serverToken;
                      adapter.insert(new User(1, serverUsername, password, serverToken, panelName));
                      createPanel(serverUsername, panelName, serverToken);
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }

  public void GetListToUpload() {
      List<User> users = adapter.getUsers();
      String token = users.get(1).getToken();
      final long id = users.get(1).getId();

      HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
              "/content/toUpload?PanelId=" + id).newBuilder();
      Request requestContent = new Request.Builder()
              .url(httpBuilderCreatePanel.build())
              .header("Authorization", "Bearer " + BearerToken)
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
                      //adapter.delAllContent();
                      for (Content c : list) {
                          ftpLoader.uploadFile(getPanelName(), "Видео", c.getFile_name(), c.getId());
                          adapter.insertContent(c);
                      }
                      //adapter.PrintContent();
                      setConnectTime(id);
                      Log.e("Количество файлов: ", String.valueOf(adapter.getCountContent()));
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }
  // установка sync = 1
  public void setUploadedFile(int id) {
      List<User> users = adapter.getUsers();
      String token = users.get(1).getToken();

      HttpUrl.Builder httpBuilder = HttpUrl.parse(apiAddr +
              "/panel/endUploadingFile?id=" + id).newBuilder();
      Request requestContent = new Request.Builder()
              .url(httpBuilder.build())
              .header("Authorization", "Bearer " + BearerToken)
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
                      Log.e("upload:", "ok");
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }

  public String getPanelName() {
      List<User> users = adapter.getUsers();
      return users.get(1).getPanelName();
  }

  public void GetListToDelete() {
        List<User> users = adapter.getUsers();
        String token = users.get(1).getToken();
        long id = users.get(1).getId();

        HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
                "/content/GetDeletedFile?id=" + id).newBuilder();
        Request requestContent = new Request.Builder()
                .url(httpBuilderCreatePanel.build())
                .header("Authorization", "Bearer " + BearerToken)
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
                      for (Content c : list) {
                          DeleteFile(c.getId());
                          File file = new File("/data/data/com.ekran.player/files/" + c.getFile_name());
                          file.delete();
                          adapter.delContent(c.getId());
                          Log.e("Удален файл:", c.getFile_name());
                      }
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
    }

    public void setConnectTime(long id) {
        HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
                "/panel/connect?id=" + id).newBuilder();
        Request requestContent = new Request.Builder()
                .url(httpBuilderCreatePanel.build())
                .header("Authorization", "Bearer " + BearerToken)
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
                        Log.e("connectTime", "ok");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void DeleteFile(long id) {
        List<User> users = adapter.getUsers();
        String token = users.get(1).getToken();

        HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
                "/panel/deleteFile?id=" + id).newBuilder();
        Request requestContent = new Request.Builder()
                .url(httpBuilderCreatePanel.build())
                .header("Authorization", "Bearer " + BearerToken)
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

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
