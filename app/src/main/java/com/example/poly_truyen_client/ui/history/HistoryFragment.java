package com.example.poly_truyen_client.ui.history;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.poly_truyen_client.R;
import com.example.poly_truyen_client.adapters.CommentsAdapter;
import com.example.poly_truyen_client.adapters.HistoryAdapter;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.api.HistoryServices;
import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.models.User;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private HistoryServices historyServices;
    private SharedPreferences sharedPreferences;
    private LinearLayout layout_history;
    private TextView tvHistoryEmpty;
    private ArrayList<Comic> list = new ArrayList<>();
    private User LoggedUser;
    private Context mainContext;

    public HistoryFragment() {
    }
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    public void fetchCache(String idUser, Context context) {
        Call<ArrayList<Comic>> arrayListCall = historyServices.getCaches(idUser);

        arrayListCall.enqueue(new Callback<ArrayList<Comic>>() {
            @Override
            public void onResponse(Call<ArrayList<Comic>> call, Response<ArrayList<Comic>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().size() <= 0) {
                        tvHistoryEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        return;
                    }

                    recyclerView.setVisibility(View.VISIBLE);

                    ArrayList<Comic> listTmp = new ArrayList<>(response.body());
                    listTmp.clear();
                    listTmp.addAll(response.body());
                    Collections.reverse(listTmp);
                    historyAdapter.updateListComic(listTmp);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comic>> call, Throwable throwable) {
                Log.d(TAG, "onFailure: eeeeeeeeeee" );
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainContext = view.getContext();

        tvHistoryEmpty = view.findViewById(R.id.tvHistoryEmpty);
        recyclerView = view.findViewById(R.id.rvHistory);
        historyServices = new ConnectAPI().connect.create(HistoryServices.class);
        sharedPreferences = view.getContext().getSharedPreferences("poly_comic", Context.MODE_PRIVATE);
        LoggedUser = new Gson().fromJson(sharedPreferences.getString("user", ""), User.class);

        historyAdapter = new HistoryAdapter(new ArrayList<Comic>(), getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(historyAdapter);

        fetchCache(LoggedUser.get_id(), getActivity());

        view.findViewById(R.id.textBtnClearHistory).setOnClickListener(v -> {
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_action_confirmation);
            new DataConvertion().decorDialogBackground(dialog);
            dialog.findViewById(R.id.textBtnCancel).setOnClickListener(dialogClick -> {
                dialog.dismiss();
            });

            dialog.findViewById(R.id.textBtnConfirm).setOnClickListener(dialogClick -> {
                Call<JsonObject> clearCache = historyServices.clearCache(LoggedUser.get_id());
                clearCache.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ArrayList<Comic> emptyList = new ArrayList<>();
                            historyAdapter.updateListComic(emptyList);
                            recyclerView.setVisibility(View.GONE);
                            tvHistoryEmpty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {

                    }
                });
                dialog.dismiss();
            });

            dialog.show();
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        fetchCache(LoggedUser.get_id(), mainContext);

    }
}