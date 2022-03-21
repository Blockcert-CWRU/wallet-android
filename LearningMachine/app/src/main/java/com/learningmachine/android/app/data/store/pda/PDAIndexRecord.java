package com.learningmachine.android.app.data.store.pda;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class PDAIndexRecord {

    public static PDAIndexRecord.Builder builder() {
        return ImmutablePDAIndexRecord.builder();
    }

    @SerializedName("certId")
    public abstract String certId();

    @SerializedName("recordId")
    public abstract String recordId();

    @SerializedName("issuerId")
    public abstract String issuerId();

    public abstract static class Builder {

        public abstract PDAIndexRecord build();

        public abstract Builder recordId(String recordId);

        public abstract Builder issuerId(String issuerId);

        public abstract Builder certId(String certId);
    }
}