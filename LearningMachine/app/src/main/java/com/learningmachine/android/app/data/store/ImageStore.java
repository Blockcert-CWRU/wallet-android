package com.learningmachine.android.app.data.store;


import dagger.Provides;

public interface ImageStore extends DataStore {
     @Provides

     boolean saveImage(String uuid, String jsonData);
}