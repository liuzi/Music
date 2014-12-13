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

    public Mp3Info(){
        super();
    }

    public Mp3Info(long id,String name,String artist,long duration,long size){
        super();
        this.id=id;
        this.name=name;
        this.artist=artist;
        this.duration=duration;
        this.size=size;
    }

    @Override
    public  String toString(){
        return "Mp3Info [id=" + id + ", name=" + name +  ", artist=" + artist
                + ", duration=" + duration + ", size=" + size  + "]";
    }

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
