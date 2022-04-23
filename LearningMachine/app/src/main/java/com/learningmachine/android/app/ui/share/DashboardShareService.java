package com.learningmachine.android.app.ui.share;

import com.learningmachine.android.app.data.cert.BlockCert;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

public interface DashboardShareService {

    @GET("/connect")
    String connect(@Url Url url);

    @POST("/share")
    Call<DashboardRequestBody> sendCert(@Body DashboardRequestBody cert);

}
