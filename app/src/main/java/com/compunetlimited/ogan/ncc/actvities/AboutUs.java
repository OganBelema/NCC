package com.compunetlimited.ogan.ncc.actvities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.compunetlimited.ogan.ncc.R;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    private Action getAction() {
        return Actions.newView(getString(R.string.about_us_index), "http://nccjos.org/about-us");
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
                .setName(getString(R.string.about_us_index))
                .setUrl("http://nccjos.org/about-us")
                .build();
    }
}
