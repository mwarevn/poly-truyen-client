package com.example.poly_truyen_client.ui.home;

import static org.greenrobot.eventbus.EventBus.*;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.poly_truyen_client.MainActivity;
import com.example.poly_truyen_client.R;
import com.example.poly_truyen_client.ViewDetailsComicActivity;
import com.example.poly_truyen_client.adapters.AdapterViewPagerTopPopular;
import com.example.poly_truyen_client.adapters.ComicsAdapter;
import com.example.poly_truyen_client.api.CatsServices;
import com.example.poly_truyen_client.api.ComicServices;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.databinding.FragmentHomeBinding;
import com.example.poly_truyen_client.models.Cats;
import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.models.Comment;
import com.example.poly_truyen_client.models.User;
import com.example.poly_truyen_client.notifications.NotificationEvent;
import com.example.poly_truyen_client.socket.SocketConfig;
import com.example.poly_truyen_client.socket.SocketManager;
import com.example.poly_truyen_client.socket.SocketSingleton;
import com.example.poly_truyen_client.utils.DataConvertion;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private ComicsAdapter comicsAdapter;
    private ComicServices comicServices;
    private User loggedInUser;
    private ArrayList<Cats> listCats = new ArrayList<>();
    private Socket socket;
    private ArrayList<Comic> list = new ArrayList<>();
    private AdapterViewPagerTopPopular adapterViewPagerTopPopular;

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        socket = SocketManager.getInstance(new ConnectAPI().API_URL).getSocket();

        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        socket.on("changeListComic", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetchComics();
                    }
                });
            }
        });
        socket.on("ServerPostNewComic", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetchComics();
                    }
                });
            }
        });

        sharedPreferences = getActivity().getSharedPreferences("poly_comic", Context.MODE_PRIVATE);

        comicServices = new ConnectAPI().connect.create(ComicServices.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        popularAdapter = new ComicsAdapter(new ArrayList<Comic>(), getActivity(), false);
        comicsAdapter = new ComicsAdapter(new ArrayList<Comic>(), getActivity(), false);
        binding.rvComics.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvComics.setAdapter(comicsAdapter);

        adapterViewPagerTopPopular = new AdapterViewPagerTopPopular(getChildFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(adapterViewPagerTopPopular);
        binding.viewPager.setUserInputEnabled(false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, pos) -> {
            switch (pos) {
                case 0:
                    tab.setText("TOP MONTH");
                    break;
                case 1:
                    tab.setText("TOP WEEK");
                    break;
                case 2:
                    tab.setText("TOP DAY");
                    break;
            }
        });

        tabLayoutMediator.attach();
        getLoggedInUser();
        fetchComics();
        getCats();
        getListRecommend();

        binding.userSettingsNavigate.setOnClickListener(v -> {
//            Navigation.findNavController(view).navigate(R.id.navigation_settings);
        });

        ArrayList<String> listColsCount = new ArrayList<>();
        listColsCount.add("3");
        listColsCount.add("2");
        ArrayAdapter<String> colsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, listColsCount);

        binding.spinnerCountColsView.setAdapter(colsAdapter);
        binding.spinnerCountColsView.setSelection(0);
        binding.spinnerCountColsView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    comicsAdapter = new ComicsAdapter(new ArrayList<Comic>(), getActivity(), false);
                    binding.rvComics.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    binding.rvComics.setAdapter(comicsAdapter);
                    comicsAdapter.updateList(list);
                } else {
                    comicsAdapter = new ComicsAdapter(new ArrayList<Comic>(), getActivity(), true);
                    binding.rvComics.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    binding.rvComics.setAdapter(comicsAdapter);
                    comicsAdapter.updateList(list);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.spinnerCats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    comicsAdapter.updateList(list);
                    return;
                }

                ArrayList<Comic> listSortTmp = new ArrayList<>();
                for (Comic x : list) {
                    if (x.getCats() != null) {
                        if (x.getCats().get_id().equals(listCats.get(position - 1).get_id())) {
                            listSortTmp.add(x);
                        }
                    }
                }
                comicsAdapter.updateList(listSortTmp);
