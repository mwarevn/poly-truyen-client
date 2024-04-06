package com.example.poly_truyen_client;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.poly_truyen_client.adapters.ComicContentsAdapter;
import com.example.poly_truyen_client.adapters.CommentsAdapter;
import com.example.poly_truyen_client.adapters.EndlessRecyclerViewScrollListener;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.api.HistoryServices;
import com.example.poly_truyen_client.components.Comments;
import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.models.History;
import com.example.poly_truyen_client.models.User;
import com.example.poly_truyen_client.utils.Current;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadComicActivity extends AppCompatActivity {

    public RecyclerView rvComicContents;
    private ComicContentsAdapter comicContentsAdapter;
    private ArrayList<String> listComicContents = new ArrayList<>();
    private ImageView btnOpenComments;
    private TextView tvName;
    private HistoryServices historyServices;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_comic);

        Intent intent = getIntent();
        Comic comic = new Gson().fromJson(intent.getStringExtra("comic"), Comic.class);

        historyServices = new ConnectAPI().connect.create(HistoryServices.class);
        sharedPreferences = getSharedPreferences("poly_comic", MODE_PRIVATE);

//        User loggedUser = new Gson().fromJson(sharedPreferences.getString("user", ""), User.class);

        User loggedUser = Current.loggedUser(getApplicationContext());

        Call<History> saveCache = historyServices.storeHistory(loggedUser.get_id(), comic.get_id());
        saveCache.enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {

            }

            @Override
            public void onFailure(Call<History> call, Throwable throwable) {

            }
        });

        rvComicContents = findViewById(R.id.rvComicContents);
        btnOpenComments = findViewById(R.id.btnOpenComments);
        tvName = findViewById(R.id.tvName);

        tvName.setText(comic.getName());

        // set list contents
        listComicContents = comic.getContents();
        comicContentsAdapter = new ComicContentsAdapter(new ArrayList<>(), rvComicContents);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        rvComicContents.setLayoutManager(linearLayoutManager);
        rvComicContents.setAdapter(comicContentsAdapter);

        comicContentsAdapter.appendFromLoadMore(listComicContents);

        // create dialog show comments
        Dialog dialog = new Dialog(ReadComicActivity.this);
        dialog.setContentView(R.layout.dialog_comment_layout);
        dialog.findViewById(R.id.btnClose).setOnClickListener(closeClick -> {dialog.dismiss();});
        new DataConvertion().decorDialogBackground(dialog);

        // get comments layout
        Comments commentsLayout = new Comments(this, comic);

        LinearLayout layout_modal_show_comment = dialog.findViewById(R.id.layout_modal_show_comment);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // width
                LinearLayout.LayoutParams.WRAP_CONTENT); // height
        layoutParams.setMargins(45, 35, 45, 50); // left, top, right, bottom
        commentsLayout.setLayoutParams(layoutParams);
        layout_modal_show_comment.addView(commentsLayout);

        btnOpenComments.setOnClickListener(v -> {
            dialog.show();
        });

        findViewById(R.id.ivBackBtn).setOnClickListener(v -> {
            onBackPressed();
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}