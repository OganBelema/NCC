package com.compunetlimited.ogan.ncc.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.compunetlimited.ogan.ncc.actvities.YoutubePlayerActivity;
import com.compunetlimited.ogan.ncc.gson.Youtube.Item;
import com.compunetlimited.ogan.ncc.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by belema on 9/24/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private final ArrayList<Item> result;


    public VideoAdapter(ArrayList<Item> result){
        this.result = result;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_layout, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, final int position) {
        String imageUrl = null;


        if(result.size() != 0 && !result.isEmpty()) {

            try {
                holder.bookTitle.setText(result.get(position).getSnippet().getTitle());

                imageUrl = result.get(position).getSnippet().getThumbnails().getStandard().getUrl();

            } catch (Exception e){
                e.printStackTrace();
            }

            final String videoUrl = result.get(position).getSnippet().getResourceId().getVideoId();

            if (imageUrl != null) {
                Picasso.with(context).load(imageUrl).into(holder.imageView);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+ videoUrl)));
                    Intent intent = new Intent(context, YoutubePlayerActivity.class);
                    intent.putExtra("videoUrl", videoUrl);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        final TextView bookTitle;
        final ImageView imageView;

        public VideoViewHolder(View view){
            super(view);
            context = view.getContext();
            bookTitle = view.findViewById(R.id.tv_video_title);
            imageView = view.findViewById(R.id.video_Image);
        }

    }
}
