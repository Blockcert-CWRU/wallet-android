package com.learningmachine.android.app.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class AbstractLMFragment extends Fragment {

    /**
     * Return the {@link Bundle} the given fragment is currently associated with.
     *
     * @throws IllegalStateException if not currently associated with any arguments or if associated
     *                               only with a context.
     * @see #requireArguments()
     */
    @NonNull
    public static Bundle requireArguments(Fragment fragment) {
        Bundle arguments = fragment.getArguments();
        if (arguments == null) {
            throw new IllegalStateException("Fragment " + fragment + " not attached to any arguments.");
        }
        return arguments;
    }

    /**
     * Return the {@link Bundle} this fragment is currently associated with.
     *
     * @throws IllegalStateException if not currently associated with any arguments or if associated
     *                               only with a context.
     * @see #getArguments()
     */
    @NonNull
    public final Bundle requireArguments() {
        return requireArguments(this);
    }
}
