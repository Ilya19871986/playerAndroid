package com.ekran.player.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthData {
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("username")
    @Expose
    private String username;

    public String getAccessToken() {
        return accessToken;
    }

    public String getUsername() {
        return username;
    }

}
