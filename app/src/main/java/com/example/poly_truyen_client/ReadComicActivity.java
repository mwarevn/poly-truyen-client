package com.example.poly_truyen_client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.poly_truyen_client.adapters.ComicContentsAdapter;
import com.example.poly_truyen_client.adapters.CommentsAdapter;
import com.example.poly_truyen_client.components.Comments;
import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ReadComicActivity extends AppCompatActivity {

    public RecyclerView rvComicContents;
    private ComicContentsAdapter comicContentsAdapter;
    private ArrayList<String> listComicContents = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_comic);

        rvComicContents = findViewById(R.id.rvComicContents);

        Intent intent = getIntent();
        Comic comic = new Gson().fromJson(intent.getStringExtra("comic"), Comic.class);


        // get comments

        Comments commentsLayout = new Comments(this, comic);

        LinearLayout layout_read_comic_content = findViewById(R.id.layout_read_comic_content);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // width
                LinearLayout.LayoutParams.WRAP_CONTENT); // height
        layoutParams.setMargins(45, 35, 45, 50); // left, top, right, bottom
        commentsLayout.setLayoutParams(layoutParams);
        layout_read_comic_content.addView(commentsLayout);


        // set list contents
        listComicContents = comic.getContents();
        comicContentsAdapter = new ComicContentsAdapter(listComicContents, rvComicContents);

        rvComicContents.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvComicContents.setAdapter(comicContentsAdapter);

//        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefresh);

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                rvComicContents.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
//                rvComicContents.setAdapter(comicContentsAdapter);
//                new DataConvertion().setRecyclerViewHeight(rvComicContents);
//
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });


        findViewById(R.id.ivBackBtn).setOnClickListener(v -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}