package swipedelmenu.mcxtzhang.litemusic;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import swipedelmenu.mcxtzhang.litemusic.adapter.MediaAdapter;
import swipedelmenu.mcxtzhang.litemusic.dialog.TimerDialog;
import swipedelmenu.mcxtzhang.litemusic.entity.Audio;
import swipedelmenu.mcxtzhang.litemusic.service.MediaPlayerService;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MusicListPlatformActivity extends AppCompatActivity
        implements TimerDialog.TimerListener, NumberPicker.OnValueChangeListener{
    public static final String INTENT_MEDIA = "MEDIA";
    public static final String INTENT_RESULT_FOR_ARRAYLIST = "INTENT_RESULT_FOR_ARRAYLIST";
    public static final String INTENT_RESULT_FOR_NAME = "INTENT_RESULT_FOR_NAME";
    public static final int RESULT_EDIT_OK = 2017;
    public static final String INTENT_RESULT_FOR_POSITION = "INTENT_RESULT_FOR_POSITION";
    private MediaPlayerService player;
    private boolean serviceBound = false;
    private ArrayList<Audio> audioList;
    private int position;
    private final String TAG = "@MusicListAddActivity";
    private int playingFlag = 0;
    MediaAdapter mediaAdapter;
    EditText editTextName;
    SeekBar seekBar;
    Context mContext;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int total = player.getDurationInMilliseconds();
            int position = player.getCurrentPosition();
            seekBar.setProgress((int) ((position*10000.0)/total));
            if (position >= (total - 2000)) {
                switch (playingFlag) {
                    default:
                    case 0:
                        if(serviceBound)
                            player.playByList();
                        break;
                    case 1:
                        if(serviceBound)
                            player.playByRandom();
                            break;
                    case 2:
                        if(serviceBound)
                            player.skipToNext();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list_for_platform);
        mContext = this;


        if (ContextCompat.checkSelfPermission(MusicListPlatformActivity.this,
                READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicListPlatformActivity.this, new
                    String[]{READ_EXTERNAL_STORAGE}, 1);
        }

        editTextName = (EditText) findViewById(R.id.music_list_name_edit_text_p);
        Intent intent = getIntent();
        audioList = (ArrayList<Audio>) intent.getSerializableExtra("audioList");
        position = intent.getIntExtra("position", 0);
        editTextName.setText(intent.getStringExtra("name"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_p);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mediaAdapter = new MediaAdapter(audioList, this);
        recyclerView.setAdapter(mediaAdapter);

        seekBar = (SeekBar) findViewById(R.id.seek_bar_p);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player.myIsPlaying()) {
                    int duration = player.getDurationInMilliseconds();
                    int seek = (int) ((seekBar.getProgress() / 10000.0) * duration);
                    player.mySeekTo(seek);
                }
            }
        });

        Button buttonPlayAndStop = (Button) findViewById(R.id.button_start_p);
        Button buttonSeekToPrevious = (Button) findViewById(R.id.button_seek_to_previous_p);
        Button buttonSeekToNext = (Button) findViewById(R.id.button_seek_to_next_p);
        buttonPlayAndStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceBound)
                    player.pauseOrPlay();
            }
        });
        buttonSeekToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceBound)
                    player.skipToPrevious();
            }
        });
        buttonSeekToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceBound)
                    player.skipToNext();
            }
        });


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
            Toast.makeText(MusicListPlatformActivity.this, "Service Bound", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(int position) {
//        new Thread() {
//            @Override
//            public void run() {
//                MyTimer timer = new MyTimer(player.getDurationInMilliseconds(), 1000);
//                timer.start();
//
//            }
//        }.start();
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra(INTENT_MEDIA, audioList);
            playerIntent.putExtra("position", position);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
            Intent playNewIntent = new Intent(MediaPlayerService.PLAY_NEW);
            playNewIntent.putExtra(INTENT_MEDIA, audioList);
            playNewIntent.putExtra("position", position);
            sendBroadcast(playNewIntent);
        }
        startTimer();
    }

    private void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Message message=new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        }, 1000, 2000);
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

//    class MyTimer extends CountDownTimer {
//
//        /**
//         * @param millisInFuture    The number of millis in the future from the call
//         *                          to {@link #start()} until the countdown is done and
//         *                          {@link #onFinish()}
//         *                          is called.
//         * @param countDownInterval The interval along the way to receive
//         *                          {@link #onTick(long)} callbacks.
//         */
//        public MyTimer(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            Message message = new Message();
//            message.what = (int) millisUntilFinished;
//            mHandler.sendMessage(message);
//        }
//
//        @Override
//        public void onFinish() {
//
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_music_platform_with_control, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addFile_ctl:
                performFileSearch();
                return true;
            case R.id.addFolder_ctl:
                return true;
            case R.id.timer:
                showNoticeDialog();
//                show();
                return true;
            case R.id.normal:
                playingFlag = 0;
                return true;
            case R.id.circle:
                playingFlag = 2;
                return true;
            case R.id.random:
                playingFlag = 1;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.d(TAG, "onValueChange: newVal="+newVal);
    }

    public void show()
    {

        final Dialog d = new Dialog(this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.timer);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(120);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new TimerDialog();
        dialog.show(getFragmentManager(), "TimerDialog");
    }

    @Override
    public void onDialogPositiveClick(int time) {
        Log.d("@#$", "onDialogPositiveClick: time="+time);
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
        audioList.add(new Audio(path.substring(path.lastIndexOf('/') + 1), path));
        mediaAdapter.notifyItemInserted(audioList.size() - 1);
    }

    @Override
    public void onBackPressed() {
        Intent intentBack = new Intent();
        intentBack.putExtra(INTENT_RESULT_FOR_ARRAYLIST, audioList);
        intentBack.putExtra(INTENT_RESULT_FOR_POSITION, position);
        if (!editTextName.getText().equals(""))
            intentBack.putExtra(INTENT_RESULT_FOR_NAME, editTextName.getText().toString());
        else
            intentBack.putExtra(INTENT_RESULT_FOR_NAME, "DefaultName");
        //may be wrong!
        setResult(RESULT_EDIT_OK, intentBack);
        finish();
    }
}
