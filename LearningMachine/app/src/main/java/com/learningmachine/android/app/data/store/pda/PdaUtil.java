package com.learningmachine.android.app.data.store.pda;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.stream.Stream;

public final class PdaUtil {

    private static final String RECORDS = "records";

    private PdaUtil() {
    }

    public static Map<String, String> deleteQuery(String... recordIds) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Stream.of(recordIds).forEach(recordId -> builder.put(RECORDS, recordId));
        return builder.build();
    }
}
