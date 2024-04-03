package com.example.poly_truyen_client.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poly_truyen_client.R;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.models.Comment;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class ComicContentsAdapter extends RecyclerView.Adapter<ComicContentsAdapter.ViewHolderComicContentsAdapter> {

    private ArrayList<String> listContents = new ArrayList<>();
    private Context context;
    private RecyclerView recyclerView;

    public ComicContentsAdapter(ArrayList<String> listContents, RecyclerView recyclerView) {
        this.listContents = listContents;
        this.recyclerView = recyclerView;
    }

    public void appendFromLoadMore(ArrayList<String> newList) {
        listContents.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderComicContentsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderComicContentsAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.comic_content_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderComicContentsAdapter holder, int position) {
        String image = listContents.get(holder.getAdapterPosition());
        Picasso.get().load(new ConnectAPI().API_URL + "images/" + image).into(holder.ivContent);
    }


    @Override
    public int getItemCount() {
        return listContents.size();
    }

    public class ViewHolderComicContentsAdapter extends RecyclerView.ViewHolder {
        ImageView ivContent;
        public ViewHolderComicContentsAdapter(@NonNull View itemView) {
            super(itemView);

            ivContent = itemView.findViewById(R.id.ivContent);
        }
    }


}
