package com.adrianczuczka.songle;

/**
 * Created by s1550570 on 08/10/17.
 */
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.Manifest;
import java.util.List;


public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    private List<XMLParser.Song> songsList;
    static final int LOAD_KML_REQUEST = 1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView number,artist,title;
        public MyViewHolder(View view) {
            super(view);
            number = (TextView) view.findViewById(R.id.Number);
            artist = (TextView) view.findViewById(R.id.Artist);
            title = (TextView) view.findViewById(R.id.Title);
        }
    }

    public SongsAdapter(List<XMLParser.Song> songsList) {
        this.songsList = songsList;
    }

    public void addOnClick(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView v = (TextView) view.findViewById(R.id.Number);
                Log.e("GameUI", String.valueOf(v.getText()));
                Log.e("GameUI", String.valueOf(view));

            }
        });
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        XMLParser.Song song = songsList.get(position);
        holder.number.setText(song.Number);
        holder.artist.setText(song.Artist);
        holder.title.setText(song.Title);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }
}
