package com.example.ogan.ncc;


import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ogan.ncc.eBooks.eLibraryGson.EbookResult;
import com.example.ogan.ncc.eBooks.eLibraryGson.bookData.EbookAdapter;
import com.example.ogan.ncc.eBooks.eLibraryGson.bookData.GetEbooks;
import com.example.ogan.ncc.eBooks.eLibraryGson.bookData.PaginationAdapter;
import com.example.ogan.ncc.eBooks.eLibraryGson.bookData.PaginationScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class eLibrary extends AppCompatActivity  {

    private static final String url = "http://nccjos.org/wp-json/wp/v2/";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;



    LinearLayoutManager linearLayoutManager;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;
    PaginationAdapter adapter;

    GetEbooks getEbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library);
        setTitle("eLibrary");

        progressBar = (ProgressBar) findViewById(R.id.pb_books);

        recyclerView = (RecyclerView) findViewById(R.id.rv_books);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new PaginationAdapter(getApplicationContext());

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);



        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).
                addConverterFactory(GsonConverterFactory.create()).build();

        getEbooks = retrofit.create(GetEbooks.class);

        Call<ArrayList<EbookResult> > call = getEbooks.getEbookResults("application", currentPage);
        call.enqueue(new Callback<ArrayList<EbookResult>>() {
            @Override
            public void onResponse(Call<ArrayList<EbookResult>> call, Response<ArrayList<EbookResult>> response) {
                progressBar.setVisibility(View.GONE);
                 List<EbookResult> results = fetchResults(response);
               // mAdapter = new EbookAdapter(results);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;

                System.out.println(response.code());
                System.out.println(response.errorBody());

            }

            @Override
            public void onFailure(Call<ArrayList<EbookResult>> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "An error occurred while trying to get data. Please check ", Toast.LENGTH_SHORT).show();
                System.out.println("T" + t.getMessage());
            }
        });


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

    private List<EbookResult> fetchResults(Response<ArrayList<EbookResult>> response) {
        List<EbookResult> results = response.body();
        return results;
    }

    private void loadNextPage() {

        Call<ArrayList<EbookResult> > call = getEbooks.getEbookResults("application", currentPage);
        call.enqueue(new Callback<ArrayList<EbookResult>>() {
            @Override
            public void onResponse(Call<ArrayList<EbookResult>> call, Response<ArrayList<EbookResult>> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

               List<EbookResult> results = fetchResults(response);
                //mAdapter = new EbookAdapter(results);
                adapter.addAll(results);
                //recyclerView.setAdapter(mAdapter);

                System.out.println("Second request" + response.code());
                System.out.println("Second request" + response.errorBody());

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;

            }

            @Override
            public void onFailure(Call<ArrayList<EbookResult>> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "An error occured while trying to get data", Toast.LENGTH_SHORT).show();
                System.out.println("T" + t.getMessage());
            }
        });
    }


}
