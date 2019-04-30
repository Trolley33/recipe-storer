package com.example.recipekeeper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

public class Ingredient {
    static Context context;
    private int id;
    private int recipe_id;
    private String description;
    private String amount;
    private int position;

    public Ingredient(int _id, int _recipe_id, String _desc, String _amount, int _pos) {
        id = _id;
        recipe_id = _recipe_id;
        description = _desc;
        amount = _amount;
        position = _pos;
    }

    public static void addIngredient(int recipe_id, String desc, String amount) {
        Uri uri = Uri.parse("content://com.example.recipekeeper.own.PROVIDER");

        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("RECIPE_ID", recipe_id);
        values.put("DESCRIPTION", desc);
        values.put("AMOUNT", amount);

        int pos = getIngredientList(recipe_id).size() + 1;
        values.put("POSITION", pos);

        resolver.insert(uri, values);
    }

    public static ArrayList<Ingredient> getIngredientList(int recipe_id) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        Uri uri = Uri.parse("content://com.example.recipekeeper.own.PROVIDER");

        ContentResolver resolver = context.getContentResolver();

        String selection = "RECIPE_ID=?";
        String[] selectionArgs = {Integer.toString(recipe_id)};


        Cursor res = resolver.query(uri, null, selection, selectionArgs, null);

        if (res == null) {
            return ingredients;
        }
        if (res.getCount() == 0) {
            return ingredients;
        }

        while (res.moveToNext()) {
            int _id = res.getInt(0);
            int _recipe_id = res.getInt(1);
            String _desc = res.getString(2);
            String _amount = res.getString(3);
            int _position = res.getInt(4);

            ingredients.add(new Ingredient(_id, _recipe_id, _desc, _amount, _position));
        }


        return ingredients;
    }

    public static Ingredient getFromID(int _id, int recipe_id) {
        for (Ingredient i : getIngredientList(recipe_id)) {
            if (i.getID() == _id) {
                return i;
            }
        }
        return null;
    }

    public int getID() {
        return id;
    }

    public int getRecipeID() {
        return recipe_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description = desc;
        updateIngredient();
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String _amount) {
        amount = _amount;
        updateIngredient();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int _pos) {
        position = _pos;
        updateIngredient();
    }

    void updateIngredient() {
        Uri uri = Uri.parse("content://com.example.recipekeeper.own.PROVIDER");

        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("RECIPE_ID", getRecipeID());
        values.put("DESCRIPTION", getDescription());
        values.put("AMOUNT", getAmount());
        values.put("POSITION", getPosition());

        String selection = "ID=?";
        String[] selectionArgs = {Integer.toString(getID())};

        resolver.update(uri, values, selection, selectionArgs);
    }

    public void delete() {
        Uri uri = Uri.parse("content://com.example.recipekeeper.own.PROVIDER");

        ContentResolver resolver = context.getContentResolver();

        String selection = "ID=?";
        String[] selectionArgs = {Integer.toString(getID())};

        resolver.delete(uri, selection, selectionArgs);
    }
}
