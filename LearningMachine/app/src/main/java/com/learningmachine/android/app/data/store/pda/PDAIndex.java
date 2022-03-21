package com.learningmachine.android.app.data.store.pda;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Set;

@Gson.TypeAdapters
@Value.Immutable
public abstract class PDAIndex {

    public static Builder builder() {
        return ImmutablePDAIndex.builder();
    }

    @SerializedName("records")
    public abstract Set<PDAIndexRecord> records();

    public abstract static class Builder {

        public abstract PDAIndex build();

        public abstract Builder addRecord(PDAIndexRecord element);

        public abstract Builder addRecords(PDAIndexRecord... elements);

        public abstract Builder addAllRecords(Iterable<? extends PDAIndexRecord> elements);
    }
}
