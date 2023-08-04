package com.example.musicplayingimplementation;

import static android.content.Intent.getIntent;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;
import android.window.SplashScreen;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener {

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    static MediaPlayer mediaPlayer;
    Music music;



    @Override
    public void onCreate() {
        //  The service is being created.
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Uri uri = intent.getParcelableExtra("ACTION_PLAY");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(MainActivity.getContext(), uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();
        Toast.makeText(this, "Service Done", Toast.LENGTH_SHORT).show();


        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
      mediaPlayer.start();
    }



}
