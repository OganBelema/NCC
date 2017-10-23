package com.compunetlimited.ogan.ncc.gson.EbookGson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by belema on 9/24/17.
 */

public interface GetEbookResult {
    @GET("media")
    Call<ArrayList<Ebook>> getEbook(@Query("media_type") String query,@Query("page") int page );
}
