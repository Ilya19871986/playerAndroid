package com.ekran.player.model;

public class User {
    private long id;
    private String username;
    private String token;
    private String panelName;


    public User(long id, String username, String token, String panelName) {
        this.id = id;
        this.username = username;
        this.token = token;
        this.panelName = panelName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }
}
