package com.example.poly_truyen_client.api;

import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.models.Comment;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ComicServices {
    @GET("comic/get-all-comics")
    Call<ArrayList<Comic>> getAllComics();

    @GET("comic/top-popular/{top}")
    Call<ArrayList<Comic>> getTopPopular(@Path("top") String top);

    @GET("comment/comic/{id}")
    Call<ArrayList<Comment>> getCommentsOfComic(@Path("id") String id);

    @POST("comment/create")
    Call<Comment> sendComment(@Body JsonObject commentPayload);

    @DELETE("comment/delete/{id}")
    Call<Comment> deleteCommentById(@Path("id") String id);

    @PUT("comment/update/{id}")
    Call<Comment> updateCommentById(@Path("id") String id, @Body JsonObject contentPayload);

    @GET("comment/get-all")
    Call<ArrayList<Comment>> getAllComments();

}
