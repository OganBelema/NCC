package com.compunetlimited.ogan.ncc.actvities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.compunetlimited.ogan.ncc.Event;
import com.compunetlimited.ogan.ncc.NetworkConnectivity;
import com.compunetlimited.ogan.ncc.R;
import com.compunetlimited.ogan.ncc.adapters.EventAdapter;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ListView listView;
    private EventAdapter eventAdapter;
    private ProgressBar progressBar;
    private TextView mNoEventTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        setTitle(getString(R.string.event_activity_title));

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Event");

        listView = findViewById(R.id.eventListView);
        progressBar = findViewById(R.id.pb_event);
        mNoEventTextView = findViewById(R.id.tv_no_event);
        List<Event> arrayList = new ArrayList<>();
        eventAdapter = new EventAdapter(getApplicationContext(), R.layout.event_layout,arrayList);
        listView.setAdapter(eventAdapter);

        if (NetworkConnectivity.checkNetworkConnecttion(this)) {
            if (mDatabaseReference.getDatabase() != null) {
                progressBar.setVisibility(View.GONE);
                attachDatabaseReadListener();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            mNoEventTextView.setText(getResources().getString(R.string.no_internet));
            mNoEventTextView.setVisibility(View.VISIBLE);
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {

            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Event event;
                    progressBar.setVisibility(View.VISIBLE);
                    mNoEventTextView.setVisibility(View.GONE);

                    try {
                         event = dataSnapshot.getValue(Event.class);
                         eventAdapter.add(event);
                        progressBar.setVisibility(View.GONE);
                         listView.setVisibility(View.VISIBLE);
                    } catch (Exception e){
                        progressBar.setVisibility(View.GONE);
                        mNoEventTextView.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);
                    mNoEventTextView.setVisibility(View.GONE);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    progressBar.setVisibility(View.GONE);
                    mNoEventTextView.setVisibility(View.GONE);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);
                    mNoEventTextView.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    progressBar.setVisibility(View.GONE);
                    mNoEventTextView.setVisibility(View.GONE);

                }
            };

            mDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }

    private void detachDatabaseReadListener(){
        if (mChildEventListener != null){
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    private Action getAction() {
        return Actions.newView(getString(R.string.event_index), "http://nccjos.org/events");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAppIndex.getInstance().update(getIndexable());
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    protected void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

    private Indexable getIndexable(){
        return new Indexable.Builder()
                .setName(getString(R.string.event_index))
                .setUrl("http://nccjos.org/events")
                .build();
    }
}
