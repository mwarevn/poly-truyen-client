package com.example.poly_truyen_client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.poly_truyen_client.api.AuthServices;
import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edEmail;
    private EditText edPassword;
    private Button btnSignIn;
    private AuthServices authServices = new ConnectAPI().connect.create(AuthServices.class);

    private View rootView;
    void showSnack (String msg) {
        Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private SharedPreferences sharedPreferences;

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
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                } else {
                    showSnack("Error, Invalid username or password.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                showSnack("Error, unknown error please try again or contact to admin.");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        rootView = findViewById(R.id.root_view_sign_in);

        sharedPreferences = getSharedPreferences("poly_comic", MODE_PRIVATE);

        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

//        String loggedUser = sharedPreferences.getString("user", "");
//
//        if (new Gson().fromJson(loggedUser, User.class) != null) {
//            User user = new Gson().fromJson(loggedUser, User.class);
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("email", user.getEmail());
//            jsonObject.addProperty("password", user.getPassword());
//
//            edEmail.setText(user.getUsername());
//            edPassword.setText(user.getPassword());
//            signIn(jsonObject);
//        }

        btnSignIn.setOnClickListener(v -> {

            String username = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();

            if (username.equals("") || password.equals("")) {
                showSnack("Error, Please fill these form!");
                return;
            }

            JsonObject loginPayload = new JsonObject();
            loginPayload.addProperty("email", username);
            loginPayload.addProperty("password", password);

            signIn(loginPayload);

        });

        findViewById(R.id.TextBtnSignUp).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
}