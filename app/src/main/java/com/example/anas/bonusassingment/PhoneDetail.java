package com.example.anas.bonusassingment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Anas on 11/11/2017.
 */

public interface PhoneDetail {
    @GET("phone")
    Call<List<Phone>> getPhoneList();

    @GET("phone/{phone}")
    Call<Phone> getphone(@Path("phone") int id);

    @POST("phone")
    @FormUrlEncoded
    Call<Phone> savePhone(@Field("phoneNo") String phoneNo,
                      @Field("name") String name
    );
    @POST("phone")
    Call<Phone> savePhone(@Body Phone phone);

    @DELETE("phone/{phone}")
    Call<Phone> deletePhone(@Path("phone") int id);

    @PUT("phone/{phone}")
    @FormUrlEncoded
    Call<Phone> editPhone(@Path("phone") int id,
                      @Field("name") String name,
                      @Field("phoneNo") String phoneNo
    );
}
