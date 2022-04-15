package com.learningmachine.android.app.data.store.pda;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PDAAuthResponse {

    static Builder builder() {
        return ImmutablePDAAuthResponse.builder();
    }

    @SerializedName("accessToken")
    String accessToken();

    @SerializedName("userId")
    String userId();

    interface Builder {

        PDAAuthResponse build();

        Builder accessToken(String accessToken);

        Builder userId(String userId);
    }
}
