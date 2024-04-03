package com.example.poly_truyen_client.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poly_truyen_client.R;
import com.example.poly_truyen_client.adapters.ComicContentsAdapter;
import com.example.poly_truyen_client.adapters.CommentsAdapter;
import com.example.poly_truyen_client.api.ComicServices;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.models.Comment;
import com.example.poly_truyen_client.models.User;
import com.example.poly_truyen_client.socket.SocketConfig;
import com.example.poly_truyen_client.socket.SocketManager;
import com.example.poly_truyen_client.socket.SocketSingleton;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Comments extends LinearLayout {

    private ComicServices comicServices;
    private ArrayList<Comment> listComments;
    private CommentsAdapter commentsAdapter;
    private RecyclerView rvComments;
    private CardView btnSendComment;
    private EditText edCommentContent;
    private Comic comic;
    private Socket socket;
    private SharedPreferences sharedPreferences;

    public Comments(Context context, Comic comic) {
        super(context);
        this.comic = comic;
        initView(context);
    }

    public Comments(Context context) {
        super(context);
        initView(context);
    }

    public Comments(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public Comments(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setRecyclerViewHeight(RecyclerView recyclerView) {
        int totalHeight = 0;
        for (int i = 0; i <= recyclerView.getAdapter().getItemCount(); i++) {
            View listItem = recyclerView.getAdapter().onCreateViewHolder(recyclerView, recyclerView.getAdapter().getItemViewType(i)).itemView;
            listItem.measure(0, 0);
            totalHeight += (listItem.getMeasuredHeight() + 50);
        }
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight + (recyclerView.getPaddingTop() + recyclerView.getPaddingBottom());
        recyclerView.setLayoutParams(params);
    }

    void fetchComments(Context context) {
        ArrayList<Comment> listCmt = new ArrayList<>();
        Call<ArrayList<Comment>> callComments = comicServices.getCommentsOfComic(comic.get_id());
        callComments.enqueue(new Callback<ArrayList<Comment>>() {
            @Override
            public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listCmt.addAll(response.body());
                    Collections.reverse(listCmt);
//                    commentsAdapter.updateList(listCmt);
                    rvComments.setAdapter(new CommentsAdapter(listCmt, context));
                    rvComments.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    setRecyclerViewHeight(rvComments);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comment>> call, Throwable throwable) {

            }
        });

    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.comments_layout, this, true);

        SocketManager socketManager = SocketManager.getInstance(new ConnectAPI().API_URL);
        socket = socketManager.getSocket();

        sharedPreferences = context.getSharedPreferences("poly_comic", Context.MODE_PRIVATE);
        User user = new Gson().fromJson(sharedPreferences.getString("user", ""), User.class);

        listComments = new ArrayList<>();

        rvComments = findViewById(R.id.rvComments);
        btnSendComment = findViewById(R.id.btnSendComment);
        edCommentContent = findViewById(R.id.edCommentContent);

        commentsAdapter = new CommentsAdapter(new ArrayList<Comment>(), context);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        comicServices = new ConnectAPI().connect.create(ComicServices.class);

        if (comic == null) {
            return;
        }

        fetchComments(context);

        btnSendComment.setOnClickListener(v -> {
            String commentContent = edCommentContent.getText().toString().trim();

            if (commentContent.equals("")) {
                Snackbar.make(v, "Please do not empty the comment field", Snackbar.LENGTH_SHORT).show();
                return;
            }

            JsonObject commentPayload = new JsonObject();
            commentPayload.addProperty("idComic", comic.get_id());
            commentPayload.addProperty("idUser", user.get_id());
            commentPayload.addProperty("content", commentContent);

            Call<Comment> sendComment = comicServices.sendComment(commentPayload);
            sendComment.enqueue(new Callback<Comment>() {
                @Override
                public void onResponse(Call<Comment> call, Response<Comment> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        edCommentContent.setText("");
                        fetchComments(context);
                    }
                }

                @Override
                public void onFailure(Call<Comment> call, Throwable throwable) {
                }
            });

        });

        // listen event on server and refresh list comments
        socket.on("update_comment", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0].toString().equals(comic.get_id())) {
                    fetchComments(context);
                }
            }
        });

    }
}
