package com.example.poly_truyen_client.adapters;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poly_truyen_client.LoginActivity;
import com.example.poly_truyen_client.R;
import com.example.poly_truyen_client.api.ComicServices;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.models.Comment;
import com.example.poly_truyen_client.models.User;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private ArrayList<Comment> list = new ArrayList<>();
    private Context context;
    private SharedPreferences sharedPreferences;
    private ComicServices comicServices = new ConnectAPI().connect.create(ComicServices.class);

    public CommentsAdapter(ArrayList<Comment> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void updateList(ArrayList<Comment> listUpdate) {
        this.list = listUpdate;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false));
    }

    private void handleEditCommentDialog(Dialog dialog, Comment comment, String role){

        CardView dialogView = dialog.findViewById(R.id.dialog_comment_edit);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        EditText edComment = dialog.findViewById(R.id.edCommentContent);
        edComment.setText(comment.getContent());

        if (role == null) {
            edComment.setEnabled(false);
            btnSave.setVisibility(Button.GONE);
        }

        // Button exit
        dialog.findViewById(R.id.btnExit).setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Button delete
        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();

            Dialog confirmDialog = new Dialog(context);
            confirmDialog.setContentView(R.layout.dialog_action_confirmation);

            confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            confirmDialog.findViewById(R.id.textBtnCancel).setOnClickListener(vClick -> {
                confirmDialog.dismiss();
            });

            confirmDialog.findViewById(R.id.textBtnConfirm).setOnClickListener(vClick -> {
                Call<Comment> call = comicServices.deleteCommentById(comment.get_id());
                call.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                list.remove(comment);
                                notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable throwable) {
                    }
                });

                confirmDialog.dismiss();
                dialog.dismiss();
            });

            confirmDialog.show();

        });

        // Button update
        btnSave.setOnClickListener(v -> {
            String content = edComment.getText().toString().trim();

            if (content.equals("")) {
                Snackbar.make(v, "Please do not leave the comment field blank!", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (content.equals(comment.getContent())) {
                dialog.dismiss();
                return;
            }

            JsonObject contentPayload = new JsonObject();
            contentPayload.addProperty("content", content);
            Call<Comment> call = comicServices.updateCommentById(comment.get_id(), contentPayload);
            call.enqueue(new Callback<Comment>() {
                @Override
                public void onResponse(Call<Comment> call, Response<Comment> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        comment.setContent(content);
                        list.get(list.indexOf(comment)).setContent(content);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Comment> call, Throwable throwable) {
                    dialog.dismiss();
                }
            });


        });

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {

        sharedPreferences = context.getSharedPreferences("poly_comic", Context.MODE_PRIVATE);
        User loggedUser = new Gson().fromJson(sharedPreferences.getString("user", ""), User.class);

        Comment comment = list.get(holder.getAdapterPosition());

        if (comment.getIdUser() == null || new Gson().toJson(comment.getIdUser()).equals("")) {
            holder.tvMemberName.setText("Deleted account");
            holder.tvMemberName.setTextColor(ContextCompat.getColor(context, R.color.gray));

        } else {

            holder.tvMemberName.setText(comment.getIdUser().getFullName());

            if (comment.getIdUser().getRole() != null && comment.getIdUser().getRole().equals("admin")) {
                // show verify icon for admin
                holder.ivVerifyAdmin.setVisibility(ImageView.VISIBLE);

                // Setting padding programmatically
                int paddingInPx = context.getResources().getDimensionPixelSize(R.dimen.text_padding); // Get padding in pixels
                int paddingH = context.getResources().getDimensionPixelSize(R.dimen.text_paddingH); // Get padding in pixels

                holder.layoutHighlightIsAdmin.setPadding(paddingH, 0, paddingH, paddingInPx);

                // Set margin for the item layout params (if using LinearLayout as root)
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.layoutHighlightIsAdmin.getLayoutParams();
                int marginInPx = context.getResources().getDimensionPixelSize(R.dimen.text_margin); // Get margin in pixels
                layoutParams.setMargins(marginInPx, marginInPx, marginInPx, 0);
                holder.layoutHighlightIsAdmin.setLayoutParams(layoutParams);

                holder.layoutHighlightIsAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

            }

            if (comment.getIdUser().getAvatar() != null && !comment.getIdUser().getAvatar().isEmpty()) {
                Picasso.get().load(new ConnectAPI().API_URL + "images/" + comment.getIdUser().getAvatar()).into(holder.ivAvatarMember);
            } else {
                Picasso.get().load(new ConnectAPI().API_URL + "images/" + "avatar-placeholder.png").into(holder.ivAvatarMember);
            }
        }

        holder.tvCommentContent.setText(comment.getContent());
        holder.tvCommentAt.setText(new DataConvertion().date(comment.getCreatedAt()));

        holder.commentItem.setOnLongClickListener(v -> {

            // create dialog
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_edit_comment);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // full access to comment by self

            if (comment.getIdUser() != null) {
                if (loggedUser.get_id().equals(comment.getIdUser().get_id())) {
                    handleEditCommentDialog(dialog, comment, "full");
                    dialog.show();
                    return false;
                }
            }



            // delete access only by admin
            if (loggedUser.getRole() != null && loggedUser.getRole().equals("admin")) {
                handleEditCommentDialog(dialog, comment, null);
                dialog.show();
                return false;
            }

            Snackbar.make(v, "You do not have access to other members' comments!", Snackbar.LENGTH_SHORT).show();
            return false;
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout commentItem;
        private ImageView ivAvatarMember, ivVerifyAdmin;
        private TextView tvMemberName;
        private TextView tvCommentContent;
        private LinearLayout layoutHighlightIsAdmin;
        private TextView tvCommentAt;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivVerifyAdmin = itemView.findViewById(R.id.ivVerifyAdmin);
            commentItem = itemView.findViewById(R.id.commentItem);
            ivAvatarMember = (ImageView) itemView.findViewById(R.id.ivAvatarMember);
            tvMemberName = (TextView) itemView.findViewById(R.id.tvMemberName);
            tvCommentContent = (TextView) itemView.findViewById(R.id.tvCommentContent);
            tvCommentAt = (TextView) itemView.findViewById(R.id.tvCommentAt);
            layoutHighlightIsAdmin = (LinearLayout) itemView.findViewById(R.id.layoutHighlightIsAdmin);

        }
    }
}
