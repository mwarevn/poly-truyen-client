package com.example.poly_truyen_client.ui.popular_pager;

import static android.content.ContentValues.TAG;

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
import android.widget.TextView;

import com.example.poly_truyen_client.R;
import com.example.poly_truyen_client.adapters.ComicsAdapter;
import com.example.poly_truyen_client.api.ComicServices;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.models.Comic;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularFragment extends Fragment {
    private TextView tvTopType;
    private RecyclerView rvPopular;
    private ComicsAdapter comicsAdapter;
    private ArrayList<Comic> listComicsPopular = new ArrayList<>();
    private ComicServices comicServices;
    private String top;
    private String title = "";

    public PopularFragment(String top) {
        this.top = top;
    }

    public static PopularFragment newInstance(String top) {
        PopularFragment fragment = new PopularFragment(top);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popular, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comicServices = new ConnectAPI().connect.create(ComicServices.class);
        comicsAdapter = new ComicsAdapter(new ArrayList<Comic>(), getActivity(), false);

        tvTopType = view.findViewById(R.id.tvTopType);
        rvPopular = view.findViewById(R.id.rvPopular);


        if (top.equals("day")) {
            tvTopType.setText("Top truyện HOT hôm nay");
        }

        if (top.equals("week")) {
            tvTopType.setText("Top truyện HOT tuần này");
        }

        if (top.equals("month")) {
            tvTopType.setText("Top truyện HOT trong Tháng");
        }

        rvPopular.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvPopular.setAdapter(comicsAdapter);

        Call<ArrayList<Comic>> getListComicsPopular = comicServices.getTopPopular(top);
        getListComicsPopular.enqueue(new Callback<ArrayList<Comic>>() {
            @Override
            public void onResponse(Call<ArrayList<Comic>> call, Response<ArrayList<Comic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listComicsPopular.clear();
                    listComicsPopular.addAll(response.body());
                    comicsAdapter.updateList(listComicsPopular);
                    Log.d(TAG, "onResponse: anh minh 1234");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comic>> call, Throwable throwable) {

            }
        });


    }
}