package com.compunetlimited.ogan.ncc.actvities;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.compunetlimited.ogan.ncc.NetworkConnectivity;
import com.compunetlimited.ogan.ncc.PaginationScrollListener;
import com.compunetlimited.ogan.ncc.R;
import com.compunetlimited.ogan.ncc.adapters.eBookAdapter;
import com.compunetlimited.ogan.ncc.gson.EbookGson.Ebook;
import com.compunetlimited.ogan.ncc.gson.EbookGson.GetEbookResult;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class eLibrary extends AppCompatActivity  {

    private static final String url = "http://nccjos.org/wp-json/wp/v2/";

    private ProgressBar progressBar;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private eBookAdapter adapter;
    private TextView errorTextView;
    private RecyclerView recyclerView;

    private GetEbookResult getEbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        setTitle("eLibrary");

        progressBar =  findViewById(R.id.e_library_loader);
        errorTextView = findViewById(R.id.tv_error_text);

        recyclerView = findViewById(R.id.activity_recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new eBookAdapter(getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).
                addConverterFactory(GsonConverterFactory.create()).build();

        getEbooks = retrofit.create(GetEbookResult.class);

        if (NetworkConnectivity.checkNetworkConnecttion(this)){
            makeNetworkRequest();
        } else {
            errorTextView.setText(getString(R.string.no_internet));
            showErrorText();
        }

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    private void makeNetworkRequest(){

        showProgressbar();
        Call<ArrayList<Ebook> > call = getEbooks.getEbook("application", currentPage);
        call.enqueue(new Callback<ArrayList<Ebook>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Ebook>> call, @NonNull Response<ArrayList<Ebook>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Ebook> results = fetchResults(response);
                    adapter.addAll(results);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);

                    TOTAL_PAGES = Integer.valueOf(response.headers().get("x-wp-totalpages"));

                    if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;

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
                System.out.println(response.code());
                System.out.println(response.errorBody());

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Ebook>> call, @NonNull Throwable t) {

                progressBar.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
                System.out.println("T" + t.getMessage());
            }
        });
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

    private List<Ebook> fetchResults(Response<ArrayList<Ebook>> response) {
        return response.body();
    }

    private void loadNextPage() {

        Call<ArrayList<Ebook> > call = getEbooks.getEbook("application", currentPage);
        call.enqueue(new Callback<ArrayList<Ebook>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Ebook>> call, @NonNull Response<ArrayList<Ebook>> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                if (response.isSuccessful()) {
                    List<Ebook> results = fetchResults(response);
                    adapter.addAll(results);
                    if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                } else {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getApplicationContext(),jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            showErrorText();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Ebook>> call, @NonNull Throwable t) {

                progressBar.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private Action getAction() {
        return Actions.newView(getString(R.string.eLibrary_index), "http://nccjos.org/library");
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
                .setName(getString(R.string.eLibrary_index))
                .setUrl("http://nccjos.org/library")
                .build();
    }
}