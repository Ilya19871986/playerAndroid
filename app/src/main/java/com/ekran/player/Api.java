package com.ekran.player;


import android.content.SharedPreferences;
import android.util.Log;

import com.ekran.player.model.Content;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import static com.ekran.player.ContentView.userPassword;
import static com.ekran.player.MainActivity.sharedPrefs;
import static com.ekran.player.ContentView.userName;
import static com.ekran.player.ContentView.bearerToken;
import static com.ekran.player.ContentView.panelId;
import static com.ekran.player.ContentView.orientation;
import static com.ekran.player.ContentView.imgTime;
import static com.ekran.player.ContentView.panelName;

public class Api {
  private final String apiAddr = "http://193.124.58.144:4444";

    OkHttpClient httpClient = new OkHttpClient();
    String serverUsername = null;
    String serverToken = null;

    public static boolean reload = false;

    public Api() {

    }

    FtpLoader ftpLoader = new FtpLoader();

  public void GetNewPassword() {
      HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
              "/api/GetNewPassword?userName=" + userName).newBuilder();
      Request requestCreatePanel = new Request.Builder().url(httpBuilderCreatePanel.build())
              .header("Authorization", "Bearer " + bearerToken)
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
              if (response.isSuccessful()) {
                  try {
                      JSONObject json = new JSONObject(mMessage);
                      String newPass = json.getString("password");
                      Log.e("newPass ", newPass);
                      SharedPreferences.Editor editor = sharedPrefs.edit();
                      editor.putString("password", newPass);
                      userPassword = newPass;
                      editor.apply();
                      Log.e("update user password ", newPass);

                  } catch (Exception e) {
                      e.printStackTrace();

                  }
              }
          }
      });
  }

  public void createPanel(String username, final String panelName) {

      Log.e("create panel", panelName);

      HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
              "/panel/createPanel?panelName=" + panelName + "&username=" + username).newBuilder();
      Request requestCreatePanel = new Request.Builder().url(httpBuilderCreatePanel.build())
              .header("Authorization", "Bearer " + bearerToken)
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

                      SharedPreferences .Editor editor = sharedPrefs.edit();
                      editor.putString("panelId", panelId);
                      editor.apply();

                      Log.e("panel_id ", panelId);

                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }

  public void AuthCreatePanel(String username, final String password, final String _panelName) {

      RequestBody formBody = new FormBody.Builder().add("coupon", "").build();
      HttpUrl.Builder httpBuilder = HttpUrl.parse(apiAddr + "/token?username=" + username + "&password=" + password).newBuilder();
      Request request = new Request.Builder().url(httpBuilder.build()).post(formBody).build();

      Log.e("user", username);
      Log.e("password", password);
      Log.e("_panelName", _panelName);

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
                      bearerToken = serverToken;
                      SharedPreferences .Editor editor = sharedPrefs.edit();
                      editor.putString("username", serverUsername);
                      editor.putString("token", serverToken);
                      editor.apply();

                      Log.e("token", bearerToken);
                      createPanel(serverUsername, _panelName);
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }

  public void GetListToUpload() {

      HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
              "/content/toUpload?PanelId=" + panelId).newBuilder();
      Request requestContent = new Request.Builder()
              .url(httpBuilderCreatePanel.build())
              .header("Authorization", "Bearer " + bearerToken)
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
                      if (list.size() > 0)
                        ftpLoader.uploadFileV2(panelName, "Видео", list);
                      setConnectTime(Long.parseLong(panelId));
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }
  // установка sync = 1
  public void setUploadedFile(int id) {
      HttpUrl.Builder httpBuilder = HttpUrl.parse(apiAddr +
              "/panel/endUploadingFile?id=" + id).newBuilder();
      Request requestContent = new Request.Builder()
              .url(httpBuilder.build())
              .header("Authorization", "Bearer " + bearerToken)
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
                      Log.e("upload", "ok");
                  } catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      });
  }

    public void checkVersion() {

      if (panelId != null && panelId != "0" && !panelId.isEmpty()) {
          HttpUrl.Builder httpBuilder = HttpUrl.parse(apiAddr +
                  "/panel/checkNewVersion?PanelId=" + panelId).newBuilder();
          Request requestContent = new Request.Builder()
                  .url(httpBuilder.build())
                  .header("Authorization", "Bearer " + bearerToken)
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
                          JSONObject json = new JSONObject(mMessage);
                          String newVersion = json.getString("player_version");

                          String  newOrientation = json.getString("only_vip");
                          String timeImg = json.getString("time_vip");
                          timeImg = timeImg.equals("0") ? "5" : timeImg;

                          if (checkChangeSettings(newVersion, newOrientation, timeImg)) {
                              SharedPreferences.Editor editor = sharedPrefs.edit();
                              editor.putString("version", newVersion);
                              editor.putString("orientation", newOrientation);
                              editor.putString("imgTime", timeImg);
                              editor.apply();
                              orientation = newOrientation;
                              imgTime = timeImg;
                              Log.e("version", newVersion);
                              Log.e("orientation", orientation);
                              Log.e("imgTime", timeImg);
                              reload = true;
                          }
                      } catch (Exception e){
                          e.printStackTrace();
                      }
                  }
              }
          });
      }
    }

    private boolean checkChangeSettings(String ver, String _orientation, String timeImg) {

      if (!orientation.equals(_orientation) || !imgTime.equals(timeImg)) {
          return true;
      }
      else return false;
    }

  public void GetListToDelete() {

        HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
                "/content/GetDeletedFile?id=" + panelId).newBuilder();
        Request requestContent = new Request.Builder()
                .url(httpBuilderCreatePanel.build())
                .header("Authorization", "Bearer " + bearerToken)
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
                .header("Authorization", "Bearer " + bearerToken)
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

        HttpUrl.Builder httpBuilderCreatePanel = HttpUrl.parse(apiAddr +
                "/panel/deleteFile?id=" + id).newBuilder();
        Request requestContent = new Request.Builder()
                .url(httpBuilderCreatePanel.build())
                .header("Authorization", "Bearer " + bearerToken)
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
