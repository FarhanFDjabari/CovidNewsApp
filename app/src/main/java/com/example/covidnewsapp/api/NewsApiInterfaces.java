package com.example.covidnewsapp.api;

import com.example.covidnewsapp.model.NewsModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiInterfaces {

    @GET("everything")
    Call<NewsModel> getNews(
            @Query("q") String q,
            @Query("sortBy") String sortBy,
            @Query("language") String language,
            @Query("pageSize") String pageSize,
            @Query("page") String page,
            @Query("apiKey") String apiKey
    );
}
