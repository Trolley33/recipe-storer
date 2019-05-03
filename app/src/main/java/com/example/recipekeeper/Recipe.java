package com.example.recipekeeper;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    static DBHelper db;
    private int id;
    private String name;
    private String overview;
    private boolean favourite;
    private int position;

    public Recipe(int _id, String _name, String _overview, int _fav, int _pos) {
        // Set member values on instantiation.
        id = _id;
        name = _name;
        overview = _overview;
        favourite = (_fav == 1);
        position = _pos;
    }

    /**
     * Retrieves all recipes which 'match' a search term.
     * @param term to search for.
     * @return {@link ArrayList<Recipe>}  of matching recipes.
     */
    static List<Recipe> searchFor(String term) {
        ArrayList<Recipe> results = new ArrayList<>();
        // Search is not case sensitive.
        term = term.toLowerCase();
        // Loop over every recipe in the database.
        for (Recipe r : getRecipeList(DBHelper.FILTER.ALL)) {
            // If the term is contained within the title, add it.
            if (r.getName().toLowerCase().contains(term)) {
                results.add(r);
            } else {
                // Loop over every category for this recipe.
                for (Category c : r.getCategories()) {
                    // If the term is contained within the name, add it.
                    if (c.getName().toLowerCase().contains(term)) {
                        results.add(r);
                    }
                }
            }
        }
        // Return all added recipes.
        return results;
    }

    /**
     * Add new recipe to database.
     * @param name of recipe.
     * @param overview of recipe.
     * @param fav {0,1} representing if recipe is a favourite or not.
     */
    static void addNew(String name, String overview, int fav) {
        db.createNewRecipe(name, overview, fav);
    }

    /**
     * Retrieves a list of all recipes that match a given filter..
     * @param filter {@link DBHelper.FILTER} that represents which types of recipes should be retrieved.
     * @return {@link ArrayList<Recipe>} of recipes.
     */
    static ArrayList<Recipe> getRecipeList(Enum filter) {
        ArrayList<Recipe> recipes = new ArrayList<>();

        // Retrieve cursor containing filtered results from database.
        Cursor res = db.getRecipeList(filter);
        // Query failed, return blank list.
        if (res == null) {
            return recipes;
        }
        // Query empty, return blank list.
        if (res.getCount() == 0) {
            return recipes;
        }

        // Loop over each row.
        while (res.moveToNext()) {
            // Add Recipe object to list.
            int _id = res.getInt(0);
            String _name = res.getString(1);
            String _overview = res.getString(2);
            int _favourite = res.getInt(3);
            int _position = res.getInt(4);

            recipes.add(new Recipe(_id, _name, _overview, _favourite, _position));
        }

        // Return list.
        return recipes;
    }

    /**
     * Retrieves a Recipe object given it's database ID.
     * @param _id of recipe to retrieve.
     * @return {@link Recipe} that matches the ID.
     */
    static Recipe getFromID(int _id) {
        for (Recipe r : getRecipeList(DBHelper.FILTER.ALL)) {
            if (r.getID() == _id) {
                return r;
            }
        }
        return null;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        this.name = _name;
        updateRecipe();
    }

    String getOverview() {
        return overview;
    }

    void setOverview(String _overview) {
        overview = _overview;
        updateRecipe();
    }

    boolean isFavourite() {
        return favourite;
    }

    int getPosition() {
        return position;
    }

    void setPosition(int _pos) {
        position = _pos;
        updateRecipe();
    }

    /**
     * Retrieves all categories associated with this recipe.
     * @return {@link ArrayList<Category>} of categories.
     */
    ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        // Retrieve cursor containing all (recipe_id, category_id) pairs.
        Cursor res = db.getCategoryRecipeList();

        // Loop over results/
        while (res.moveToNext()) {
            int recipe_id = res.getInt(2);
            int cat_id = res.getInt(0);
            // If the recipe is this one, create a Category object from the ID, and add it to the list.
            if (getID() == recipe_id) {
                categories.add(Category.getFromID(cat_id));
            }
        }
        // Return list.
        return categories;
    }

    /**
     * Toggles between favourite states.
     */
    void toggleFavourite() {
        favourite = !favourite;
        updateRecipe();
    }

    /**
     * Adds category to this recipe.
     * @param category_id of category to add.
     * @return if row was successfully inserted.
     */
    boolean addCategory(int category_id) {
        return db.addCategoryToRecipe(getID(), category_id);
    }

    /**
     * Removes category from this recipe.
     * @param category_id of category to remove.
     */
    void removeCategory(int category_id) {
        db.removeCategoryFromRecipe(getID(), category_id);
    }

    /**
     * Update database side information with this recipe.
     */
    private void updateRecipe() {
        db.updateRecipe(getID(), getName(), getOverview(), isFavourite() ? 1 : 0, getPosition());
    }

    /**
     * Delete this recipe from the database.
     */
    public void delete() {
        db.deleteRecipe(getID());
    }
}
