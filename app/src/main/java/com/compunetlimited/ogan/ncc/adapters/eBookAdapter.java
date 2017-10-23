package com.compunetlimited.ogan.ncc.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.compunetlimited.ogan.ncc.actvities.PdfView;
import com.compunetlimited.ogan.ncc.gson.EbookGson.Ebook;
import com.compunetlimited.ogan.ncc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by belema on 9/24/17.
 */

public class eBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;


    private final List<Ebook> itemList;
    private Context context;
    private static final String BOOK_URL = "bookUrl";
    private static final String BOOK_NAME = "bookName";

    private boolean isLoadingAdded = false;

    public eBookAdapter(Context context) {
        this.context = context;
        itemList = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.loading_layout, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.ebook_layout, parent, false);
        viewHolder = new UserViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,  int position) {

        switch (getItemViewType(position)) {
            case ITEM:
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                final String bookName = itemList.get(position).getTitle().getRendered();
                final String uppercaseBookName = bookName.substring(0, 1).toUpperCase() + bookName.substring(1);
                final String url = itemList.get(position).getSourceUrl();
                userViewHolder.userName.setText(uppercaseBookName);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PdfView.class);
                        intent.putExtra(BOOK_URL, url);
                        intent.putExtra(BOOK_NAME, uppercaseBookName);
                        context.startActivity(intent);
                    }
                });
                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == itemList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    private void add(Ebook result) {
        itemList.add(result);
        notifyItemInserted(itemList.size() - 1);
    }

    public void addAll(List<Ebook> results) {
        for (Ebook result : results) {
            add(result);
        }
    }

    private void remove(Ebook result) {
        int position = itemList.indexOf(result);
        if (position > -1) {
            itemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Ebook());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = itemList.size() - 1;
        Ebook result = getItem(position);

        if (result != null) {
            itemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Ebook getItem(int position) {
        return itemList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */


    class UserViewHolder extends RecyclerView.ViewHolder{

        final TextView userName;



        public UserViewHolder(View view){
            super(view);
            context = view.getContext();
            userName = view.findViewById(R.id.tv_book_title);
        }
    }


    class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}
