package swipedelmenu.mcxtzhang.litemusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import swipedelmenu.mcxtzhang.litemusic.R;
import swipedelmenu.mcxtzhang.litemusic.entity.Audio;


public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private List<ArrayList<Audio>> arrayListofAudioList;
    private ArrayList<String> listName;
    private Context mContext;
    private final String TAG = "@vir MediaAdapter";
    public static final String INTENT_SHOW_PLATFORM_POSITION = "CONNECT_PLATFORM_MUSICLIST";

    public MusicListAdapter(List<ArrayList<Audio>> arrayListofAudioList, Context context,
                            ArrayList<String> listName) {
        this.arrayListofAudioList = arrayListofAudioList;
        mContext = context;
        this.listName = listName;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView musicListName;
        View musicListItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            musicListItemView = itemView;
            musicListName = itemView.findViewById(R.id.music_list_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_list_item, parent
                , false);
        final ViewHolder holder = new ViewHolder(view);
        holder.musicListItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int position = holder.getAdapterPosition();
                // TODO: 11/3/17 completeme
                Intent intent = new Intent(INTENT_SHOW_PLATFORM_POSITION);
                intent.putExtra(INTENT_SHOW_PLATFORM_POSITION,position);
                mContext.sendBroadcast(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.musicListName.setText(listName.get(position));
    }


    @Override
    public int getItemCount() {
        return arrayListofAudioList.size();
    }
}

