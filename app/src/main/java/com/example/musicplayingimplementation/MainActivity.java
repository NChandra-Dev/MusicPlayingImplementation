package com.example.musicplayingimplementation;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.bluetooth.BluetoothProfile;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.provider.MediaStore;
import android.telephony.TelephonyCallback;
import android.util.Log;
import android.util.Size;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static Context mContext;
    private  Uri contentUri;

    Bitmap thumbnail = null;
    String name;
    int size;
    int duration;

    ImageView albumImageMain;
    TextView musicName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContext = this.getApplicationContext(); // correct


        albumImageMain = (ImageView) findViewById(R.id.thumbnail);
        musicName = (TextView) findViewById(R.id.SongTitleMain);


        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                       Context context = getApplicationContext();
                       CharSequence text = "Runtime permission granted";
                       int durationToast = Toast.LENGTH_SHORT;
                       Toast toast = Toast.makeText(context, text, durationToast);
                       toast.show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Context context = getApplicationContext();
                        CharSequence text = "Please grant permission to fetch songs!!!";
                        int durationToast = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, durationToast);
                        toast.show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();


        List<Music> musicList = new ArrayList<Music>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        };
        String selection = MediaStore.Audio.Media.DURATION +
                " >= ?";
        String[] selectionArgs = new String[] {
                String.valueOf(TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES))
        };
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            //String album = cursor.getString(cursor
                    //.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            while (cursor.moveToNext()) {
                // Get values of columns for a given audio.
                long id = cursor.getLong(idColumn);
                name = cursor.getString(nameColumn);
                duration = cursor.getInt(durationColumn);
                size = cursor.getInt(sizeColumn);
                long albumId = cursor.getLong(albumIdColumn);
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);




                // Load thumbnail of a specific media item.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                     thumbnail = getApplicationContext().getContentResolver().loadThumbnail(
                                    contentUri, new Size(640, 480), null);
                }
                   musicList.add(new Music(contentUri, name, duration, size, thumbnail));

            }

            Music music =  musicList.get(0);
            System.out.println(music);
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }




        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);
        // Initialize contacts
        // Create adapter passing in the sample user data
        RecycleAdapter adapter = new RecycleAdapter(musicList);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
        rvContacts.addItemDecoration(new DividerItemDecoration(rvContacts.getContext(), DividerItemDecoration.VERTICAL));


        // Starting the service

        Intent myService = new Intent(this, MusicService.class);
        myService.putExtra("ACTION_PLAY", contentUri);
        startService(myService);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //when activity is destroyed the player releases and stops the media player
        //MusicMethod.mediaPlayer.stop();
        //MusicMethod.mediaPlayer.release();

    }

    public static Context getContext() {
        return mContext;
    }




    protected void onResume () {
        super.onResume();

    }



}