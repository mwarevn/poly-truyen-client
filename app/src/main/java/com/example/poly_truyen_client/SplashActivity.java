package com.example.poly_truyen_client;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.poly_truyen_client.api.AuthServices;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private LinearLayout componentAnimate;
    private SharedPreferences sharedPreferences;
    private AuthServices authServices;

    private void displayMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }, 3000); // 3000
    }

    private void displayLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }, 3000); // 3000
    }

    void signIn(JsonObject loginPayload) {
        Call<User> callLogin = authServices.signIn(loginPayload);
        callLogin.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    if (!user.getUsername().equals("")) {
                        String userString = new Gson().toJson(user);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user", userString);
                        editor.apply();
                        displayMain();
                    } else {
                        displayLogin();
                    }

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                displayLogin();
                Toast.makeText(SplashActivity.this, "ERROR, Failed to login please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        authServices = new ConnectAPI().connect.create(AuthServices.class);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        componentAnimate = findViewById(R.id.componentAnimate);
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.splash_anim);
        animatorSet.setTarget(componentAnimate);
        animatorSet.start();

        // get loggedIn user
        sharedPreferences = getSharedPreferences("poly_comic", MODE_PRIVATE);

        String loggedUser = sharedPreferences.getString("user", "");

        if (new Gson().fromJson(loggedUser, User.class) != null) {
            User user = new Gson().fromJson(loggedUser, User.class);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", user.getEmail());
            jsonObject.addProperty("password", user.getPassword());
            signIn(jsonObject);
        } else {
            displayLogin();
        }
    }
}