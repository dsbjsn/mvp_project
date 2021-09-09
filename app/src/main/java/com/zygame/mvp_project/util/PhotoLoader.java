package com.zygame.mvp_project.util;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2021/8/14 12
 *
 * @author xjl
 */
public class PhotoLoader implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private LoadFinishCallback loadFinishCallback;

    public void setLoadFinishCallback(LoadFinishCallback loadFinishCallback) {
        this.loadFinishCallback = loadFinishCallback;
    }

    public interface LoadFinishCallback {
        void wholeImage(List<String> imageWhole);
    }

    private final String[] PARAMS_IMAGE = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID
    };
    public FragmentActivity activity;

    public PhotoLoader(FragmentActivity activity) {
        this.activity = activity;
        activity.getLoaderManager().initLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new Loader<>(activity);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> pLoader, Cursor pCursor) {
        if (pCursor == null) {
            return;
        }
        List<String> wholeImages = new ArrayList<>();
        int photoCount = pCursor.getCount();
        if (photoCount > 0) {
            pCursor.moveToFirst();
            do {
                String photoPath = pCursor.getString(pCursor.getColumnIndexOrThrow(PARAMS_IMAGE[0]));
                wholeImages.add(photoPath);
            } while (pCursor.moveToNext());
        }
        //调用接口
        loadFinishCallback.wholeImage(wholeImages);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> pLoader) {

    }
}
