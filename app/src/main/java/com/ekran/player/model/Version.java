package com.ekran.player.model;

public class Version {
    private long id;
    private String version;
    private String orientation; // 0 - горизонтальная, 1 - вертикальная

    public Version(long id, String version, String orientation) {
        this.version = version;
        this.orientation = orientation;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getVersion() {
        return this.version;
    }

    public String getOrientation() {
        return this.orientation;
    }

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
