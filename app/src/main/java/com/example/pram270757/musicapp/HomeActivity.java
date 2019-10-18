package com.example.pram270757.musicapp;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private String[] items;
    private ListView mSongList;
    private Toolbar toolbar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar1 = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setTitle("Music Player");


        mSongList = (ListView)findViewById(R.id.songList);
        runTimePermission();
    }
    public void runTimePermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                     displayOnlyAudioSong();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /*
     * This method is create to find the songs.
     */
    public ArrayList<File> readOnlyAudioSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] allFiles = file.listFiles();
        for(File singleFile: allFiles){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(readOnlyAudioSong(singleFile));
            }
            else
            if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".aac") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".wma")){
                arrayList.add(singleFile);
            }
        }
        return arrayList;
    }


    void displayOnlyAudioSong(){
        final ArrayList<File> audioSong = readOnlyAudioSong(Environment.getExternalStorageDirectory());
        items = new String[audioSong.size()];
        for(int songCounter=0;songCounter<audioSong.size();songCounter++){
            items[songCounter] = audioSong.get(songCounter).getName().toString().replace(".mp3","").replace(".wav","");
        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,items);
        mSongList.setAdapter(myAdapter);

        mSongList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String songName = mSongList.getItemAtPosition(position).toString();
               Intent intent = new Intent(HomeActivity.this,SmartPlayActivity.class);
               intent.putExtra("songs",audioSong);
               intent.putExtra("name",songName);
               intent.putExtra("position",position);
               startActivity(intent);

            }
        });
    }

}
