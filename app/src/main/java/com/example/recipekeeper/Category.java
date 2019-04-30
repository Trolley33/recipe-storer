package com.example.recipekeeper;

import android.database.Cursor;

import java.util.ArrayList;

public class Category {
    static DBHelper db;
    private int id;
    private String name;

    public Category(int _id, String _name) {
        // Set id and category name on instantiation.
        id = _id;
        name = _name;
    }

    /**
     * Adds new category to the database.
     * @param name of new category.
     */
    public static void addNew(String name) {
        db.createNewCategory(name);
    }

    /**
     * Retrieves list of all categories from database.
     * @return {@link ArrayList<Category>} of categories.
     */
    public static ArrayList<Category> getCategoryList() {
        ArrayList<Category> categories = new ArrayList<>();

        // Retrieve cursor containing all categories from DBHelper.
        Cursor res = db.getCategoryList();
        // Query failed, return blank list.
        if (res == null) {
            return categories;
        }
        // Query empty, return blank list.
        if (res.getCount() == 0) {
            return categories;
        }
        // Loop over each row.
        while (res.moveToNext()) {
            // Add new category object to list.
            int _id = res.getInt(0);
            String _name = res.getString(1);

            categories.add(new Category(_id, _name));
        }
        // Return list.
        return categories;
    }

    /**
     * Retrieves category object from associated ID.
     * @param _id of category.
     * @return {@link Category}
     */
    public static Category getFromID(int _id) {
        // Loop over each category until one with ID is found.
        for (Category c : getCategoryList()) {
            if (c.getID() == _id) {
                return c;
            }
        }
        // Return null if not found.
        return null;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets name of this category to new string.
     * @param _name new name of category.
     */
    public void setName(String _name) {
        name = _name;
        // Update database when object is updated.
        updateCategory();
    }

    /**
     * Get list of recipes that contain this category.
     * @return {@link ArrayList<Recipe>} that have this category.
     */
    public ArrayList<Recipe> getRecipeList() {
        // Retrieve cursor of all (Category,Recipe) pairs which have this category.
        Cursor res = db.getCategoryRecipeList(getID());
        ArrayList<Recipe> output = new ArrayList<>();
        // Query failed, return blank list.
        if (res == null) {
            return output;
        }

        // Get list of all recipes in database.
        ArrayList<Recipe> allRecipes = Recipe.getRecipeList(DBHelper.FILTER.ALL);
        // Loop every all (Category, Recipe) pairs.
        while (res.moveToNext()) {
            int res_id = res.getInt(0);
            for (Recipe r : allRecipes) {
                // Find recipe object which matches the current ID.
                if (r.getID() == res_id) {
                    output.add(r);
                    break;
                }
            }
        }
        // Return list.
        return output;
    }

    /**
     * Get number of recipes in this category.
     * @return {@link Integer} number of recipes in this category.
     */
    public int getRecipeCount() {
        return getRecipeList().size();
    }

    /**
     * Update database side information about this category.
     */
    void updateCategory() {
        db.updateCategory(getID(), getName());
    }

    /**
     * Delete this category from the database.
     */
    public void delete() {
        db.deleteCategory(getID(), getRecipeList().toArray(new Recipe[0]));
    }
}
