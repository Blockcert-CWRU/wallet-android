package com.learningmachine.android.app.ui.share;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface DashboardShareService {
    @GET("/connect")
    String connect(@Url Url url);

    @POST("/share")
    Call<DashboardRequestBody> sendCert(@Body DashboardRequestBody cert);
}
