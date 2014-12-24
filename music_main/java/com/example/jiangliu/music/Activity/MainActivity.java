package com.example.jiangliu.music.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiangliu.music.Adapter.MusicListAdapter;
import com.example.jiangliu.music.Class.AppConstant;
import com.example.jiangliu.music.Class.Mp3Info;
import com.example.jiangliu.music.R;
import com.example.jiangliu.music.Service.PlayerService;
import com.example.jiangliu.music.Util.MediaUtil;

import java.util.List;


public class MainActivity extends Activity {
    private ListView mMusiclist;
    private List<Mp3Info> mp3Infos=null;
    MusicListAdapter listAdapter;
    private Button playBtn;
    private Button shuffleBtn;
    private Button nextBtn;
    private TextView musicName;
    private TextView musicArtist;
    private TextView musicDuration;

    private int repeatState;
    private final int isCurentRepeat=1;
    private final int isAllRepeat=2;
    private final int isNoneRepeat=3;
    private boolean isFirstTime=true;
    private boolean isPlaying;
    private boolean isPause;
    private boolean isOrder=true;
    private boolean isShuffle=false;

    private int listPosition=0;//list position tag
    private HomeReceiver homeReceiver;//custom BroadcastReceiver

    public static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";
    public static final String CTL_ACTION = "com.example.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";
    public static final String REPEAT_ACTION = "com.example.action.REPEAT_ACTION";
    public static final String SHUFFLE_ACTION = "com.example.action.SHUFFLE_ACTION";
    private int currentTime;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mMusiclist=(ListView)findViewById(R.id.music_list);

        mp3Infos= MediaUtil.getMp3Infos(MainActivity.this);//get all songs
        listAdapter=new MusicListAdapter(this,mp3Infos);
        mMusiclist.setAdapter(listAdapter);
        mMusiclist.setOnItemClickListener(new MusicListItemClickListener());
        findViewById();//get all controls
        setViewOnclickListener();//set clickListener
        init();

