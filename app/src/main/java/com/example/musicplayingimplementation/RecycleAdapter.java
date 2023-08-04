package com.example.musicplayingimplementation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private String[] localDataSet;


    static MediaPlayer mediaplayer;

    public static Context context;

    public static List<Music> mMusicList;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
     public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;

        private final ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            context = view.getContext();
            view.setOnClickListener(this);

            textView = (TextView) view.findViewById(R.id.SongTitleMain);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            thumbnail.setOnClickListener(this);
            textView.setOnClickListener(this);

        }


        public TextView getTextView() {
            return textView;
        }

        @Override
        public void onClick(View view) {
            if (mediaplayer.isPlaying()) {
                mediaplayer.stop();
                int position = this.getAdapterPosition();
                Music music = mMusicList.get(position);

                String name = music.getName();
                Bitmap AlbumArt = music.getThumbnail();
                Uri uri = music.getUri();

                // getTrackInfo


                Intent myIntent = new Intent(MainActivity.getContext(), MusicMethod.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.putExtra("SongName", name.replace(".mp3", ""));
                myIntent.putExtra("AlbumArtUri", uri);
                MainActivity.getContext().startActivity(myIntent);
            }

        }


    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param musicList String[] containing the data to populate views to be used
     * by RecyclerView
     */
    // Store a member variable for the contacts
   

    // Pass in the contact array into the constructor
    public RecycleAdapter(List<Music> musicList) {
        mMusicList = musicList;

    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        // viewHolder.getTextView().setText(localDataSet[position]);
        // Get the data model based on position
        Music music = mMusicList.get(position);

        // Set item views based on your

        TextView textView = viewHolder.textView;
        String musicName = music.getName();
        if (musicName.length()>20){
           musicName =  musicName.substring(0, 20) + "...";
        }

        textView.setText(musicName.replace(".mp3", ""));

        ImageView imageView = viewHolder.thumbnail;
        imageView.setImageBitmap(music.getThumbnail());



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {

        return mMusicList.size();
    }


}
