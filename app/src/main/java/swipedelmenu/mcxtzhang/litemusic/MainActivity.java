package swipedelmenu.mcxtzhang.litemusic;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.DocumentsContract;
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
        registerReceiver(audioSetBroadcastReceiver, intentFilter);
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

    private void playAudio(String path) {
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra(INTENT_MEDIA, path);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
            Intent playNewIntent = new Intent(MediaPlayerService.PLAY_NEW);
            playNewIntent.putExtra(INTENT_MEDIA,path);
            sendBroadcast(playNewIntent);
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
            Log.d(TAG, "onReceive: intent = " + intent);
            int position = intent.getIntExtra("POSITION", 0);
            playAudio(audioList.get(position).getPath());
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

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
//        intent.setType(DocumentsContract.Document.MIME_TYPE_DIR);
        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        String folderPath = resultData.getDataString().substring(7);
        //TODO handle your request here
        Log.d(TAG, "onActivityResult: floderPath = " + folderPath.toString());
        initializeAudioNameAndPath(folderPath);
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void initializeAudioNameAndPath(String path) {
        audioList.add(new Audio(path.substring(path.lastIndexOf('/')+1), path));
        for (Audio a : audioList) {
            Log.d(TAG, "initializeAudioNameAndPath: Audio="+a);
        }
        Log.d(TAG, "initializeAudioNameAndPath: name = " + path.substring(path.lastIndexOf('/') +
                1));
        mediaAdapter.notifyItemInserted(audioList.size() - 1);
    }
}