        homeReceiver=new HomeReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        filter.addAction(REPEAT_ACTION);
        filter.addAction(SHUFFLE_ACTION);
        registerReceiver(homeReceiver, filter);
        Intent intent = new Intent(this, PlayerService.class);
        startService(intent);

    }
    /*private class MusicListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            if(mp3Infos!=null){
                Mp3Info mp3Info=mp3Infos.get(position);
                Intent intent=new Intent();
                intent.putExtra("url",mp3Info.getUrl());
                intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
                intent.setClass(MainActivity.this, PlayerService.class);
                startService(intent);
            }
        }
    }*/

    private void findViewById(){
        shuffleBtn=(Button)findViewById(R.id.shuffle_music);
        playBtn=(Button)findViewById(R.id.play_music);
        nextBtn=(Button)findViewById(R.id.next_music);
        musicName=(TextView)findViewById(R.id.music_name);
        musicArtist=(TextView)findViewById(R.id.music_artist);
        musicDuration=(TextView)findViewById(R.id.music_duration);
    }

    private void setViewOnclickListener(){
        ViewOnClickListener viewOnClickListener=new ViewOnClickListener();
        shuffleBtn.setOnClickListener(viewOnClickListener);
        playBtn.setOnClickListener(viewOnClickListener);
        nextBtn.setOnClickListener(viewOnClickListener);
    }

    private class ViewOnClickListener implements View.OnClickListener{
        Intent intent=new Intent();

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.play_music:
                    if(isFirstTime){
                        play();
                        isFirstTime=false;
                        isPlaying=true;
                        isPause=false;
                    }
                    else{
                        if(isPlaying){
                            playBtn.setBackgroundResource(R.drawable.playbutton);
                            intent.setAction("com.example.media.MUSIC_SERVICE");
                            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                            startService(intent);
                            isPlaying = false ;
                            isPause = true ;
                        }else if (isPause) {
                            playBtn.setBackgroundResource(R.drawable.pause);
                            intent.setAction("com.example.media.MUSIC_SERVICE");
                            intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);
                            startService(intent);
                            isPause = false;
                            isPlaying = true ;
                        }
                    }
                    break;
                case R.id.shuffle_music:
                    if(isOrder){
                        shuffleBtn.setBackgroundResource(R.drawable.random);
                        Toast.makeText(MainActivity.this,R.string.shuffle,Toast.LENGTH_SHORT).show();
                        isOrder=false;
                        isShuffle=true;
                        shuffleMusic();
                    }
                    else if(isShuffle){
                        shuffleBtn.setBackgroundResource(R.drawable.sequent);
                        Toast.makeText(MainActivity.this,R.string.order,Toast.LENGTH_SHORT).show();
                        isShuffle=false;
                        isOrder=true;
                    }
                    break;
                case R.id.next_music:
                    isFirstTime=false;
                    isPlaying=true;
                    isPause=false;
                    next();
                    break;
            }
        }
    }

    private class MusicListItemClickListener implements OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Log.v("state","position"+position);
            listPosition=position;
            playMusic(listPosition);
        }
    }
    public void init(){
        Mp3Info mp3Info=mp3Infos.get(0);
        musicName.setText(mp3Info.getName());
        musicDuration.setText(mp3Info.getArtist());

    }
    public void next(){
        listPosition=listPosition+1;
        if(listPosition>=mp3Infos.size()){
            listPosition=0;
        }
        Mp3Info mp3Info=mp3Infos.get(listPosition);
        musicName.setText(mp3Info.getName());
        musicDuration.setText(mp3Info.getArtist());
        Intent intent=new Intent();
        intent.setAction("com.example.media.MUSIC_SERVICE");
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);
        startService(intent);
    }
    public void play(){
        playBtn.setBackgroundResource(R.drawable.pause);
        Mp3Info mp3Info=mp3Infos.get(listPosition);
        musicName.setText(mp3Info.getName());
        musicDuration.setText(mp3Info.getArtist());
        Intent intent=new Intent();
        intent.setAction("com.example.media.MUSIC_SERVICE");
        intent.putExtra("listPosition", 0);
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
        startService(intent);
    }
    public void shuffleMusic(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 4);
        sendBroadcast(intent);
    }
    public void playMusic(int listPosition){
        if(mp3Infos!=null){
            Mp3Info mp3Info=mp3Infos.get(listPosition);
            musicName.setText(mp3Info.getName());
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            intent.putExtra("Name", mp3Info.getName());
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("artist", mp3Info.getArtist());
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("currentTime", currentTime);
            intent.putExtra("repeatState", repeatState);
            intent.putExtra("shuffleState", isShuffle);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("退出")
                    .setMessage("您确定要退出？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                    Intent intent = new Intent(
                                            MainActivity.this,
                                            PlayerService.class);
                                    unregisterReceiver(homeReceiver);
                                    stopService(intent); // 停止后台服务
                                }
                            }).show();

        }
        return super.onKeyDown(keyCode, event);
    }


    public class HomeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MUSIC_CURRENT)){
                //currentTime代表当前播放的时间
                currentTime = intent.getIntExtra("currentTime", -1);
                musicDuration.setText(MediaUtil.formatTime(currentTime));
            } else if (action.equals(MUSIC_DURATION)) {
                duration = intent.getIntExtra("duration", -1);
            }
            else if(action.equals(UPDATE_ACTION)) {
                //获取Intent中的current消息，current代表当前正在播放的歌曲
                listPosition = intent.getIntExtra("current", -1);
                if(listPosition >= 0) {
                    musicName.setText(mp3Infos.get(listPosition).getName());
                }
            }else if(action.equals(SHUFFLE_ACTION)) {
                isShuffle = intent.getBooleanExtra("shuffleState", false);
                if(isShuffle) {
                    isOrder = false;
                    shuffleBtn.setBackgroundResource(R.drawable.random);
                } else {
                    isOrder = true;
                    shuffleBtn.setBackgroundResource(R.drawable.sequent);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
