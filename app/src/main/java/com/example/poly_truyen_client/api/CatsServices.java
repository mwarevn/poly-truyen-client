package com.example.poly_truyen_client.api;

import com.example.poly_truyen_client.models.Cats;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CatsServices {
    @GET("cats/get-all-cats")
    Call<ArrayList<Cats>> getAllCats();
}
