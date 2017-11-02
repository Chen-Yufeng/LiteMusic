package swipedelmenu.mcxtzhang.litemusic.adapter;

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


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private List<Audio> audioList;

    public MediaAdapter(List<Audio> audioList) {
        this.audioList = audioList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMediaName;
        View mediaView;

        public ViewHolder(View itemView) {
            super(itemView);
            mediaView = itemView;
            textViewMediaName = itemView.findViewById(R.id.music_item);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent
                , false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("")
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Audio audio = audioList.get(position);
        holder.textViewMediaName.setText(audio.getTitle());
    }


    @Override
    public int getItemCount() {
        return audioList.size();
    }
}
