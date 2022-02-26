package com.learningmachine.android.app.data.error;

import androidx.annotation.StringRes;

public class ExceptionWithResourceString extends Exception {
    @StringRes
    private final int mErrorMessageResId;

    public ExceptionWithResourceString(int errorMessageResId) {
        mErrorMessageResId = errorMessageResId;
    }

    public ExceptionWithResourceString(Throwable cause, int errorMessageResId) {
        super(cause);
        mErrorMessageResId = errorMessageResId;
    }

    public int getErrorMessageResId() {
        return mErrorMessageResId;
    }
}
