package com.example.pram270757.musicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayActivity extends AppCompatActivity {
 private RelativeLayout parentRelative;
 private SpeechRecognizer speechRecognizer;
 private Intent speechRecognizerIntent;
 private String keeper = "";
 private String mode="ON";

 private ImageView playPauseBtn,previousBtn,nextBtn;
 private TextView songNameText;
 private ImageView imageView;
 private RelativeLayout lowerRelative;
 private Button enableBtn;

 private MediaPlayer mediaPlayer;
 private int position;
 private ArrayList<File> mSongs;
 private String mSongName;

 private Toolbar toolbar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_play);
        toolbar1 = (Toolbar)findViewById(R.id.smart_toolbar);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CheckVoiceCommandPermission();

        playPauseBtn = (ImageView)findViewById(R.id.btn_play_pause);
       previousBtn = (ImageView)findViewById(R.id.btn_previous);
        nextBtn = (ImageView)findViewById(R.id.btn_next);
       imageView = (ImageView)findViewById(R.id.logo);
       enableBtn = (Button) findViewById(R.id.voice_enabled);
       songNameText = (TextView)findViewById(R.id.songName);
       lowerRelative = (RelativeLayout) findViewById(R.id.lower);
        parentRelative = (RelativeLayout)findViewById(R.id.parentRelativeLayout);

         speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayActivity.this);
         speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
         speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
         speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


         validateRecieveValueAndStartPlaying();
         imageView.setBackgroundResource(R.drawable.logo);

         speechRecognizer.setRecognitionListener(new RecognitionListener() {
             @Override
             public void onReadyForSpeech(Bundle params) {

             }

             @Override
             public void onBeginningOfSpeech() {

             }

             @Override
             public void onRmsChanged(float rmsdB) {

             }

             @Override
             public void onBufferReceived(byte[] buffer) {

             }

             @Override
             public void onEndOfSpeech() {

             }

             @Override
             public void onError(int error) {

             }

             @Override
             public void onResults(Bundle results) {
                 ArrayList<String> matchFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                 if(matchFound !=null){
                     if(mode.equals("ON")){
                         keeper = matchFound.get(0);

                         if(keeper.equals("pause the song")){
                             playPauseSong();
                             Toast.makeText(SmartPlayActivity.this,"Command="+keeper,Toast.LENGTH_LONG).show();
                         }
                         else if(keeper.equals("play the song")){
                             playPauseSong();
                             Toast.makeText(SmartPlayActivity.this,"Command="+keeper,Toast.LENGTH_LONG).show();
                         }
                         else if(keeper.equals("play next song")){
                             playNextSong();
                             Toast.makeText(SmartPlayActivity.this,"Command="+keeper,Toast.LENGTH_LONG).show();
                         }
                         else if(keeper.equals("play previous song")){
                             playPreviousSong();
                             Toast.makeText(SmartPlayActivity.this,"Command="+keeper,Toast.LENGTH_LONG).show();
                         }
                     }


                 }
             }

             @Override
             public void onPartialResults(Bundle partialResults) {

             }

             @Override
             public void onEvent(int eventType, Bundle params) {

             }
         });


        parentRelative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });
        /*
         * This Enable Button is used for Voice Enabled
         */
        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("ON")){
                    mode= "OFF";
                    enableBtn.setText("Voice Enabled Mode-OFF");
                    lowerRelative.setVisibility(View.VISIBLE);
                }
                else{
                    mode= "ON";
                    enableBtn.setText("Voice Enabled Mode-ON");
                    lowerRelative.setVisibility(View.GONE);
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSong();
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    playPreviousSong();

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    playNextSong();


            }
        });
    }


    private void validateRecieveValueAndStartPlaying(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mSongs = (ArrayList)bundle.getParcelableArrayList("songs");
        mSongName  = mSongs.get(position).getName();
        String SongName = intent.getStringExtra("name");

        songNameText.setText(SongName);
        songNameText.setSelected(true);

        position = bundle.getInt("position",0);
        Uri uri = Uri.parse(mSongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(SmartPlayActivity.this,uri);
        mediaPlayer.start();
    }

    /*
     this method is checking ther permission for voice enabling.
     */

    private void CheckVoiceCommandPermission(){
        if(Build.VERSION.SDK_INT >Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(SmartPlayActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" +getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    /*
     * this method is used for play or pause the song
     */

    private void playPauseSong(){
        imageView.setBackgroundResource(R.drawable.four);
        if(mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        }
        else{
            playPauseBtn.setImageResource(R.drawable.pause);
            mediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    private void playNextSong(){
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position+1)%mSongs.size());

        Uri uri = Uri.parse(mSongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(SmartPlayActivity.this,uri);
        mSongName = mSongs.get(position).toString();
        songNameText.setText(mSongName);

        mediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);
        if(mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.pause);
            mediaPlayer.pause();
        }
        else{
            playPauseBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    private void playPreviousSong(){
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position-1)<0 ? (mSongs.size()-1): (position-1));

        Uri uri = Uri.parse(mSongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(SmartPlayActivity.this,uri);
        mSongName = mSongs.get(position).toString();
        songNameText.setText(mSongName);

        mediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);
        if(mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.pause);
            mediaPlayer.pause();
        }
        else{
            playPauseBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
