package com.attrsc.braincs.soundrecorder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.attrsc.braincs.soundrecorder.audio.AudioRecorder;
import com.attrsc.braincs.soundrecorder.audio.AudioTrackManager;
import com.attrsc.braincs.soundrecorder.audio.RecordStreamListener;
import com.attrsc.braincs.soundrecorder.audio.VolumeUtil;
import com.attrsc.braincs.soundrecorder.utils.Constants;
import com.attrsc.braincs.soundrecorder.utils.FileUtils;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private AudioRecorder recorder;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private boolean isPrepared;
    private boolean isEnableBle = false;
    private TextView tvText;
    private AudioTrackManager audioPlayer;
    private AudioManager mAudioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvText = findViewById(R.id.tvText);

        initRecorder();

        initPlayer();

        getPermissions();
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initPlayer() {
        audioPlayer = new AudioTrackManager();
    }

    private void initRecorder() {
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if(!mAudioManager.isBluetoothScoAvailableOffCall()){
            Log.d(TAG, "系统不支持蓝牙录音");
            return;
        }
        //蓝牙录音的关键，启动SCO连接，耳机话筒才起作用
//        mAudioManager.startBluetoothSco();
        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
//
//                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
//                    mAudioManager.setMode(AudioManager.MODE_IN_CALL);
//                    mAudioManager.setBluetoothScoOn(true);  //打开SCO
////                    mRecorder.start();//开始录音
//                    isPrepared = true;
//                    unregisterReceiver(this);  //别遗漏
//                }else{//等待一秒后再尝试启动SCO
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    mAudioManager.startBluetoothSco();
//                }
//            }
//        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
        recorder = new AudioRecorder();
//        recorder.createAudio(Constants.CACHE_RECORD_AUDIO_NAME,
//                MediaRecorder.AudioSource.VOICE_COMMUNICATION,16000,
//                AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT);

        recorder.createDefaultAudio(Constants.CACHE_RECORD_AUDIO_NAME);
//        recorder.createDefaultAudio(Constants.CACHE_RECORD_AUDIO_NAME);
        recorder.setListener(recordStreamListener);
    }

    private RecordStreamListener recordStreamListener = new RecordStreamListener() {
        @Override
        public void onRecording(byte[] audiodata, int i, int length) {
            double volume = VolumeUtil.calculateVolumeRate(audiodata);

            Log.d(TAG, "volume: " + volume);
//            if (null != volumeCircleBar) {
//                volumeCircleBar.updateVolumeRate(volume);
//            }
            tvText.setText(String.format("volume: %s", volume));
        }

        @Override
        public void finishRecord() {

        }
    };


    public void onClickRecord(View view) {
        if (!isRecording){
            recorder.startRecord();
        }else {
            recorder.stopRecord();

        }
        isRecording = !isRecording;
    }

    public void onClickPlay(View view) {

        if (!isPlaying){
            audioPlayer.startPlay(FileUtils.getFileUnderAppFolder(Constants.CACHE_RECORD_AUDIO_NAME));
        }else {
            audioPlayer.stopPlay();
        }
        isPlaying = !isPlaying;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void onClickBt(View view) {
        if (!isEnableBle){
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
            mAudioManager.setBluetoothScoOn(true);  //打开SCO
            mAudioManager.startBluetoothSco();
        }else {
            if(mAudioManager.isBluetoothScoOn()){
                mAudioManager.setBluetoothScoOn(false);
                mAudioManager.stopBluetoothSco();
            }
        }
        isEnableBle = !isEnableBle;
    }
}
