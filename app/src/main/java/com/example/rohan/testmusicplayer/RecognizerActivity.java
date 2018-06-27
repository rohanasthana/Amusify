package com.example.rohan.testmusicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by Rohan on 6/25/2018.
 */

public class RecognizerActivity {
    private ArrayList<Song> songs;
    private AudioManager audioManager = null;
    private Context context;
    String type = "";
    int no_of_words = 0;
    int f = 0;
    int begin = 0;
    MainActivity act = new MainActivity();

    public RecognizerActivity(Context context1) {
        context = context1;
    }

    public int getType(String sentence) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Song s = null;
        int indexOfSpace = 0;
        int nextIndexOfSpace = 0;
        sentence = sentence + " ";
        for (int i = 0; i < sentence.length(); i++) {
            char ch = sentence.charAt(i);
            if (ch == ' ') {
                no_of_words = no_of_words + 1;
            }
        }
        String words[] = new String[no_of_words];
        for (int i = 0; i < sentence.length(); i++) {
            char ch = sentence.charAt(i);
            if (ch == ' ') {
                words[f] = sentence.substring(begin, i);
                begin = i + 1;
                f++;
            }
        }
        int pos = -1;
        int f2 = 0;
        switch (words[0].toUpperCase()) {
            case "PLAY":
                int x = playSong(words);
                pos = x;
                break;
            default:
                break;
        }
        for (int i = 0; i < words.length; i++) {
            if (words[i].toUpperCase().equals("VOLUME") || words[i].toUpperCase().equals("SOUND")) {
                f2 = f2 + 10;
                break;
            }
        }
        if (f2 >= 10) {
            for (int i = 0; i < words.length; i++) {
                if (words[i].toUpperCase().equals("DECREASE") || words[i].toUpperCase().equals("LOWER") || words[i].toUpperCase().equals("DOWN") || words[i].toUpperCase().equals("LOW")) {
                    f2 = 2;
                }
                if (words[i].toUpperCase().equals("INCREASE") || words[i].toUpperCase().equals("UP") || words[i].toUpperCase().equals("LOUDER") || words[i].equals("RAISE"))
                    f2 = 1;
            }
        }
        if (f2 == 2) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            pos = -2;
        }
        if (f2 == 1) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            pos = -3;
        }


        return pos;

    }

    public int playSong(String words[]) {
        songs = new ArrayList<Song>();
        songs = act.getSongList(context);
        Collections.sort(songs, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().toUpperCase().compareTo(b.getTitle().toUpperCase());
            }
        });
        String song = "";
        for (int i = 0; i < words.length - 1; i++) {
            if (i != words.length - 2)
                song = song + words[i + 1] + " ";
            else
                song = song + words[i + 1];
        }
        if (song.toUpperCase().equals("")){
            return -1;
        }
        int x = 0;
        int f = 0;
        int fmax = 0;
        int imax = 0;
        for (int i = 0; i < songs.size(); i++) {
            String song_name = songs.get(i).getTitle();
            if (song.toUpperCase().equals(song_name.toUpperCase())) {
                x = i;
                return x;
            }
        }
        for (int i = 0; i < songs.size(); i++) {
            String song_name = songs.get(i).getTitle();
            outerloop:
            for (int j = 0; j < words.length - 1; j++) {

                for (int k = 0; k < song_name.length(); k++) {
                    for (int l = k; l < song_name.length(); l++) {
                        String str = song_name.substring(k, l + 1);

                        if (words[j + 1].toUpperCase().equals(str.toUpperCase())) {
                            f++;
                            Log.i("Match", str);
                            continue outerloop;
                        }

                    }
                }
            }



        if (f >= fmax) {
            fmax = f;
            imax = i;
        }
        f = 0;
    }
        x=imax;
        return x;

    }


}
