package com.example.covidnewsapp.api;

import com.example.covidnewsapp.model.StatisticModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CovidStatisticApiInterfaces {

    @GET("positif")
    Call<StatisticModel> getTotalPositif();

    @GET("meninggal")
    Call<StatisticModel> getTotalMeninggal();

    @GET("sembuh")
    Call<StatisticModel> getTotalSembuh();

}
