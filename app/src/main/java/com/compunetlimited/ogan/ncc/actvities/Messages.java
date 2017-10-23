package com.compunetlimited.ogan.ncc.actvities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.compunetlimited.ogan.ncc.NetworkConnectivity;
import com.compunetlimited.ogan.ncc.R;
import com.compunetlimited.ogan.ncc.adapters.VideoAdapter;
import com.compunetlimited.ogan.ncc.gson.Youtube.GetVideos;
import com.compunetlimited.ogan.ncc.gson.Youtube.Item;
import com.compunetlimited.ogan.ncc.gson.Youtube.YoutubeUploads;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Messages extends AppCompatActivity {

    private final static String url ="https://www.googleapis.com/youtube/v3/";
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private ProgressBar progressBar;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        progressBar = findViewById(R.id.e_library_loader);
        errorTextView = findViewById(R.id.tv_error_text);
        recyclerView = findViewById(R.id.activity_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (NetworkConnectivity.checkNetworkConnecttion(this)){
            makeNetworkRequest();
        } else {
            errorTextView.setText(getString(R.string.no_internet));
            showErrorText();
        }

    }

    private void makeNetworkRequest(){
        showProgressbar();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetVideos getVideos = retrofit.create(GetVideos.class);

        Call<YoutubeUploads> call = getVideos.getVideosResult();
        call.enqueue(new Callback<YoutubeUploads>() {
            @Override
            public void onResponse(@NonNull Call<YoutubeUploads> call, @NonNull Response<YoutubeUploads> response) {

                progressBar.setVisibility(View.GONE);

                try {
                    if (response.isSuccessful()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        if (response.body() != null) {
                            ArrayList<Item> itemArrayList = response.body().getItems();
                            videoAdapter = new VideoAdapter(itemArrayList);
                            recyclerView.setAdapter(videoAdapter);
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                errorTextView.setText(jObjError.getString("message"));
                                showErrorText();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<YoutubeUploads> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {

            if (NetworkConnectivity.checkNetworkConnecttion(this)){
                makeNetworkRequest();
            } else {
                errorTextView.setText(getString(R.string.no_internet));
                showErrorText();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressbar(){
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showErrorText(){
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private Action getAction() {
        return Actions.newView(getString(R.string.message_index), "http://nccjos.org/media/videos");
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
                .setName(getString(R.string.message_index))
                .setUrl("http://nccjos.org/media/videos")
                .build();
    }
}
