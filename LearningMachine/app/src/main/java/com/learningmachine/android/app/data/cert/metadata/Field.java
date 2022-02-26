package com.learningmachine.android.app.data.cert.metadata;

public class Field {
    private final String mTitle;
    private final String mValue;

    public Field(String title, String value) {
        mTitle = title;
        mValue = value;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getValue() {
        return mValue;
    }
}