//                setRecyclerViewHeight(binding.rvComics, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // handle search feature
        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ArrayList<Comic> listTmp = new ArrayList<>();

                String textSearch = s.toString().trim();

                if (textSearch.equals("")) {
                    comicsAdapter.updateList(list);

                    binding.tabLayout.setVisibility(View.VISIBLE);
                    binding.viewPager.setVisibility(View.VISIBLE);
                    binding.vfPopularComics.setVisibility(View.VISIBLE);
                    binding.tvRecommendComicsTitle.setVisibility(View.VISIBLE);


                } else {
                    for (Comic x : list) {
                        if (x.getName().toLowerCase().contains(textSearch.toLowerCase())) {
                            listTmp.add(x);
                        }
                    }

                    binding.tabLayout.setVisibility(View.GONE);
                    binding.viewPager.setVisibility(View.GONE);
                    binding.vfPopularComics.setVisibility(View.GONE);
                    binding.tvRecommendComicsTitle.setVisibility(View.GONE);


                    comicsAdapter.updateList(listTmp);

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getLoggedInUser() {
        String userString = sharedPreferences.getString("user", "");
        loggedInUser = new Gson().fromJson(userString, User.class);

        binding.tvEmail.setText(loggedInUser.getUsername());
        binding.tvName.setText(loggedInUser.getFullName());

        if (loggedInUser.getRole() == null || !loggedInUser.getRole().equals("admin")) {
            binding.ivIconVerify.setVisibility(View.GONE);
        }

        if (loggedInUser.getAvatar() != null && !loggedInUser.getAvatar().equals("")) {
            Picasso.get().load(new ConnectAPI().API_URL + "images/" + loggedInUser.getAvatar()).into(binding.userAvatar);
        } else {
            Picasso.get().load(new ConnectAPI().API_URL + "images/" + "avatar-placeholder.png").into(binding.userAvatar);
        }
    }

    private void getCats() {
        // get list cats
        ArrayList<Cats> listCatsTmp = new ArrayList<>();
        CatsServices catsServices = new ConnectAPI().connect.create(CatsServices.class);
        Call<ArrayList<Cats>> arrayListCatsCall = catsServices.getAllCats();
        arrayListCatsCall.enqueue(new Callback<ArrayList<Cats>>() {
            @Override
            public void onResponse(Call<ArrayList<Cats>> call, Response<ArrayList<Cats>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listCats.clear();
                    listCats.addAll(response.body());
                    listCatsTmp.addAll(response.body());

                    ArrayList<String> listCatsName = new ArrayList<>();
                    listCatsName.add("Tất cả");
                    for (Cats x : listCatsTmp) {
                        listCatsName.add(x.getName());
                    }

                    ArrayAdapter<String> catsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, listCatsName);

                    binding.spinnerCats.setAdapter(catsAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Cats>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "ERROR, Failed to get all category!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void fetchComics() {

        // get list comic
        ArrayList<Comic> listTmp = new ArrayList<>();

        Call<ArrayList<Comic>> getAllcomics = comicServices.getAllComics();
        getAllcomics.enqueue(new Callback<ArrayList<Comic>>() {
            @Override
            public void onResponse(Call<ArrayList<Comic>> call, Response<ArrayList<Comic>> response) {
                if (response.isSuccessful()) {
                    listTmp.addAll(response.body());
                    Collections.reverse(listTmp);
                    list.clear();
                    list.addAll(listTmp);
                    comicsAdapter.updateList(listTmp);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comic>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "ERROR, Failed to get all comics!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    void getListRecommend() {
        Call<ArrayList<Comic>> getListComicsPopular = comicServices.getTopPopular("week");
        getListComicsPopular.enqueue(new Callback<ArrayList<Comic>>() {
            @Override
            public void onResponse(Call<ArrayList<Comic>> call, Response<ArrayList<Comic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Comic> listRecommend = response.body();
                    displayComics(listRecommend);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comic>> call, Throwable throwable) {
                Toast.makeText(getContext(), "ERROR, Failed to get TOP popular comics!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayComics(ArrayList<Comic> newList) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (Comic comic : newList) {
            View view = inflater.inflate(R.layout.popular_viewfliper_item, null);

            ImageView ivPoster = view.findViewById(R.id.ivPoster);
            TextView tvName = view.findViewById(R.id.tvName);
            TextView tvCountViews = view.findViewById(R.id.tvCountViews);
            TextView tvCountComments = view.findViewById(R.id.tvCountComments);
            TextView tvDesc = view.findViewById(R.id.tvDesc);
            CardView cvItem = view.findViewById(R.id.cvItem);

            cvItem.setOnClickListener(v -> {
                view.getContext().startActivity(new Intent(view.getContext(), ViewDetailsComicActivity.class).putExtra("comic", new Gson().toJson(comic)));
            });

            tvDesc.setText(new DataConvertion().shortDesc(comic.getDesc()));
            tvName.setText(comic.getName());
            tvCountViews.setText(comic.getViews() + " Views");
            Picasso.get().load(new ConnectAPI().API_URL + "images/" + comic.getPoster()).into(ivPoster);
            tvCountComments.setText(comic.getComments() + " Comments");

            // Add view to ViewFlipper
            binding.vfPopularComics.addView(view);
        }

        // Set animation for ViewFlipper
        binding.vfPopularComics.setInAnimation(getActivity(), android.R.anim.slide_in_left);
        binding.vfPopularComics.setOutAnimation(getActivity(), android.R.anim.slide_out_right);

        // Start flipping
        binding.vfPopularComics.setAutoStart(true);
        binding.vfPopularComics.setFlipInterval(2500);
        binding.vfPopularComics.startFlipping();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchComics();
        getLoggedInUser();
        getListRecommend();
    }
}