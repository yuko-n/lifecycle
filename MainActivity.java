package com.example.lifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


/*
Задание:
1. Доработать воспроизведение музыки с учетом жизненного цикла Activity
2. Попробовать найти и добавить функционал управления громкостью.
3. Правильно обрабатывать нажатие кнопки Stop.
4. Добавить отслеживание в реальном времени текущей секунды воспроизведения.
5. Для настойчивых. Добавить возможность считывать плейлист из папки с музыкой.
 */

public class MainActivity extends AppCompatActivity {

    private String TAG = "Life Cycle";

    MediaPlayer mPlayer;
    Button bPlay, bPause, bStop;
    SeekBar volumeControl;
    AudioManager audioManager;
    Chronometer timer;
    TextView text;
    boolean running;
    long pauseOffset;
    String duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, (String) "onCreate() method");

        mPlayer = MediaPlayer.create(this, R.raw.test);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onStop();
            }
        });
        bPlay = findViewById(R.id.buttonPlay);
        bPause = findViewById(R.id.buttonPause);
        bStop = findViewById(R.id.buttonStop);
        timer = findViewById(R.id.timer);
        duration = millisecondsToTime(mPlayer.getDuration());
        text = findViewById(R.id.text1);
        text.setText(duration);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeControl = findViewById(R.id.volumeControl);
        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(curValue);
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bPause.setEnabled(false);
        bStop.setEnabled(false);

    }

    @Override
    protected  void onStart() {
        super.onStart();

        Log.i(TAG, (String) "onStart() method");
    }

    @Override
    protected  void onPause() {
        super.onPause();

        Log.i(TAG, (String) "onPause() method");
    }

    @Override
    protected  void onResume() {
        super.onResume();

        Log.i(TAG, (String) "onResume() method");
    }

    @Override
    protected  void onStop() {
        super.onStop();

        Log.i(TAG, (String) "onStop() method");
    }
    private String millisecondsToTime(int millis) {
        double min = TimeUnit.MILLISECONDS.toMinutes(millis);
        double sec = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("/0%1$,.0f:%2$,.0f", min, sec);
    }
    public void stop(View view){
        mPlayer.stop();
        bPause.setEnabled(false);
        bStop.setEnabled(false);
            pauseOffset = 0;
            timer.setBase(SystemClock.elapsedRealtime());
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
            bPlay.setEnabled(true);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void play(View view){
        if (!running) {
            timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            timer.start();
            running = true;
        }
        mPlayer.start();
        bPlay.setEnabled(false);
        bPause.setEnabled(true);
        bStop.setEnabled(true);
    }
    public void pause(View view){
        if (running){
            timer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - timer.getBase();
            running = false;
        }
        mPlayer.pause();
        bPlay.setEnabled(true);
        bPause.setEnabled(false);
        bStop.setEnabled(true);
    }






}