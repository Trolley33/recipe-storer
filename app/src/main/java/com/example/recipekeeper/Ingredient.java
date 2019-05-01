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

    private Ingredient(int _id, int _recipe_id, String _desc, String _amount, int _pos) {
        // Set member values on instantiation.
        id = _id;
        recipe_id = _recipe_id;
        description = _desc;
        amount = _amount;
        position = _pos;
    }

    /**
     * Adds new ingredient to database.
     * @param recipe_id of recipe to add ingredient to.
     * @param desc of ingredient.
     * @param amount of ingredient.
     */
    static void addIngredient(int recipe_id, String desc, String amount) {
        // Create URI for content provider.
        Uri uri = Uri.parse("content://com.example.recipekeeper.own.PROVIDER");

        // Get content resolver.
        ContentResolver resolver = context.getContentResolver();

        // Insert values into object.
        ContentValues values = new ContentValues();
        values.put("RECIPE_ID", recipe_id);
        values.put("DESCRIPTION", desc);
        values.put("AMOUNT", amount);

        // Get last position of ingredient list.
        int pos = getIngredientList(recipe_id).size() + 1;
        values.put("POSITION", pos);

        // Insert data into database.
        resolver.insert(uri, values);
    }

    /**
     * Retrieves list of all ingredients for a given recipe from database.
     * @param recipe_id of recipe to get ingredients from.
     * @return {@link ArrayList<Ingredient>} of ingredients.
     */
    static ArrayList<Ingredient> getIngredientList(int recipe_id) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        // Create URI for content provider.
        Uri uri = Uri.parse("content://com.example.recipekeeper.own.PROVIDER");

        // Get content resolver.
        ContentResolver resolver = context.getContentResolver();

        // Set selection query.
        String selection = "RECIPE_ID=?";
        String[] selectionArgs = {Integer.toString(recipe_id)};


        // Retrieve cursor containing all matching ingredients from resolver.
        Cursor res = resolver.query(uri, null, selection, selectionArgs, null);

        // Query failed, return blank list.
        if (res == null) {
            return ingredients;
        }
        // Query empty, return blank list.
        if (res.getCount() == 0) {
            return ingredients;
        }

        // Loop over each row.
        while (res.moveToNext()) {
            // Add ingredient object to list.
            int _id = res.getInt(0);
            int _recipe_id = res.getInt(1);
            String _desc = res.getString(2);
            String _amount = res.getString(3);
            int _position = res.getInt(4);

            ingredients.add(new Ingredient(_id, _recipe_id, _desc, _amount, _position));
        }

        res.close();

        // Return list.
        return ingredients;
    }

    public int getID() {
        return id;
    }

    private int getRecipeID() {
        return recipe_id;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String desc) {
        description = desc;
        updateIngredient();
    }

    String getAmount() {
        return amount;
    }

    void setAmount(String _amount) {
        amount = _amount;
        updateIngredient();
    }

    private int getPosition() {
        return position;
    }

    void setPosition(int _pos) {
        position = _pos;
        updateIngredient();
    }

    /**
     * Updates ingredient in database with current values.
     */
    private void updateIngredient() {
        // Create URI for content provider.
        Uri uri = Uri.parse("content://com.example.recipekeeper.own.PROVIDER");

        // Get content resolver.
        ContentResolver resolver = context.getContentResolver();
        // Add new values to object.
        ContentValues values = new ContentValues();
        values.put("RECIPE_ID", getRecipeID());
        values.put("DESCRIPTION", getDescription());
        values.put("AMOUNT", getAmount());
        values.put("POSITION", getPosition());

        // Set update parameters.
        String selection = "ID=?";
        String[] selectionArgs = {Integer.toString(getID())};

        // Update row with new information.
        resolver.update(uri, values, selection, selectionArgs);
    }

    /**
     * Deletes ingredient from database.
     */
    public void delete() {
        // Create URI for content provider.
        Uri uri = Uri.parse("content://com.example.recipekeeper.own.PROVIDER");

        // Get content resolver.
        ContentResolver resolver = context.getContentResolver();

        // Set deletion parameters.
        String selection = "ID=?";
        String[] selectionArgs = {Integer.toString(getID())};

        // Delete row from database.
        resolver.delete(uri, selection, selectionArgs);
    }
}
