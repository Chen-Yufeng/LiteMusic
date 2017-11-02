package swipedelmenu.mcxtzhang.litemusic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import swipedelmenu.mcxtzhang.litemusic.adapter.MediaAdapter;
import swipedelmenu.mcxtzhang.litemusic.entity.Audio;
import swipedelmenu.mcxtzhang.litemusic.service.MediaPlayerService;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    public final static String INTENT_MEDIA = "MEDIA";
    private MediaPlayerService player;
    boolean serviceBound = false;
    List<Audio> audioList = new ArrayList<>();
    private final String TAG = "@vir MainActivity";
    MediaAdapter mediaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{READ_EXTERNAL_STORAGE}, 1);
        }

        //test


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mediaAdapter = new MediaAdapter(audioList, MainActivity.this);
        recyclerView.setAdapter(mediaAdapter);

        AudioSetBroadcastReceiver audioSetBroadcastReceiver = new AudioSetBroadcastReceiver();
        //may be wrong
        IntentFilter intentFilter = new IntentFilter("com.ifchan.litemusic.PLAY");
        registerReceiver(audioSetBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(Uri uri) {
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra(INTENT_MEDIA,uri);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

    private void loadAudio(ArrayList<Audio> audioList) {
        this.audioList.addAll(audioList);
    }

    private void loadAudio(Audio audio) {
        this.audioList.add(audio);
    }

    class AudioSetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: 11/2/17 complete it!
            Log.d(TAG, "onReceive: intent = "+intent);
            int position = intent.getIntExtra("POSITION",0);
            playAudio(audioList.get(position).getUri());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addFile:
                performFileSearch();
                return true;
            case R.id.addFolder:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int READ_REQUEST_CODE = 42;

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".

        //MAY BE A ERROR!!!
        intent.setType("audio/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
//                Log.d(TAG, "onActivityResult: uri = " + uri);
//                File file = new File(uri.getPath());
//                Log.d(TAG, "onActivityResult: file = " + file.getName() + " / " + file.getPath());
//                loadAudio(new Audio(file.getName(), file.getPath()));
//                mediaAdapter.notifyItemInserted(audioList.size() - 1);
                initializeAudioNameAndPath(uri);

            }
        }
    }

    private void initializeAudioNameAndPath(Uri uri) {
        Log.d(TAG, "initializeAudioNameAndPath: uri = " + uri);
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
//                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media
//                        .TITLE));
                String title = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                Log.d(TAG, "initializeAudioNameAndPath: title = " + title);
                loadAudio(new Audio(title, uri));
//                loadAudio(new Audio("test","/sdcard/Download/testmusic.mp3"));
                mediaAdapter.notifyItemInserted(audioList.size() - 1);
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
    }
}
