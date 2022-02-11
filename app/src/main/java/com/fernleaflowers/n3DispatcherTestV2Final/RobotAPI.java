package com.fernleaflowers.n3DispatcherTestV2Final;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RobotAPI {

    @GET("names")
    Call<Names> getNames();

    @GET("attributes")
    Call<Robots> getAttributes();

    @PUT("{robotName}/paths")
    Call<String> sendPathRequest(@Path("robotName") String name, @Body PathJSON path);

}