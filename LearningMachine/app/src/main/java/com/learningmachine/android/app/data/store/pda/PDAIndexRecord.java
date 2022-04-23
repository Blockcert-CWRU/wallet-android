package com.learningmachine.android.app.data.store.pda;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PDAIndexRecord {

    static PDAIndexRecord.Builder builder() {
        return ImmutablePDAIndexRecord.builder();
    }

    @SerializedName("certId")
    String certId();

    @SerializedName("recordId")
    String recordId();

    @SerializedName("issuerId")
    String issuerId();

    interface Builder {

        PDAIndexRecord build();

        Builder recordId(String recordId);

        Builder issuerId(String issuerId);

        Builder certId(String certId);
    }
}