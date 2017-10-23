package com.compunetlimited.ogan.ncc.actvities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.compunetlimited.ogan.ncc.Media;
import com.compunetlimited.ogan.ncc.NetworkConnectivity;
import com.compunetlimited.ogan.ncc.R;
import com.compunetlimited.ogan.ncc.adapters.AudioAdapter;
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

public class AudioActivity extends AppCompatActivity {

    private AudioAdapter audioAdapter;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView mNoMediaTextView;

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Audio");

        listView = findViewById(R.id.eventListView);
        progressBar = findViewById(R.id.pb_event);
        mNoMediaTextView = findViewById(R.id.tv_no_event);
        mNoMediaTextView.setText(getResources().getString(R.string.no_audio));

        List<Media> arrayList = new ArrayList<>();
        audioAdapter = new AudioAdapter(getApplicationContext(), R.layout.audio_layout,arrayList);
        listView.setAdapter(audioAdapter);

        if (NetworkConnectivity.checkNetworkConnecttion(this)) {
            if (mDatabaseReference.getDatabase() != null) {
                progressBar.setVisibility(View.GONE);
                attachDatabaseReadListener();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            mNoMediaTextView.setText(getResources().getString(R.string.no_internet));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(audioAdapter.getItem(i).getAudio_url()));
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }



    private Action getAction() {
        return Actions.newView(getString(R.string.audio_index), "http://nccjos.org/messages");
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
                .setName(getString(R.string.audio_index))
                .setUrl("http://nccjos.org/messages")
                .build();
    }

    private void attachDatabaseReadListener() {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Media media;
                    progressBar.setVisibility(View.VISIBLE);
                    mNoMediaTextView.setVisibility(View.GONE);

                    try {
                        media = dataSnapshot.getValue(Media.class);
                        audioAdapter.add(media);
                        progressBar.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        mNoMediaTextView.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);
                    mNoMediaTextView.setVisibility(View.GONE);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    progressBar.setVisibility(View.GONE);
                    mNoMediaTextView.setVisibility(View.GONE);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);
                    mNoMediaTextView.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    progressBar.setVisibility(View.GONE);
                    mNoMediaTextView.setVisibility(View.GONE);

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
}
