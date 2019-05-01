package com.example.recipekeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipe.db";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 9);
        // Register this helper with each model.
        Recipe.db = this;
        Category.db = this;
        Method.db = this;
        // Provide context to Ingredient (as this uses the content provider).
        Ingredient.context = context;
    }

    /**
     * Creates tables for database.
     *
     * @param db database to affect.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Recipe table has id, name, overview, isFavourite, and ordering position.
        db.execSQL("CREATE TABLE recipes (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, OVERVIEW TEXT, FAVOURITE INT, POSITION INT)");

        // Ingredients table has id, recipe_id, a description, an amount, and ordering position.
        db.execSQL("CREATE TABLE ingredients (ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPE_ID INT, DESCRIPTION TEXT, AMOUNT TEXT, POSITION INT)");

        // Method table has id, recipe_id, an ordering position, some text, and a time for that step.
        db.execSQL("CREATE TABLE methods (ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPE_ID INT, POSITION INT, STEP TEXT, TIME REAL)");

        // Category table has id, name.
        db.execSQL("CREATE TABLE categories (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT)");

        // Recipe + category table has id, recipe_id, category_id.
        db.execSQL("CREATE TABLE recipe_category (ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPE_ID INT, CATEGORY_ID INT)");
    }

    /**
     * When database is updated, remove all tables and recreate them.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS ingredients");
        db.execSQL("DROP TABLE IF EXISTS methods");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS recipe_category");
        onCreate(db);
    }

    /**
     * Creates new recipe in database.
     * @param name     of recipe
     * @param overview if supplied (usually blank).
     * @param fav      if recipe is a favourite.
     * @return whether row was inserted correctly.
     */
    boolean createNewRecipe(String name, String overview, int fav) {
        // Get the database object.
        SQLiteDatabase db = this.getWritableDatabase();

        // Position is 0 by default.
        int pos = 0;
        // Get bottom position in list.
        Cursor cursor = db.rawQuery("SELECT MAX(POSITION) FROM recipes", null);
        // If other entries exist, place new entry 1 below it (at the bottom).
        if (cursor.moveToFirst()) {
            pos = cursor.getInt(0) + 1;
        }
        cursor.close();

        // Insert recipe information into database.
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("OVERVIEW", "");
        contentValues.put("FAVOURITE", fav);
        contentValues.put("POSITION", pos);
        long result = db.insert("recipes", null, contentValues);

        // Return whether result is successful. (-1 --> error).
        return result != -1;
    }

    /**
     * Retrieve list of recipes.
     * @param filter of which recipes to receive.
     * @return cursor with recipes list.
     */
    Cursor getRecipeList(Enum filter) {
        // Get the database object.
        SQLiteDatabase db = this.getReadableDatabase();
        // Return either all recipes or just favourites based on filter.
        if (filter == FILTER.ALL) {
            return db.rawQuery("SELECT * FROM recipes ORDER BY POSITION", null);
        }
        if (filter == FILTER.FAVOURITES) {
            return db.rawQuery("SELECT * FROM recipes WHERE FAVOURITE='1' ORDER BY POSITION", null);
        }

        return null;
    }

    /**
     * Update recipe with new information.
     * @param id of recipe to update.
     * @param name to replace old name.
     * @param overview to replace old overview.
     * @param favourite to replace old favourite state.
     * @param position to replace old position.
     */
    void updateRecipe(int id, String name, String overview, int favourite, int position) {
        // Get the database object.
        SQLiteDatabase db = this.getWritableDatabase();

        // Update information with new values.
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("OVERVIEW", overview);
        contentValues.put("FAVOURITE", favourite);
        contentValues.put("POSITION", position);
        db.update("recipes", contentValues, "ID=" + id, null);
    }

    /**
     * Delete recipe from database.
     * @param id of recipe to delete.
     */
    void deleteRecipe(int id) {
        // Get the database object.
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete recipe, and related objects, from all tables.
        db.delete("recipes", "ID=" + id, null);
        db.delete("ingredients", "RECIPE_ID=" + id, null);
        db.delete("methods", "RECIPE_ID=" + id, null);
        db.delete("recipe_category", "RECIPE_ID=" + id, null);
    }

    /**
     * Creates new category in database.
     * @param name of new category.
     * @return whether row was inserted correctly.
     */
    boolean createNewCategory(String name) {
        // Get database object.
        SQLiteDatabase db = this.getWritableDatabase();
        // Insert category information into database.
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        long result = db.insert("categories", null, contentValues);

        // Return whether result is successful (-1 --> error).
        return result != -1;
    }

    /**
     * Retrieve list of categories.
     * @return cursor with list of categories.
     */
    Cursor getCategoryList() {
        // Get database object.
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM categories ORDER BY NAME", null);
    }

    /**
     * Update category with new information.
     * @param id of category to update.
     * @param name to replace old name.
     */
    void updateCategory(int id, String name) {
        // Get database object.
        SQLiteDatabase db = this.getWritableDatabase();

        // Update information with new values.
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        db.update("categories", contentValues, "ID=" + id, null);
    }

    /**
     * Delete category from database.
     * @param category_id of category to delete.
     * @param associated_recipes list of recipes which have this category.
     */
    void deleteCategory(int category_id, Recipe[] associated_recipes) {
        // Get database object.
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete category from database.
        db.delete("categories", "ID=" + category_id, null);

        // Remove category from all associated recipes.
        for (Recipe r : associated_recipes) {
            removeCategoryFromRecipe(r.getID(), category_id);
        }
    }

    /**
     * Retrieve list of associated categories and recipes.
     * @return list of (category, recipe) pairs.
     */
    Cursor getCategoryRecipeList() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("" +
                        "SELECT categories.ID as cID, categories.NAME as cName, recipes.ID as rID, recipes.NAME as rName FROM categories " +
                        "JOIN recipe_category " +
                        "ON (recipe_category.CATEGORY_ID = categories.ID) " +
                        "JOIN recipes " +
                        "ON (recipe_category.RECIPE_ID = recipes.ID) ",
                null);
    }

    /**
     * Retrieve list of associated recipes for a given category.
     * @param category_id of category to get recipes from.
     * @return list of recipes.
     */
    Cursor getCategoryRecipeList(int category_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("" +
                        "SELECT recipes.ID, recipes.NAME FROM categories " +
                        "JOIN recipe_category " +
                        "ON (recipe_category.CATEGORY_ID = categories.ID) " +
                        "JOIN recipes " +
                        "ON (recipe_category.RECIPE_ID = recipes.ID) " +
                        "WHERE categories.ID=" + category_id,
                null);
    }

    /**
     * Add category to recipe.
     * @param recipe_id of recipe to add category to.
     * @param category_id of category to add .
     * @return whether row was inserted successfully.
     */
    boolean addCategoryToRecipe(int recipe_id, int category_id) {
        // Get database object.
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if recipe already has this category set.
        Cursor cursor = db.rawQuery("SELECT * FROM recipe_category WHERE RECIPE_ID=" + recipe_id + " AND CATEGORY_ID=" + category_id, null);
        if (cursor.getCount() != 0) {
            cursor.close();
            return false;
        }

        cursor.close();

        // Insert recipe category pair into table.
        ContentValues contentValues = new ContentValues();
        contentValues.put("RECIPE_ID", recipe_id);
        contentValues.put("CATEGORY_ID", category_id);
        long result = db.insert("recipe_category", null, contentValues);

        // Return whether result is successful (-1 --> error).
        return result != -1;
    }

    /**
     * Remove category from recipe.
     * @param recipe_id of recipe to remove from.
     * @param category_id of category to remove.
     */
    void removeCategoryFromRecipe(int recipe_id, int category_id) {
        // Get database object.
        SQLiteDatabase db = this.getWritableDatabase();

        // Remove recipe category pair from table.
        db.delete("recipe_category", "RECIPE_ID=" + recipe_id + " AND CATEGORY_ID=" + category_id, null);
    }

    /**
     * Create new step in recipe's method.
     * @param recipe_id of recipe to add to.
     * @param pos to place step at.
     * @param step description of step.
     * @param time to complete step.
     * @return whether row was inserted correctly.
     */
    boolean createNewMethod(int recipe_id, int pos, String step, double time) {
        // Get database object.
        SQLiteDatabase db = this.getWritableDatabase();
        // Insert information into table.
        ContentValues contentValues = new ContentValues();
        contentValues.put("RECIPE_ID", recipe_id);
        contentValues.put("POSITION", pos);
        contentValues.put("STEP", step);
        contentValues.put("TIME", time);
        long result = db.insert("methods", null, contentValues);

        // Return whether result is successful (-1 --> error).
        return result != -1;
    }

    /**
     * Update step with new information.
     * @param id of step to update.
     * @param recipe_id of recipe which ingredient is in.
     * @param pos to replace old position.
     * @param step to replace old description.
     * @param time to replace old time.
     */
    void updateMethod(int id, int recipe_id, int pos, String step, double time) {
        // Get database object.
        SQLiteDatabase db = this.getWritableDatabase();

        // Update information in table.
        ContentValues contentValues = new ContentValues();
        contentValues.put("POSITION", pos);
        contentValues.put("STEP", step);
        contentValues.put("TIME", time);
        db.update("methods", contentValues, "ID=" + id, null);
    }

    /**
     * Retrieve list of steps for a recipe.
     * @param id of recipe to get steps from.
     * @return list of steps.
     */
    Cursor getMethodList(int id) {
        // Get database object.
        SQLiteDatabase db = this.getReadableDatabase();

        // Return result of query.
        return db.rawQuery("SELECT * FROM methods WHERE RECIPE_ID=" + id + " ORDER BY POSITION", null);
    }

    /**
     * Delete step from recipe method.
     * @param id of step to delete.
     */
    void deleteMethod(int id) {
        // Get database object.
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete step from database.
        db.delete("methods", "ID=" + id, null);
    }

    // For filtering favourites on the main screen.
    public enum FILTER {
        ALL, FAVOURITES
    }
}
