package com.compunetlimited.ogan.ncc.gson.Youtube;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by belema on 9/24/17.
 */

public interface GetVideos {
    @GET("playlistItems?part=snippet&maxResults=50&playlistId=UU02Nh30a0AH2KdcBu1ju03g&key=AIzaSyDMlejgd_xqJOM2Bo9wmTC3v0bi2OwFLyY")
    Call<YoutubeUploads> getVideosResult();
}
