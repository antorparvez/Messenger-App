package com.antor.chatapp.Fragments;

import com.antor.chatapp.Notifications.MyResponse;
import com.antor.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA4QZP8xo:APA91bHfmxPILyTHei64S3ZiT6rikHK9zwiGM2ANp7cCqXIjQ-iZbfQ12cETib03wHbOLEelnCbeuTCd1lavbXxiXsVJEISSijiAZM886Dw1o-_x9bb7dOPQtWIG95SlzDv8OKT0qt7n"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
