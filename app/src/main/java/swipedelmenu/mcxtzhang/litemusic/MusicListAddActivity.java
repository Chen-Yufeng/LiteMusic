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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import swipedelmenu.mcxtzhang.litemusic.adapter.MediaAdapter;
import swipedelmenu.mcxtzhang.litemusic.entity.Audio;
import swipedelmenu.mcxtzhang.litemusic.service.MediaPlayerService;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MusicListAddActivity extends AppCompatActivity {
    public final static String INTENT_MEDIA = "MEDIA";
    public static final String INTENT_RESULT_FOR_ARRAYLIST = "INTENT_RESULT_FOR_ARRAYLIST";
    public static final String INTENT_RESULT_FOR_NAME = "INTENT_RESULT_FOR_NAME";
    private MediaPlayerService player;
    boolean serviceBound = false;
    ArrayList<Audio> audioList = new ArrayList<>();
    private final String TAG = "@vir MusicListAddActivity";
    MediaAdapter mediaAdapter;
    AudioSetBroadcastReceiver audioSetBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list);

        if (ContextCompat.checkSelfPermission(MusicListAddActivity.this, READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicListAddActivity.this, new
                    String[]{READ_EXTERNAL_STORAGE}, 1);
        }

//        mediaAdapter.notifyItemInserted(audioList.size() - 1);
        loadAudio(new Audio("2", "/sdcard/Download/b.mp3"));
        loadAudio(new Audio("1", "/sdcard/Download/b.mp3"));
//        mediaAdapter.notifyItemInserted(audioList.size() - 1);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mediaAdapter = new MediaAdapter(audioList,this);
        recyclerView.setAdapter(mediaAdapter);


        audioSetBroadcastReceiver = new AudioSetBroadcastReceiver();
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
        unregisterReceiver(audioSetBroadcastReceiver);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            Toast.makeText(MusicListAddActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(int position) {
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra(INTENT_MEDIA, audioList);
            playerIntent.putExtra("position",position);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
            Intent playNewIntent = new Intent(MediaPlayerService.PLAY_NEW);
            playNewIntent.putExtra(INTENT_MEDIA,audioList);
            playNewIntent.putExtra("position",position);
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
            int position = intent.getIntExtra("POSITION", 0);
            playAudio(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_music_platform, menu);
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
        initializeAudioNameAndPath(folderPath);
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void initializeAudioNameAndPath(String path) {
        audioList.add(new Audio(path.substring(path.lastIndexOf('/')+1), path));
        mediaAdapter.notifyItemInserted(audioList.size() - 1);
    }

    @Override
    public void onBackPressed() {
        EditText editTextName = (EditText) findViewById(R.id.music_list_name_edit_text);
        Intent intentBack = new Intent();
        intentBack.putExtra(INTENT_RESULT_FOR_ARRAYLIST,audioList);
        if(!editTextName.getText().equals(""))
            intentBack.putExtra(INTENT_RESULT_FOR_NAME,editTextName.getText().toString());
        else
            intentBack.putExtra(INTENT_RESULT_FOR_NAME,"DefaultName");
        setResult(RESULT_OK,intentBack);
        finish();
    }
}
