package com.example.jiangliu.music.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jiangliu.music.Class.Mp3Info;
import com.example.jiangliu.music.R;
import com.example.jiangliu.music.Util.MediaUtil;

import java.util.List;

/**
 * Created by jiangliu on 14-12-12.
 */
public class MusicListAdapter extends BaseAdapter {
    private Context context;
    private List<Mp3Info> mp3Infos;
    private Mp3Info mp3Info;
    private int pos=-1;

    public  MusicListAdapter(Context context,List<Mp3Info> mp3Infos){
        this.context=context;
        this.mp3Infos=mp3Infos;
    }
    @Override
    public int getCount(){
        return  mp3Infos.size();
    }
    @Override
    public Object getItem(int position){
        return position;
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder=null;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.music_list,null);
            //viewHolder.albumImage=(ImageView)convertView.findViewById(R.id.albumImage);
            viewHolder.musicName=(TextView)convertView.findViewById(R.id.music_name);
            viewHolder.musicArtist=(TextView)convertView.findViewById(R.id.music_artist);
            viewHolder.musicDuration=(TextView)convertView.findViewById(R.id.music_duration);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        mp3Info=mp3Infos.get(position);
        if(position==pos){
            //viewHolder.albumImage.setImageResource(R.drawable.item);
        }
        viewHolder.musicName.setText(mp3Info.getName());
        viewHolder.musicArtist.setText(mp3Info.getArtist());
        viewHolder.musicDuration.setText(MediaUtil.formatTime(mp3Info.getDuration()));
        return convertView;
    }
    public class ViewHolder{
        public ImageView albumImage;
        public TextView musicName;
        public TextView musicDuration;
        public TextView musicArtist;
    }
}
