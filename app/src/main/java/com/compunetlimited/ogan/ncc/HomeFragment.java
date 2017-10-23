package com.compunetlimited.ogan.ncc;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private VideoView videoView;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        videoView = view.findViewById(R.id.about_us_videoView);

        String videoPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.about_us_video;

        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.start();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }
}
