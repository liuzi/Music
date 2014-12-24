package com.example.jiangliu.music.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.example.jiangliu.music.R;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiangliu.music.Class.AppConstant;
import com.example.jiangliu.music.Class.LrcView;
import com.example.jiangliu.music.Class.Mp3Info;
import com.example.jiangliu.music.Service.PlayerService;
import com.example.jiangliu.music.Util.MediaUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jiangliu on 14-12-22.
 */
public class PlayerActivity extends Activity {
    private TextView musicName = null;
    private TextView musicArtist = null;
    private Button previousBtn;
    //private Button repeatBtn;
    private Button playBtn;
    private Button shuffleBtn;
    private Button nextBtn;
    private Button queueBtn;
    private SeekBar music_progressBar;// 歌曲进度
    private TextView currentProgress;// 当前进度消耗的时间
    private TextView finalProgress;// 歌曲时间

    private String name;
    private String artist;
    private String url;
    private int listPosition;
    private int currentTime;
    private int duration;
    private int flag;// 播放标识

    private int repeatState;
    private final int isCurrentRepeat = 1;// 单曲循环
    private final int isAllRepeat = 2;// 全部循环
    private final int isNoneRepeat = 3;// 无重复播放
    private boolean isPlaying;
    private boolean isPause;
    private boolean isNoneShuffle;
    private boolean isShuffle;

    private List<Mp3Info> mp3Infos;
    public static LrcView lrcView;

    private PlayerReceiver playerReceiver;
    public static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";
    public static final String CTL_ACTION = "com.example.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";
    public static final String MUSIC_PLAYING = "com.example.action.MUSIC_PLAYING";
    public static final String REPEAT_ACTION = "com.example.action.REPEAT_ACTION";
    public static final String SHUFFLE_ACTION = "com.example.action.SHUFFLE_ACTION";
    public static final String SHOW_LRC = "com.example.action.SHOW_LRC";

    private ImageView musicAlbum;//音乐专辑封面
    private ImageView musicAblumReflection;//倒影反射

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity_layout);

        findViewById();
        setViewOnclickListener();
        getDataFromBundle();

        mp3Infos = MediaUtil.getMp3Infos(PlayerActivity.this);
        registerReceiver();

//        showVoicePanelAnimation = AnimationUtils.loadAnimation(PlayerActivity.this, R.anim.push_up_in);
//        hiddenVoicePanelAnimation = AnimationUtils.loadAnimation(PlayerActivity.this, R.anim.push_up_out);

//        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        sb_player_voice.setProgress(currentVolume);
        initView();
//        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
    }

    private void registerReceiver() {
        //定义和注册广播接收器
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        registerReceiver(playerReceiver, filter);
    }

    /**
     * 从界面上根据id获取按钮
     */
    private void findViewById() {
        musicName = (TextView) findViewById(R.id.music_name);
        musicArtist = (TextView) findViewById(R.id.musicArtist);
        previousBtn = (Button) findViewById(R.id.previous_music);
        playBtn = (Button) findViewById(R.id.play_music);
        shuffleBtn = (Button) findViewById(R.id.shuffle_music);
        nextBtn = (Button) findViewById(R.id.next_music);
        queueBtn = (Button) findViewById(R.id.play_queue);
        music_progressBar = (SeekBar) findViewById(R.id.audioTrack);
        currentProgress = (TextView) findViewById(R.id.current_progress);
        finalProgress = (TextView) findViewById(R.id.final_progress);
        lrcView = (LrcView) findViewById(R.id.lrcShowView);
//        ibtn_player_voice = (ImageButton) findViewById(R.id.ibtn_player_voice);
//        ll_player_voice = (RelativeLayout) findViewById(R.id.ll_player_voice);
//        sb_player_voice = (SeekBar) findViewById(R.id.sb_player_voice);
        musicAlbum = (ImageView) findViewById(R.id.iv_music_ablum);
//        musicAblumReflection = (ImageView) findViewById(R.id.iv_music_ablum_reflection);
    }

    /**
     * 给每一个按钮设置监听器
     */
    private void setViewOnclickListener() {
        ViewOnclickListener ViewOnClickListener = new ViewOnclickListener();
        previousBtn.setOnClickListener(ViewOnClickListener);
        playBtn.setOnClickListener(ViewOnClickListener);
        shuffleBtn.setOnClickListener(ViewOnClickListener);
        nextBtn.setOnClickListener(ViewOnClickListener);
        queueBtn.setOnClickListener(ViewOnClickListener);
        music_progressBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
//        ibtn_player_voice.setOnClickListener(ViewOnClickListener);
//        sb_player_voice.setOnSeekBarChangeListener(new SeekBarChangeListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("PlayerActivity has started");
    }

    /**
     * 从Bundle中获取来自MainActivity中传过来的数据
     */
    private void getDataFromBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name = bundle.getString("name");
        artist = bundle.getString("artist");
        url = bundle.getString("url");
        listPosition = bundle.getInt("listPosition");
        repeatState = bundle.getInt("repeatState");
        isShuffle = bundle.getBoolean("shuffleState");
        flag = bundle.getInt("MSG");
        currentTime = bundle.getInt("currentTime");
        duration = bundle.getInt("duration");
    }
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("PlayerActivity has paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        System.out.println("PlayerActivity has onResume");
    }

    /**
     * 初始化界面
     */
    public void initView() {
        isPlaying = true;
        isPause = false;
        musicName.setText(name);
        musicArtist.setText(artist);
        music_progressBar.setProgress(currentTime);
        music_progressBar.setMax(duration);
//        sb_player_voice.setMax(maxVolume);
        Mp3Info mp3Info = mp3Infos.get(listPosition);
        //showArtwork(mp3Info);
        if (isShuffle) {
            isNoneShuffle = false;
            shuffleBtn.setBackgroundResource(R.drawable.random);
        } else {
            isNoneShuffle = true;
            shuffleBtn.setBackgroundResource(R.drawable.sequent);
        }
        if (flag == AppConstant.PlayerMsg.PLAYING_MSG) {// 如果播放信息是正在播放
//            Toast.makeText(PlayerActivity.this, "正在播放--" + name, 1).show();
            Intent intent = new Intent();
            intent.setAction(SHOW_LRC);
            intent.putExtra("listPosition", listPosition);
            sendBroadcast(intent);
        } else if (flag == AppConstant.PlayerMsg.PLAY_MSG) {// 如果是点击列表播放歌曲的话
            playBtn.setBackgroundResource(R.drawable.playbutton);
            play();
        } else if (flag == AppConstant.PlayerMsg.CONTINUE_MSG) {
            Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
            playBtn.setBackgroundResource(R.drawable.playbutton);
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);	//继续播放音乐
            startService(intent);
        }
    }

    /**
     * 显示专辑封面
     */
