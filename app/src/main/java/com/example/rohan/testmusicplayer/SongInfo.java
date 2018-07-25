package com.example.rohan.testmusicplayer;

/**
 * Created by Bhavya Tripathi on 18/07/2018.
 */

public class SongInfo {
    public String songName,artistName,songUrl,albumName;
    public long albumArt;


    public SongInfo(String songName,String artistName,String songUrl,String albumName,long albumArt){
        this.songName=songName;
        this.artistName=artistName;
        this.songUrl=songUrl;
        this.albumName=albumName;
        this.albumArt=albumArt;


    }
    public String getSongName(){
        return songName;
    }
    public String getArtistName(){
        return artistName;
    }
    public String getSongUrl(){
        return songUrl;
    }
    public String getAlbumName(){return albumName;}

    public long getAlbumArt() {
        return albumArt;
    }
}

