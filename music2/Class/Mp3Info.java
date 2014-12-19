package com.example.jiangliu.music.Class;

/**
 * Created by jiangliu on 14-12-12.
 */
public class Mp3Info {
    private  long id;
    private String name;
    private String artist;
    private long duration;
    private long size;

    public long getId(){
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
}