//    private void showArtwork(Mp3Info mp3Info) {
//        Bitmap bm = MediaUtil.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
//        //切换播放时候专辑图片出现透明效果
//        Animation albumanim = AnimationUtils.loadAnimation(PlayerActivity.this, R.anim.album_replace);
//        //开始播放动画效果
//        musicAlbum.startAnimation(albumanim);
//        if(bm != null) {
//            musicAlbum.setImageBitmap(bm);	//显示专辑封面图片
//            musicAblumReflection.setImageBitmap(ImageUtil.createReflectionBitmapForSingle(bm));	//显示倒影
//        } else {
//            bm = MediaUtil.getDefaultArtwork(this, false);
//            musicAlbum.setImageBitmap(bm);		//显示专辑封面图片
//            musicAblumReflection.setImageBitmap(ImageUtil.createReflectionBitmapForSingle(bm));	//显示倒影
//        }
//
//    }

    /**
     * 反注册广播
     */

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(playerReceiver);
        System.out.println("PlayerActivity has stoped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("PlayerActivity has Destoryed");
    }

    /**
     * 控件点击事件
     *
     */
    private class ViewOnclickListener implements View.OnClickListener {
        Intent intent = new Intent();

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_music:
                    if (isPlaying) {
                        playBtn.setBackgroundResource(R.drawable.playbutton);
                        intent.setAction("com.example.media.MUSIC_SERVICE");
                        intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                        startService(intent);
                        isPlaying = false;
                        isPause = true;
                    } else if (isPause) {
                        playBtn.setBackgroundResource(R.drawable.pause);
                        intent.setAction("com.example.media.MUSIC_SERVICE");
                        intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);
                        startService(intent);
                        isPause = false;
                        isPlaying = true;
                    }
                    break;
                case R.id.previous_music:
                    previous_music();
                    break;
                case R.id.next_music:
                    next_music();
                    break;
                case R.id.shuffle_music:
                    Intent shuffleIntent = new Intent(SHUFFLE_ACTION);
                    if (isNoneShuffle) {
                        shuffleBtn.setBackgroundResource(R.drawable.random);
                        Toast.makeText(PlayerActivity.this, R.string.shuffle,
                                Toast.LENGTH_SHORT).show();
                        isNoneShuffle = false;
                        isShuffle = true;
                        shuffleMusic();
                        shuffleIntent.putExtra("shuffleState", true);
                        sendBroadcast(shuffleIntent);
                    } else if (isShuffle) {
                        shuffleBtn
                                .setBackgroundResource(R.drawable.sequent);
                        Toast.makeText(PlayerActivity.this, R.string.order,
                                Toast.LENGTH_SHORT).show();
                        isShuffle = false;
                        isNoneShuffle = true;
                        shuffleIntent.putExtra("shuffleState", false);
                        sendBroadcast(shuffleIntent);
                    }
                    break;
                case R.id.play_queue:
                    showPlayQueue();
                    break;
            }
        }
    }

    /**
     * 实现监听Seekbar的类
     *
     */
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            switch(seekBar.getId()) {
                case R.id.audioTrack:
                    if (fromUser) {
                        audioTrackChange(progress); // 用户控制进度的改变
                    }
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }

    public void play() {
        // 开始播放的时候为顺序播放
        Intent intent = new Intent();
        intent.setAction("com.wwj.media.MUSIC_SERVICE");
        intent.putExtra("url", url);
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("MSG", flag);
        startService(intent);
    }

    /**
     * 显示播放列表
     */
    public void showPlayQueue() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View playQueueLayout = layoutInflater.inflate(R.layout.play_queue_layout, (ViewGroup)findViewById(R.id.play_queue_layout));
        ListView queuelist = (ListView) playQueueLayout.findViewById(R.id.lv_play_queue);

        List<HashMap<String, String>> queues = MediaUtil.getMusicMaps(mp3Infos);
        SimpleAdapter adapter = new SimpleAdapter(this, queues, R.layout.play_queue_item_layout, new String[]{"title",
                "Artist", "duration"}, new int[]{R.id.music_name, R.id.music_artist, R.id.music_duration});
        queuelist.setAdapter(adapter);
        AlertDialog.Builder builder;
        final AlertDialog dialog;
        builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.setView(playQueueLayout);
        dialog.show();
    }

    /**
     * 随机播放
     */
    public void shuffleMusic() {
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 4);
        sendBroadcast(intent);
    }

    /**
     * 播放进度改变
     * @param progress
     */
    public void audioTrackChange(int progress) {
        Intent intent = new Intent();
        intent.setAction("com.wwj.media.MUSIC_SERVICE");
        intent.putExtra("url", url);
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("MSG", AppConstant.PlayerMsg.PROGRESS_CHANGE);
        intent.putExtra("progress", progress);
        startService(intent);
    }

    /**
     * 顺序播放
     */
    public void repeat_none() {
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 3);
        sendBroadcast(intent);
    }

    /**
     * 上一首
     */
    public void previous_music() {
        playBtn.setBackgroundResource(R.drawable.playbutton);
        listPosition = listPosition - 1;
        if (listPosition >= 0) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            //showArtwork(mp3Info);
            musicName.setText(mp3Info.getName());
            musicArtist.setText(mp3Info.getArtist());
            url = mp3Info.getUrl();
            Intent intent = new Intent();
            intent.setAction("com.example.media.MUSIC_SERVICE");
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PRIVIOUS_MSG);
            startService(intent);
        } else {
            listPosition = 0;
            Toast.makeText(PlayerActivity.this, "start from the first song", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /**
     * 下一首
     */
    public void next_music() {
        playBtn.setBackgroundResource(R.drawable.playbutton);
        listPosition = listPosition + 1;
        if (listPosition <= mp3Infos.size() - 1) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            //showArtwork(mp3Info);
            url = mp3Info.getUrl();
            musicName.setText(mp3Info.getName());
            musicArtist.setText(mp3Info.getArtist());
            Intent intent = new Intent();
            intent.setAction("com.example.media.MUSIC_SERVICE");
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);
            startService(intent);

        } else {
            listPosition = mp3Infos.size() - 1;
            Toast.makeText(PlayerActivity.this, "next song", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 用来接收从service传回来的广播的内部类
     *
     */
    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MUSIC_CURRENT)) {
                currentTime = intent.getIntExtra("currentTime", -1);
                currentProgress.setText(MediaUtil.formatTime(currentTime));
                music_progressBar.setProgress(currentTime);
            } else if (action.equals(MUSIC_DURATION)) {
                int duration = intent.getIntExtra("duration", -1);
                music_progressBar.setMax(duration);
                finalProgress.setText(MediaUtil.formatTime(duration));
            } else if (action.equals(UPDATE_ACTION)) {
                listPosition = intent.getIntExtra("current", -1);
                url = mp3Infos.get(listPosition).getUrl();
                if (listPosition >= 0) {
                    musicName.setText(mp3Infos.get(listPosition).getName());
                    musicArtist.setText(mp3Infos.get(listPosition).getArtist());
                }
                if (listPosition == 0) {
                    finalProgress.setText(MediaUtil.formatTime(mp3Infos.get(
                            listPosition).getDuration()));
                    playBtn.setBackgroundResource(R.drawable.pause);
                    isPause = true;
                }
            }
        }
    }

}

