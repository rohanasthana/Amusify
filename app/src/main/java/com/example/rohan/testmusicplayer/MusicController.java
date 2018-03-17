package com.example.rohan.testmusicplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

/**
 * Created by Rohan on 3/1/2018.
 */

public class MusicController extends MediaController {
    Boolean show;
    public MusicController(Context context,Boolean mshow) {
        super(context);
        show=mshow;
    }
    public void hide(){

    }
}
