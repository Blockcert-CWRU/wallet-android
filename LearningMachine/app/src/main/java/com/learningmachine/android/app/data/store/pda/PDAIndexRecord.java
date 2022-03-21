package com.learningmachine.android.app.data.store.pda;

import org.immutables.value.Value;

@Value.Immutable
public abstract class PDAIndexRecord {

    public static PDAIndexRecord.Builder builder() {
        return ImmutablePDAIndexRecord.builder();
    }

    public abstract String certId();

    public abstract String recordId();

    public abstract String issuerId();

    public abstract static class Builder {

        public abstract PDAIndexRecord build();

        public abstract Builder recordId(String recordId);

        public abstract Builder issuerId(String issuerId);

        public abstract Builder certId(String certId);
    }
}
