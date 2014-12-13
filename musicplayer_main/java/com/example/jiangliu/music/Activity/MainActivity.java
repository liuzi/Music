package com.example.jiangliu.music.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jiangliu.music.Adapter.MusicListAdapter;
import com.example.jiangliu.music.Class.Mp3Info;
import com.example.jiangliu.music.R;
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
    private boolean isNoneShuffle=true;
    private boolean isShuffle=false;

    private int listposition=0;//list position tag
    private HomeReceiver homeReceiver;//custom BroadcastReceiver
    private int currentTime;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mMusiclist=(ListView)findViewById(R.id.music_list);
        mMusiclist.setOnClickListener(new MusicListItemClickListener());
        mp3Infos= MediaUtil.getMp3Infos(MainActivity.this);//get all songs
        listAdapter=new MusicListAdapter(this,mp3Infos);
        mMusiclist.setAdapter(listAdapter);
        findViewById();//get all controls
        setViewOnclickListener();//set clickListener
        repeatState=isNoneRepeat;

        homeReceiver=new HomeReceiver();
    }

    private void findViewById(){
        shuffleBtn=(Button)findViewById(R.id.shuffle_music);
        playBtn=(Button)findViewById(R.id.play_music);
        nextBtn=(Button)findViewById(R.id.next_music);
        musicName=(TextView)findViewById(R.id.music_name);
        musicArtist=(TextView)findViewById(R.id.music_artist);
        musicDuration=(Button)findViewById(R.id.music_duration);
    }

    private void setViewOnclickListener(){
        ViewOnClickListener viewOnClickListener=new ViewOnClickListener();
    }

    private class ViewOnClickListener implements View.OnClickListener{

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
    public class HomeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){
            String action=intent.getAction();
        }
    }
}
