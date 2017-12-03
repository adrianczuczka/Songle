package com.adrianczuczka.songle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} for the list of already guessed songs to be displayed, so the user can play them again.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    private final List<XMLParser.Song> songsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final TextView number;
        public final TextView artist;
        public final TextView title;

        public MyViewHolder(View view) {
            super(view);
            number = view.findViewById(R.id.Number);
            artist = view.findViewById(R.id.Artist);
            title = view.findViewById(R.id.Title);
        }
    }

    /**
     * Constructor for the songs adapter.
     *
     * @param songsList The list of songs to show. Should always be the list of guessed songs.
     */
    public SongsAdapter(List<XMLParser.Song> songsList) {
        this.songsList = songsList;
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
