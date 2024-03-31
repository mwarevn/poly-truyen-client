package com.example.poly_truyen_client.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectAPI {

    public String host = "172.20.10.3:3001";
    public String API_URL = "http://" + host + "/";
    public Retrofit connect = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

}
