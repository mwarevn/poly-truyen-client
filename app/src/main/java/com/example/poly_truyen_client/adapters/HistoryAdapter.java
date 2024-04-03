package com.example.poly_truyen_client.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poly_truyen_client.R;
import com.example.poly_truyen_client.ViewDetailsComicActivity;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.api.HistoryServices;
import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.models.History;
import com.example.poly_truyen_client.models.User;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryAdapterViewHolder> {

    private HistoryServices historyServices;
    private ArrayList<Comic> list = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Context context;

    public HistoryAdapter(ArrayList<Comic> list, Context context) {
        this.list = list;
        this.context = context;
        notifyDataSetChanged();
    }

    public void updateListComic(ArrayList<Comic> listUpdate) {
        list = listUpdate;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapterViewHolder holder, int position) {
        sharedPreferences = context.getSharedPreferences("poly_comic", Context.MODE_PRIVATE);
        User loggedUser = new Gson().fromJson(sharedPreferences.getString("user", ""), User.class);

        historyServices = new ConnectAPI().connect.create(HistoryServices.class);

        Comic comic = list.get(holder.getAdapterPosition());

        holder.history_item.setOnClickListener(v -> {

            Intent intent = new Intent(context, ViewDetailsComicActivity.class);
            intent.putExtra("comic", new Gson().toJson(comic));
            context.startActivity(intent);

        });

        holder.tvName.setText(comic.getName());
        holder.tvAuthor.setText(comic.getAuthor());
        holder.tvCreatedAt.setText(new DataConvertion().date(comic.getCreatedAt()));
        Picasso.get().load(new ConnectAPI().API_URL + "images/" + comic.getPoster()).into(holder.ivPoster);

        holder.btnRemoveHistory.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_action_confirmation);
            dialog.findViewById(R.id.textBtnCancel).setOnClickListener(vClick -> {
                dialog.dismiss();
            });
            new DataConvertion().decorDialogBackground(dialog);

            dialog.findViewById(R.id.textBtnConfirm).setOnClickListener(vClick -> {
                Call<History> deleteCache = historyServices.deleteHistory(loggedUser.get_id(), comic.get_id());
                deleteCache.enqueue(new Callback<History>() {
                    @Override
                    public void onResponse(Call<History> call, Response<History> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            list.remove(comic);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<History> call, Throwable throwable) {

                    }
                });
                dialog.dismiss();
            });
            dialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HistoryAdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPoster;
        private TextView tvName;
        private TextView tvAuthor;
        private TextView tvCreatedAt;
        private LinearLayout btnRemoveHistory, history_item;

        public HistoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPoster = (ImageView) itemView.findViewById(R.id.ivPoster);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
            btnRemoveHistory = (LinearLayout) itemView.findViewById(R.id.btnRemoveHistory);
            history_item = (LinearLayout) itemView.findViewById(R.id.history_item);

        }
    }
}
