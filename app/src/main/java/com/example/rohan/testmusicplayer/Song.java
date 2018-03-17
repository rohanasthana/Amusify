package com.example.rohan.testmusicplayer;

/**
 * Created by Rohan on 2/22/2018.
 */

public class Song {

    private long id;
    private String title;
    private String artist;
    private long albumArt;
    public Song(long songID, String songTitle, String songArtist,long songalbumart) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        albumArt=songalbumart;
    }
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public long getAlbumArt(){return albumArt;}
}
