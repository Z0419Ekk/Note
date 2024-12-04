package com.example.note.bean;

import java.io.Serializable;

public class Note implements Serializable {

    private String title;
    private String content;
    private String createdTime;
    private String id;
    private int weather;
    //0晴天 1雨天  2阴天  3多云

    private byte[] picture;
    public Note() {
    }

    public Note(String title, String content, String createdTime, String id, int weather, byte[] picture) {
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.id = id;
        this.weather = weather;
        this.picture = picture;
    }

    public Note(String title, String content, String createdTime, int weather, byte[] picture) {
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.weather = weather;
        this.picture = picture;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", id='" + id + '\'' +
                ", weather=" + weather +
                '}';
    }
}
