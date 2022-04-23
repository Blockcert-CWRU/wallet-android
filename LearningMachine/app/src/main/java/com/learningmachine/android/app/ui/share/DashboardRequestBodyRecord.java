package com.learningmachine.android.app.ui.share;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.cert.BlockCert;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface DashboardRequestBodyRecord {

    static DashboardRequestBodyRecord.Builder builder() {
        return ImmutableDashboardRequestBodyRecord.builder();
    }

    @SerializedName("blockCert")
    BlockCert blockCert();

    @SerializedName("clientToken")
    String clientToken();

    interface Builder {

        DashboardRequestBodyRecord build();

        Builder blockCert(BlockCert blockCert);

        Builder clientToken(String clientToken);
    }
}
