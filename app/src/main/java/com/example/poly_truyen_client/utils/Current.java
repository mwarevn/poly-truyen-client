package com.example.poly_truyen_client.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.poly_truyen_client.models.User;
import com.example.poly_truyen_client.socket.SocketManager;
import com.google.gson.Gson;

public class Current {

    public static Current instance;

    public Current() {
    }

    public static synchronized Current getInstance() {
        if (instance == null) {
            instance = new Current();
        }
        return instance;
    }

    public static User loggedUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("poly_comic", Context.MODE_PRIVATE);
        String userString = sharedPreferences.getString("user", "");

        User user = null;

        if(!userString.isEmpty()) {
            user = new Gson().fromJson(userString, User.class);
        }

        return user;
    }
}
