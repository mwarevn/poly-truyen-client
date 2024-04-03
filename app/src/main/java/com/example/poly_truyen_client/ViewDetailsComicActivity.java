package com.example.poly_truyen_client;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.components.Comments;
import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class ViewDetailsComicActivity extends AppCompatActivity {

    private ImageView ivPoster;
    private TextView tvComicName;
    private TextView tvComicAuthor;
    private TextView tvComicPublicAt;
    private TextView btnReadComic;
    private TextView tvComicDesc, tvCats;
    private LinearLayout layout_details;
    private Comic comic;
    private Comments commentsLayout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_details_comic);

        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        tvComicName = (TextView) findViewById(R.id.tvComicName);
        tvComicAuthor = (TextView) findViewById(R.id.tvComicAuthor);
        tvComicPublicAt = (TextView) findViewById(R.id.tvComicPublicAt);
        btnReadComic = (TextView) findViewById(R.id.btnReadComic);
        tvComicDesc = (TextView) findViewById(R.id.tvComicDesc);
        layout_details = findViewById(R.id.layout_details);
        tvCats = findViewById(R.id.tvCats);

        Intent intent = getIntent();

        comic = new Gson().fromJson(intent.getStringExtra("comic"), Comic.class);

        Picasso.get().load(new ConnectAPI().API_URL + "images/" + comic.getPoster()).into(ivPoster);
        tvComicName.setText(comic.getName());
        tvComicAuthor.setText(comic.getAuthor());
        tvComicPublicAt.setText(new DataConvertion().date(comic.getCreatedAt()).split("- ")[1]);
        tvComicDesc.setText(comic.getDesc());

        if (comic.getCats() != null) {
            tvCats.setText(comic.getCats().getName());
        } else {
            tvCats.setText("Đang cập nhật");
        }

        loadComment(this, comic);

        btnReadComic.setOnClickListener(v -> {
            Intent intentReadComic = new Intent(ViewDetailsComicActivity.this, ReadComicActivity.class);
            intentReadComic.putExtra("comic", intent.getStringExtra("comic"));
            startActivity(intentReadComic);
        });

        findViewById(R.id.ivBackBtn).setOnClickListener(v -> {
            onBackPressed();
        });
    }

    void loadComment(Context context, Comic comic) {
        commentsLayout = new Comments(context, comic);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // width
                LinearLayout.LayoutParams.WRAP_CONTENT); // height
        layoutParams.setMargins(45, 35, 45, 50); // left, top, right, bottom
        commentsLayout.setLayoutParams(layoutParams);
        layout_details.addView(commentsLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout_details.removeView(commentsLayout);
        loadComment(this, comic);
    }

    // Xử lý sự kiện khi nút back được nhấn
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}