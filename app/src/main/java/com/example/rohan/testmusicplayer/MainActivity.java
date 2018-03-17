package com.example.rohan.testmusicplayer;
import com.example.rohan.testmusicplayer.MusicService.MusicBinder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.triggertrap.seekarc.SeekArc;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{
    private ArrayList<Song> songList;
    private ListView songView;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private TextView mSongName;
    private TextView mArtistName;
    private ImageView mAlbumArtSmall;
    private ImageView mAlbumArt;
    private ImageButton mPlayIcon;
    private ImageButton mNextIcon;
    private SlidingUpPanelLayout mSlidingPane;
    private View mBottomBar;
    Animation fadeIn;
    Animation fadeOut;
    private SlidingUpPanelLayout mSlidingPaneNow;
    private MusicController controller;
    private ImageView mNowPlaying;
    private boolean paused = false, playbackPaused = false;
    private ImageView mNowPlayingAlbum;
    private TextView mNowPlayingText;
    private TextView mNowPlayingArtist;
    private ImageButton mShuffleIcon;
    private ImageButton mNowPlayingPause;
    private ImageButton mNowPlayingPrev;
    private ImageButton mNowPlayingNext;
    private Bitmap bitmap=null;




    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            //setController();
            paused = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

// MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
// app-defined int constant

                return;
            }
        }


        mSlidingPane = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mNowPlayingAlbum=(ImageView)findViewById(R.id.now_playing_album);
        mSlidingPaneNow = (SlidingUpPanelLayout) findViewById(R.id.sliding_now_playing);
        mNowPlayingText=(TextView)findViewById(R.id.now_playing_text);
        mSlidingPane.setParallaxOffset(300);
        mSlidingPaneNow.setParallaxOffset(300);
        mBottomBar = (View) findViewById(R.id.bottom_bar);
        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<Song>();
        mSongName = (TextView) findViewById(R.id.song_name);
        mArtistName = (TextView) findViewById(R.id.artist_name);
        mAlbumArt = (ImageView) findViewById(R.id.album_cover);
        mAlbumArtSmall = (ImageView) findViewById(R.id.album_cover_small);
        mPlayIcon = (ImageButton) findViewById(R.id.pause_icon);
        mNextIcon = (ImageButton) findViewById(R.id.next_icon);
        mNowPlaying=(ImageView) findViewById(R.id.now_playing);
        mNowPlayingArtist=(TextView)findViewById(R.id.now_playing_artist);
        mShuffleIcon=(ImageButton)findViewById(R.id.shuffle_icon);
        mNowPlayingPause=(ImageButton)findViewById(R.id.now_playing_pause);
        mNowPlayingNext=(ImageButton)findViewById(R.id.play_next);
        mNowPlayingPrev=(ImageButton)findViewById(R.id.play_prev);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/muktamaheelight.ttf");
        Typeface font1=Typeface.createFromAsset(getAssets(),"fonts/muktaamaheeextralight.ttf");
        mSongName.setTypeface(font);
        mArtistName.setTypeface(font);
        mNowPlayingText.setTypeface(font1);
        mNowPlayingArtist.setTypeface(font1);
        mSlidingPane.setAnchorPoint(1.0f);
        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().toUpperCase().compareTo(b.getTitle().toUpperCase());
            }
        });
        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mSlidingPane.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }


            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (mSlidingPane.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mBottomBar.setBackgroundColor(Color.parseColor("#00000f"));
                } else {
                    mBottomBar.setBackgroundColor(Color.parseColor("#212121"));
                }
            }
        });

        mSlidingPaneNow.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {


            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (previousState == SlidingUpPanelLayout.PanelState.DRAGGING && newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                { mSlidingPane.setTouchEnabled(false);}

// Hide the status bar.

                else
                    mSlidingPane.setTouchEnabled(true);


            }

        });

        setController();
        mPlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playpause();
            }
        });
        mNextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();

            }
        });

        mShuffleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean shuf= musicSrv.setShuffle();
                if(!shuf){
                    mShuffleIcon.setBackgroundResource(R.drawable.shuffle_off);
                    Toast.makeText(getApplicationContext(),"Shuffle Off",Toast.LENGTH_SHORT).show();}
                else{
                    mShuffleIcon.setBackgroundResource(R.drawable.shuffle);
                    Toast.makeText(getApplicationContext(),"Shuffle On",Toast.LENGTH_SHORT).show();}

            }
        });

        mNowPlayingPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playpause();
                if(musicSrv.isPng()){
                    mNowPlayingPause.setBackgroundResource(R.drawable.now_playing_pause);
                }
                else{
                    mNowPlayingPause.setBackgroundResource(R.drawable.play_now_playing);
                }
            }
        });
        mNowPlayingNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        mNowPlayingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrev();

            }
        });




    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //retrieve song info
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumArtCollumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisAlbumArt = musicCursor.getLong(albumArtCollumn);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbumArt));
            }
            while (musicCursor.moveToNext());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void songPicked(View view) {
        Song playSong1 = songList.get(Integer.parseInt(view.getTag().toString()));
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        setViews(playSong1);

    }

    public void setViews(Song playSong1) {

        mNowPlayingPause.setBackgroundResource(R.drawable.now_playing_pause);

        final String name = playSong1.getTitle();
        final String artist = playSong1.getArtist();
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Long x1 = playSong1.getAlbumArt();
        final Uri uri = ContentUris.withAppendedId(sArtworkUri,
                x1);
        mAlbumArt.startAnimation(fadeOut);
        mNowPlaying.startAnimation(fadeOut);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    Blurry.with(getApplicationContext()).from(bitmap).into(mAlbumArt);
                    Blurry.with(getApplicationContext()).from(bitmap).into(mNowPlaying);
                    mNowPlayingAlbum.setImageBitmap(bitmap);

                    mAlbumArtSmall.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(bitmap==null){
                    bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.logo_white);
                    Blurry.with(getApplicationContext()).from(bitmap).into(mAlbumArt);
                    Blurry.with(getApplicationContext()).from(bitmap).into(mNowPlaying);
                    mAlbumArtSmall.setImageBitmap(bitmap);
                    mNowPlayingAlbum.setImageBitmap(bitmap);
                }
                mAlbumArt.startAnimation(fadeIn);
                mNowPlaying.startAnimation(fadeIn);
                bitmap=null;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        mSongName.setText(name);
        mNowPlayingText.setText(name);
        mNowPlayingArtist.setText(artist);
        mPlayIcon.setBackgroundResource(R.drawable.pause);
        mArtistName.setText(artist);
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        //controller.show(0);


    }



    @Override
    public void onBackPressed() {
        if(mSlidingPane.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED &&mSlidingPaneNow.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
            mSlidingPane.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }
        else if(mSlidingPaneNow.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED){
            mSlidingPaneNow.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }

        else{
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }

    }
    public void playNext(){
        int song=musicSrv.playNext();
        Song song2=songList.get(song);
        setViews(song2);
    }
    public void playpause(){
        if (musicSrv.isPng()) {
            musicSrv.pausePlayer();
            mPlayIcon.setBackgroundResource(R.drawable.play);
        } else {
            musicSrv.go();
            mPlayIcon.setBackgroundResource(R.drawable.pause);
        }
    }
    public void playPrev(){
        int so=musicSrv.playPrev();
        Song song3=songList.get(so);
        setViews(song3);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null&&musicBound&&musicSrv.isPng()) {
            return musicSrv.getDur();
        }else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null&&musicBound&&musicSrv.isPng()){
            return musicSrv.getPosn();
        }else
            return 0;
    }

    @Override
    public void seekTo(int i) {
        musicSrv.seek(i);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null&&musicBound){
            return musicSrv.isPng();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    private void setController(){
        controller=new MusicController(this,false);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicSrv.playNext();
                if(playbackPaused){
                    setController();
                    playbackPaused=false;
                }
                // controller.show(0);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicSrv.playPrev();
                if(playbackPaused){
                    setController();
                    playbackPaused=false;
                }
                //controller.show(0);
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.now_playing));
        controller.setEnabled(true);
    }
}
