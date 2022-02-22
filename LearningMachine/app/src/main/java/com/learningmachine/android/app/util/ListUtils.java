package com.learningmachine.android.app.util;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ListUtils {

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static <T> Collector<T, ?, List<T>> toImmutableList() {
        return Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf);
    }
}
