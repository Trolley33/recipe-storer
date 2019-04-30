package com.example.recipekeeper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class IngredientsContentProvider extends ContentProvider {
    private DBHelper helper;

    public IngredientsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return helper.getWritableDatabase().delete("ingredients", selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        helper.getWritableDatabase().insert("ingredients", null, values);
        return null;

    }

    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        return helper.getReadableDatabase().query("ingredients", projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return helper.getWritableDatabase().update("ingredients", values, selection, selectionArgs);
    }
}
