package com.deo.tanis.safechat.LatestMessaging;

import com.deo.tanis.safechat.Notifications.MyResponse;
import com.deo.tanis.safechat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAUbihLMs:APA91bHgG5ZD0Tf9UL4qWkOu-3v0tjiy7JESzASBh5b0YMSFBTjJxmLJ_H4b3tk80Q3zgwyqoYMaBUajynEN87NCV2uo58TJUpC9XmT69nVkJHa8Ch1iQuhrVNrMC7boldFlgYzIMGBN"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
