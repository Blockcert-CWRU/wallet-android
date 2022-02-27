package com.learningmachine.android.app.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

public abstract class AbstractLMFragment extends Fragment implements NonNullFragment {

    @NonNull
    public Bundle nonNullArguments() {
        return Objects.requireNonNull(getArguments());
    }

    @NonNull
    public FragmentActivity nonNullActivity() {
        return Objects.requireNonNull(getActivity());
    }

    @NonNull
    public Context nonNullContext() {
        return Objects.requireNonNull(getContext());
    }
}
