package swipedelmenu.mcxtzhang.litemusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import swipedelmenu.mcxtzhang.litemusic.adapter.MusicListAdapter;
import swipedelmenu.mcxtzhang.litemusic.entity.Audio;

public class MusicListActivity extends AppCompatActivity {
    private MusicListAdapter musicListAdapter;
    private ArrayList<ArrayList<Audio>> arrayListofAudioList;
    private ArrayList<String> listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //test
        arrayListofAudioList = new ArrayList<>();
        listName = new ArrayList<>();
        setContentView(R.layout.activity_music_list);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_of_music_list_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        musicListAdapter = new MusicListAdapter(arrayListofAudioList,this,listName);
        recyclerView.setAdapter(musicListAdapter);

        //what java skill is used here???
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int position = intent.getIntExtra(MusicListAdapter.INTENT_SHOW_PLATFORM_POSITION,
                        0);
                Intent intentToMusicListPlatform = new Intent(MusicListActivity.this,
                        MusicListPlatformActivity.class);
                intentToMusicListPlatform.putExtra("position",position);
                intentToMusicListPlatform.putExtra("audioList",arrayListofAudioList.get(position));
                intentToMusicListPlatform.putExtra("name",listName.get(position));
                MusicListActivity.this.startActivityForResult(intentToMusicListPlatform,233);
            }
        },new IntentFilter(MusicListAdapter.INTENT_SHOW_PLATFORM_POSITION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_music_list_platform, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.music_list_platform_add:
                Intent intentToMusicListPlatform = new Intent(MusicListActivity.this,
                        MusicListAddActivity.class);
                startActivityForResult(intentToMusicListPlatform,arrayListofAudioList.size());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果不删除super
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == arrayListofAudioList.size()) {
            if (resultCode == RESULT_OK) {
                arrayListofAudioList.add((ArrayList<Audio>) data.getSerializableExtra
                        (MusicListAddActivity
                        .INTENT_RESULT_FOR_ARRAYLIST));
                String name = data.getStringExtra(MusicListAddActivity.INTENT_RESULT_FOR_NAME);
                if (name != null) {
                    listName.add(name);
                } else {
                    listName.add(Integer.toString(arrayListofAudioList.size()));
                }
                musicListAdapter.notifyItemInserted(arrayListofAudioList.size() - 1);
            }
        } else {
            if (resultCode == MusicListPlatformActivity.RESULT_EDIT_OK) {
                int position = data.getIntExtra(MusicListPlatformActivity
                        .INTENT_RESULT_FOR_POSITION,0);
                ArrayList<Audio> audioList = (ArrayList<Audio>) data.getSerializableExtra(MusicListPlatformActivity
                        .INTENT_RESULT_FOR_ARRAYLIST);
                String name = data.getStringExtra(MusicListPlatformActivity
                        .INTENT_RESULT_FOR_NAME);
                arrayListofAudioList.remove(position);
                arrayListofAudioList.add(position,audioList);
                listName.remove(position);
                listName.add(position,name);
                musicListAdapter.notifyDataSetChanged();
            }
        }
    }
}
