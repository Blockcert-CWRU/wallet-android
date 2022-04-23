package com.learningmachine.android.app.data.store.pda;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Set;

@Gson.TypeAdapters
@Value.Immutable
public interface PDAIndex {

    static Builder builder() {
        return ImmutablePDAIndex.builder();
    }

    @SerializedName("records")
    Set<PDAIndexRecord> records();

    interface Builder {

        PDAIndex build();

        Builder addRecord(PDAIndexRecord element);

        Builder addRecords(PDAIndexRecord... elements);

        Builder addAllRecords(Iterable<? extends PDAIndexRecord> elements);
    }
}
