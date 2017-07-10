package com.werb.mediautilsdemo.activity;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.werb.mediautilsdemo.R;
import com.werb.mediautilsdemo.permissionUtils.PermissionUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private Button audio,video,playAudio;
    private PermissionUtils permissionUtils;
    private int type;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionUtils = new PermissionUtils(this); // initialize，must need

        audio = (Button) findViewById(R.id.btn_audio);
        video = (Button) findViewById(R.id.btn_video);
        playAudio = (Button) findViewById(R.id.play_audio);

        audio.setOnClickListener(audioClick);
        video.setOnClickListener(videoClick);
        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }

                    mMediaPlayer.reset();
                    File[] files = new File(Environment.getExternalStorageDirectory() + "/DownloadAudioVideo").listFiles();
                    //这个注释是获取指定文件的路径
//                    mMediaPlayer.setDataSource( new File(Environment.getExternalStorageDirectory()+"/DownloadAudioVideo","zw123456.m4a").getAbsolutePath());


                    //获取手机文件夹后默认数组中文件顺序是按照时间倒序的，即最后的是最新的。
                    mMediaPlayer.setDataSource(files[files.length-1].getAbsolutePath());
                    Log.d("zw--dudio",files[files.length-1].getAbsolutePath());
                    mMediaPlayer.prepareAsync();// 开始在后台缓冲音频文件并返回

                    // 缓冲监听
                    mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        }
                    });
                    // 后台准备完成监听
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                             Log.d("zw--onPrepared", "缓冲完成播放音频");
                            mMediaPlayer.start();
                        }
                    });

                    // 播放完成监听
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            Log.d("zw--OnCompletion", "缓冲完成播放音频");
                            stopMusic();

                        }
                    });

                    // 播放错误监听
                    mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            Toast.makeText(MainActivity.this,extra+"---error", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 停止播放音频文件
     */
    public void stopMusic() {
        Log.d("zw--stopMusic1", "完成播放音频");
        if (mMediaPlayer.isPlaying()) {
            Log.d("zw--stopMusic", "完成播放音频");
            mMediaPlayer.stop();
        }
    }
    View.OnClickListener audioClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            type = 1;
            if (permissionUtils.isNeedPermissions(PERMISSIONS)) {
                permissionUtils.requestPermissions();
            } else {
                startAudio();
            }
        }
    };

    View.OnClickListener videoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            type = 2;
            if (permissionUtils.isNeedPermissions(PERMISSIONS)) {
                permissionUtils.requestPermissions();
            } else {
                startVideo();
            }
        }
    };

    private void startAudio(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,AudioRecorderActivity.class);
        startActivity(intent);
    }

    private void startVideo(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,VideoRecorderActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSION_REQUEST_CODE:
                if (permissionUtils.isRequestAllPermissions(grantResults)) {
                    if(type == 1){
                        startAudio();
                    }else if(type == 2){
                        startVideo();
                    }
                } else {
                    permissionUtils.showDialog();
                }
                break;
        }
    }

}
