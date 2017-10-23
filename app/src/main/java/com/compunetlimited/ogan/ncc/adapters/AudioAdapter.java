package com.compunetlimited.ogan.ncc.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.compunetlimited.ogan.ncc.Media;
import com.compunetlimited.ogan.ncc.R;

import java.util.List;

/**
 * Created by belema on 10/4/17.
 */

public class AudioAdapter extends ArrayAdapter<Media> {

    public AudioAdapter(@NonNull Context context, @LayoutRes int resource, List<Media> medias) {
        super(context, resource, medias);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_layout, parent, false);

        }

        TextView audioTitle = convertView.findViewById(R.id.audio_title);

        Media media = getItem(position);

        if (media != null){
            audioTitle.setText(media.getAudio_name());
        }

        return convertView;
    }


}