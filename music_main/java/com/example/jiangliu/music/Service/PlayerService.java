package com.example.jiangliu.music.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.jiangliu.music.Class.AppConstant;
import com.example.jiangliu.music.Class.Mp3Info;
import com.example.jiangliu.music.Util.MediaUtil;

import java.util.List;

/**
 * Created by jiangliu on 14-12-21.
 */
public class PlayerService extends Service {
    private MediaPlayer mediaPlayer;
    private String path;
    private int msg;
    private boolean isPause;
    private int current=0;
    private List<Mp3Info> mp3Infos;
    private int status=3;
    private MyReceiver myReceiver;
    private int currentTime;
    private int duration;
    private int index=0;

    public static final String UPDATE_ACTION="com.example.action.UPDATE_ACTION";
    public static final String CTL_ACTION="com.example.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==1){
                if(mediaPlayer!=null){
                    currentTime=mediaPlayer.getCurrentPosition();
                    Intent intent=new Intent();
                    intent.setAction(MUSIC_CURRENT);
                    intent.putExtra("currentTime",currentTime);
                    sendBroadcast(intent);
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };
    };

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("service","service created");
        mediaPlayer=new MediaPlayer();
        mp3Infos= MediaUtil.getMp3Infos(PlayerService.this);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(status==1){
                    mediaPlayer.start();
                }else if(status==2){
                    current++;
                    if(current>=mp3Infos.size()){
                        current=0;
                    }
                    Intent sendIntent=new Intent(UPDATE_ACTION);
                    sendIntent.putExtra("current",current);
                    sendBroadcast(sendIntent);
                    path=mp3Infos.get(current).getUrl();
                    play(0);
                }else if(status==3){
                    current++;
                    if(current<mp3Infos.size()){
                        Intent sendIntent=new Intent(UPDATE_ACTION);
                        sendIntent.putExtra("current",current);
                        sendBroadcast(sendIntent);
                        path=mp3Infos.get(current).getUrl();
                        play(0);
                    }else{
                        mediaPlayer.seekTo(0);
                        current=0;
                        Intent sendIntent=new Intent(UPDATE_ACTION);
                        sendIntent.putExtra("current",current);
                        sendBroadcast(sendIntent);
                    }
                }else if(status==4){
                    current=getRandomIndex(mp3Infos.size()-1);
                    Intent sendIntent=new Intent(UPDATE_ACTION);
                    sendIntent.putExtra("current",current);
                    path=mp3Infos.get(current).getUrl();
                    play(0);
                }
            }
        });
        myReceiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(CTL_ACTION);
        registerReceiver(myReceiver,filter);
    }

    protected int getRandomIndex(int end){
        int index=(int)(Math.random()*end);
        return index;
    }

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        path = intent.getStringExtra("url");
        current = intent.getIntExtra("listPosition", -1);
        msg = intent.getIntExtra("MSG", 0);
        Log.v("msg","start play"+msg);
        Log.v("path","path"+path);
        if (msg == AppConstant.PlayerMsg.PLAY_MSG) {
            play(0);
        } else if (msg == AppConstant.PlayerMsg.PAUSE_MSG) {
            pause();
        } else if (msg == AppConstant.PlayerMsg.STOP_MSG) {
            stop();
        } else if (msg == AppConstant.PlayerMsg.CONTINUE_MSG) {
            resume();
        } else if (msg == AppConstant.PlayerMsg.PRIVIOUS_MSG) {
            previous();
        } else if (msg == AppConstant.PlayerMsg.NEXT_MSG) {
            next();
        } else if (msg == AppConstant.PlayerMsg.PROGRESS_CHANGE) {
            currentTime = intent.getIntExtra("progress", -1);
            play(currentTime);
        } else if (msg == AppConstant.PlayerMsg.PLAYING_MSG) {
            handler.sendEmptyMessage(1);
        }
        return super.onStartCommand(intent,flags, startId);
    }

    private void play(int currentTime){
        Log.v("command","start play");
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));
            handler.sendEmptyMessage(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void pause(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause=true;
        }
    }

    private void resume(){
        if(isPause){
            mediaPlayer.start();
            isPause=false;
        }
    }

    private void previous(){
        Intent sendIntent=new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current",current);
        sendBroadcast(sendIntent);
        play(0);
    }

    private void next(){
        Intent sendIntent=new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current",current);
        sendBroadcast(sendIntent);
        play(0);
    }
    private void stop(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            try{
                mediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();
            if (currentTime > 0) {
                mediaPlayer.seekTo(currentTime);
            }
            Intent intent = new Intent();
            intent.setAction(MUSIC_DURATION);
            duration = mediaPlayer.getDuration();
            intent.putExtra("duration", duration);
            sendBroadcast(intent);
        }
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control) {
                case 1:
                    status = 1;
                    break;
                case 2:
                    status = 2;
                    break;
                case 3:
                    status = 3;
                    break;
                case 4:
                    status = 4;
                    break;
            }
        }
    }
}
