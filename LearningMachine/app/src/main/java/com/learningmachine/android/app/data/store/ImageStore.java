package com.learningmachine.android.app.data.store;


public interface ImageStore extends DataStore {

     boolean saveImage(String uuid, String jsonData);
}