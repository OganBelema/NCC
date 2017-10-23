package com.compunetlimited.ogan.ncc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.compunetlimited.ogan.ncc.Event;
import com.compunetlimited.ogan.ncc.R;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, int resource, List<Event> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout, parent, false);

        }

        TextView dateTextView =  convertView.findViewById(R.id.tv_date);
        TextView eventTextView = convertView.findViewById(R.id.tv_event);

        Event message = getItem(position);

        if (message != null){
            eventTextView.setText(message.getEvent());
        }

        if (message != null){
            dateTextView.setText(message.getDate());
        }

        return convertView;
    }
}
