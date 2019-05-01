package com.example.recipekeeper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

public class IngredientsContentProvider extends ContentProvider {
    private DBHelper helper;

    public IngredientsContentProvider() {
    }

    /**
     * Deletes an ingredient from the database.
     * @param selection which columns to filter.
     * @param selectionArgs which values to filter.
     * @return whether deletion was successful.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return helper.getWritableDatabase().delete("ingredients", selection, selectionArgs);
    }

    /**
     * Unused method.
     */
    @Override
    public String getType(@NonNull Uri uri) {return "";}

    /**
     * Insert an ingredient into the database.
     * @param values to insert for ingredient.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        helper.getWritableDatabase().insert("ingredients", null, values);
        return null;

    }

    /**
     * Called when content provider is created.
     */
    @Override
    public boolean onCreate() {
        // Create new database helper for interfacing with the database.
        helper = new DBHelper(getContext());
        return true;
    }

    /**
     * Retrieve list of ingredients matching parameters.
     * @param projection which columns to return.
     * @param selection which columns to filter.
     * @param selectionArgs which values to filter.
     * @param sortOrder how to sort rows.
     * @return cursor with list of ingredients.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        return helper.getReadableDatabase().query("ingredients", projection, selection, selectionArgs, null, null, sortOrder);
    }

    /**
     * Update an ingredient in the database.
     * @param values to update for ingredient.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return helper.getWritableDatabase().update("ingredients", values, selection, selectionArgs);
    }
}
