package com.ekran.player.model;

public class Content {
     private int id;
     private String file_name;
     private String file_size;
     private int sync;
     private int deleted;
     private int group_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPanel_id() {
        return panel_id;
    }

    public void setPanel_id(int panel_id) {
        this.panel_id = panel_id;
    }

    public int getType_content() {
        return type_content;
    }

    public void setType_content(int type_content) {
        this.type_content = type_content;
    }

    private String end_date;
     private int user_id;
     private int panel_id;
     private int type_content;

    public Content(int id, String file_name, String file_size, int sync, int deleted, int group_id, String end_date, int user_id, int panel_id, int type_content) {
        this.id = id;
        this.file_name = file_name;
        this.file_size = file_size;
        this.sync = sync;
        this.deleted = deleted;
        this.group_id = group_id;
        this.end_date = end_date;
        this.user_id = user_id;
        this.panel_id = panel_id;
        this.type_content = type_content;
    }

    @Override
    public  String toString(){
        return id + " " + file_name;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
}
