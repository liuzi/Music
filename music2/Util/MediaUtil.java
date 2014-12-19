package com.example.jiangliu.music.Util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.jiangliu.music.Class.Mp3Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jiangliu on 14-12-12.
 */
public class MediaUtil {
    public static List<Mp3Info> getMp3Infos(Context context){
        Cursor cursor=context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        List<Mp3Info> mp3Infos=new ArrayList<Mp3Info>();
        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToNext();
            Mp3Info mp3Info = new Mp3Info();
            Long id=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            int ismusic=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if(ismusic!=0){
                mp3Info.setId(id);
                mp3Info.setName(name);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
            }
        }
        return  mp3Infos;
    }

    /*
    public static List<HashMap<String,String>> getMusicMap(List<Mp3Info> mp3Infos){
        List<HashMap<String,String>>mp3list=new ArrayList<HashMap<String, String>>();
        for(Iterator iterator=mp3Infos.iterator();iterator.hasNext();){
            Mp3Info mp3Info=(Mp3Info) iterator.next();
            HashMap<String, String> map=new HashMap<String, String>();
            map.put("name",mp3Info.getName());
            map.put("artist",mp3Info.getArtist());
            map.put("duration", formatTime(mp3Info.getDuration()));
            map.put("size",String.valueOf(mp3Info.getSize()));
            mp3list.add(map);
        }
        return mp3list;
    }
    */

    public static String formatTime(long time){
        String min=time/(1000*60)+"";
        String sec=time%(1000*60)/1000+"";
        if(min.length()<2){
            min="0"+min;
        }
        return min+":"+sec.trim();
    }
}
