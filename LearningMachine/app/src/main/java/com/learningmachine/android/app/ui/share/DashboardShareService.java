package com.learningmachine.android.app.ui.share;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface DashboardShareService {

    @GET("/share")
    String connect(@Url Url url);

    @POST("/share/certificate")
    String sendCert(@Url String url, @Body String cert);

}
