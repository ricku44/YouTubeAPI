package com.sample.szone.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Main2Activity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {


    private YoutubeDataModel youtubeDataModel = null;
    TextView textViewName;
    TextView textViewDes;
    TextView textViewDate;
    TextView tester;
    private YouTubePlayer mYoutubePlayer = null;
    String track = ">";
    int j = 0, k = 0, l =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        k=0;l=0;
        j = getIntent().getIntExtra("check",0);
        youtubeDataModel = getIntent().getParcelableExtra(YoutubeDataModel.class.toString());
        Log.e("", youtubeDataModel.getDescription());

        YouTubePlayerView mYoutubePlayerView = findViewById(R.id.youtube_player);
        String GOOGLE_YOUTUBE_API = "AIzaSyAk5kfRweMvBkQxczSHMjLrQjnsDmM4gP8";
        mYoutubePlayerView.initialize(GOOGLE_YOUTUBE_API, this);

        textViewName =  findViewById(R.id.textViewName);
        textViewDes =  findViewById(R.id.textViewDes);
        textViewDate =  findViewById(R.id.textViewDate);

        tester = findViewById(R.id.tester);
        tester.setMovementMethod(new ScrollingMovementMethod());

        textViewName.setText(youtubeDataModel.getTitle());
        textViewDes.setText(youtubeDataModel.getDescription());
        textViewDate.setText(youtubeDataModel.getPublishedAt());

        if(j==1 || j==2) {
            track = track + "On Start from LiveTv Button or Notification...\nSending Event...\t" + "Tried Playing\n";
            tester.setText(track);

            try {
                MyApplication.getInstance().trackEvent("tried playing", youtubeDataModel.getChannel());
                track = track + "Success\n";
                tester.setText(track);

            } catch (Exception e) {
                MyApplication.getInstance().trackException(e);
                track = track + "Error:\t" + e + "\n";
                tester.setText(track);
            }

            if (j == 2) {
                track = track + "On Start from Notification...\nSending Event...\t" + "Tried Playing Notification\n";
                tester.setText(track);

                try {
                    MyApplication.getInstance().trackEvent("tried playing notification", youtubeDataModel.getChannel());
                    track = track + "Success\n";
                    tester.setText(track);

                } catch (Exception e) {
                    MyApplication.getInstance().trackException(e);
                    track = track + "Error:\t" + e + "\n";
                    tester.setText(track);
                }

            }
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        if (!b) {
            youTubePlayer.loadVideo(youtubeDataModel.getVideo_id());
            mYoutubePlayer = youTubePlayer;
        }
    }


    private YouTubePlayer.PlaybackEventListener playbackEventListener =
            new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {


                final Timer timer = new Timer();
                TimerTask hourlyTask = new TimerTask() {
                    @Override
                    public void run() {

                        if(k==1){
                            timer.cancel();
                            timer.purge();
                        }
                        track = track + "On Play Every 5s...\nSending Event...\t" + "Total Play Duration\n";

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tester.setText(track);

                            }
                        });

                        try {
                            MyApplication.getInstance().trackEvent2("total play duration", youtubeDataModel.getTitle(), 5);
                            track = track + "Success\n";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tester.setText(track);

                                }
                            });
                        } catch (Exception e) {
                            MyApplication.getInstance().trackException(e);
                            track = track + "Error:\t" + e + "\n";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tester.setText(track);

                                }
                            });
                        }

                    }
                };

                timer.schedule(hourlyTask, 5000, 5000);


            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    if(j==0 || j==1){
                        track = track + "On Start 1s...\nSending Event...\t"+"Start\n";
                        tester.setText(track);

                        try {
                            MyApplication.getInstance().trackEvent("Start", youtubeDataModel.getTitle());
                            track = track + "Success\n";
                            tester.setText(track);

                        }
                        catch (Exception e) {
                            MyApplication.getInstance().trackException(e);
                            track = track + "Error:\t"+e+"\n";
                            tester.setText(track);
                        }
                    }

                    else if(j==2){
                        track = track + "On Start from Notification 1s...\nSending Event...\t"+"Start\n";
                        tester.setText(track);

                        try {
                            MyApplication.getInstance().trackEvent("start notification", youtubeDataModel.getChannel());
                            track = track + "Success\n";
                            tester.setText(track);

                        }
                        catch (Exception e) {
                            MyApplication.getInstance().trackException(e);
                            track = track + "Error:\t"+e+"\n";
                            tester.setText(track);
                        }
                    }
                }
            }, 1000);

        }

        @Override
        public void onPaused() {

            k = 1;

        }

        @Override
        public void onStopped() {

            if(TimeUnit.MILLISECONDS.toSeconds(mYoutubePlayer.getCurrentTimeMillis())!=0) {

                track = track + "On Stop...\nSending Event...\t" + "Play Duration\n";
                tester.setText(track);

                try {
                    MyApplication.getInstance().trackEvent2("play duration", youtubeDataModel.getTitle(), TimeUnit.MILLISECONDS.toSeconds(mYoutubePlayer.getCurrentTimeMillis()));
                    track = track + "Success\n";
                    tester.setText(track);
                } catch (Exception e) {
                    MyApplication.getInstance().trackException(e);
                    track = track + "Error:\t" + e + "\n";
                    tester.setText(track);
                }
            }

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };


    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener =
            new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

            l=1;
        }

        @Override
        public void onLoaded(String s) {


        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {



        }

        @Override
        public void onVideoEnded() {

                track=track+"On End...\nSending Event...\t"+"Play Duration\n";
                tester.setText(track);

                try{
                        MyApplication.getInstance().trackEvent2("play duration",youtubeDataModel.getTitle(),TimeUnit.MILLISECONDS.toSeconds(mYoutubePlayer.getCurrentTimeMillis()));
                        track=track+"Success\n";
                        tester.setText(track);
                }
                catch(Exception e){
                        MyApplication.getInstance().trackException(e);
                        track=track+"Error:\t"+e+"\n";
                        tester.setText(track);
                }

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }

    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public void share_btn_pressed(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String link = ("https://www.youtube.com/watch?v=" + youtubeDataModel.getVideo_id());
        // this is the text that will be shared
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, youtubeDataModel.getTitle()
                + "Share");

        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "share"));
    }

    @Override
    public void onStart() {
        super.onStart();
        MyApplication.getInstance().trackScreenView();

    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView();
    }

}
