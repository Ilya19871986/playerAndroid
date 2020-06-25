package com.ekran.player.model;

public class Version {
    private long id;
    private String version;
    private String orientation; // 0 - горизонтальная, 1 - вертикальная
    private String imgTime;

    public Version(long id, String version, String orientation, String imgTime) {
        this.version = version;
        this.orientation = orientation;
        this.imgTime = imgTime;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getVersion() {
        return this.version;
    }
    public String getImgTime() { return this.imgTime; }
    public String getOrientation() { return this.orientation; }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
