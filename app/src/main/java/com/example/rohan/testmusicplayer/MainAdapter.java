package com.example.rohan.testmusicplayer;

import android.content.ContentUris;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Rohan on 3/6/2018.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Bhavya Tripathi on 03/07/2018.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.SongHolder> {
    SongInfo c;
    private Bitmap bitmap=null;
    int i;
    ArrayList<SongInfo> _songs;
    Context context;
    OnItemClickListener onItemClickListener;
    public MainAdapter(Context context,ArrayList<SongInfo> _songs){
        this.context=context;
        this._songs=_songs;
    }
    public interface OnItemClickListener{
        void onItemClick(Button b,View v,SongInfo obj,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View myview= LayoutInflater.from(context).inflate(R.layout.row,viewGroup,false) ;
        return new SongHolder(myview);
    }

    @Override
    public void onBindViewHolder(final SongHolder songHolder,final int i) {

        if (_songs.size() != 0) {
            c = _songs.get(i);
            songHolder.songName.setText(c.albumName);
            songHolder.artistName.setText(c.artistName);
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            long x1=c.albumArt;
            final Uri uri = ContentUris.withAppendedId(sArtworkUri,
                    x1);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            songHolder.albumArt.setImageBitmap(bitmap);

        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        TextView songName,artistName;
        ImageView albumArt;
        Button btnAction;
        public SongHolder(View itemView) {
            super(itemView);
            songName=(TextView)itemView.findViewById(R.id.tvSongName);
            artistName=(TextView)itemView.findViewById(R.id.tvArtistName);

            albumArt=(ImageView)itemView.findViewById(R.id.image1);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/muktamaheelight.ttf");
            Typeface font1=Typeface.createFromAsset(context.getAssets(),"fonts/muktaamaheeextralight.ttf");
            songName.setTypeface(font);
            artistName.setTypeface(font1);
        }
    }
}


