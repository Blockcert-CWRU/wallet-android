package com.learningmachine.android.app.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public interface NonNullFragment {

    @NonNull
    Bundle nonNullArguments();

    @NonNull
    FragmentActivity nonNullActivity();

    @NonNull
    Context nonNullContext();
}
