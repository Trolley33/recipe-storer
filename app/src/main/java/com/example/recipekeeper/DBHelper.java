package com.example.recipekeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLInput;
import java.util.ArrayList;
import java.util.logging.Filter;

public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "recipe.db";

    // For filtering favourites on the main screen.
    public enum FILTER {ALL, FAVOURITES};

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 9);
        Recipe.db = this;
        Category.db = this;
        Ingredient.db = this;
        Method.db = this;
    }

    /**
     * Creates tables for database.
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

    /* ---- Recipe ---- */

    /**
     * When a new recipe is created (from the homescreen)
     * @param name of recipe
     * @param overview if supplied (usually blank).
     * @return if row was inserted correctly.
     */
    boolean createNewRecipe(String name, String overview)
    {
        return createNewRecipe(name, overview, 0);
    }
    /**
     * When a new recipe is created (from the homescreen)
     * @param name of recipe
     * @param overview if supplied (usually blank).
     * @param fav if recipe is a favourite.
     * @return if row was inserted correctly.
     */
    boolean createNewRecipe(String name, String overview, int fav)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int pos = 0;
        Cursor cursor = db.rawQuery("SELECT MAX(POSITION) FROM recipes", null);
        if (cursor.moveToFirst())
        {
            pos = cursor.getInt(0) + 1;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("OVERVIEW", "");
        contentValues.put("FAVOURITE", fav);
        contentValues.put("POSITION", pos);
        long result = db.insert("recipes", null, contentValues);

        return result != -1;
    }

    public Cursor getRecipeList(Enum filter)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if (filter == FILTER.ALL)
        {
            return db.rawQuery("SELECT * FROM recipes ORDER BY POSITION", null);
        }
        if (filter == FILTER.FAVOURITES)
        {
            return db.rawQuery("SELECT * FROM recipes WHERE FAVOURITE='1' ORDER BY POSITION", null);
        }

        return null;
    }

    public void updateRecipe(int id, String name, String overview, int favourite, int position)
    {
        SQLiteDatabase db  = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("OVERVIEW", overview);
        contentValues.put("FAVOURITE", favourite);
        contentValues.put("POSITION", position);
        db.update("recipes", contentValues, "ID="+id, null);
    }

    public void deleteRecipe(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("recipes", "ID="+id, null);
        db.delete("ingredients", "RECIPE_ID="+id, null);
        db.delete("methods", "RECIPE_ID="+id, null);
        db.delete("recipe_category", "RECIPE_ID="+id, null);
    }

    /* ---- Categories ----*/

    boolean createNewCategory(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        long result = db.insert("categories", null, contentValues);

        return result != -1;
    }

    public Cursor getCategoryList()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM categories ORDER BY NAME", null);
    }

    public void deleteCategory(int category_id, Recipe[] associated_recipes)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("categories", "ID="+category_id, null);

        for (Recipe r : associated_recipes)
        {
            removeCategoryFromRecipe(r.getID(), category_id);
        }
    }

    /* ---- Category Recipe ---- */
    public Cursor getCategoryRecipeList ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("" +
                        "SELECT categories.ID as cID, categories.NAME as cName, recipes.ID as rID, recipes.NAME as rName FROM categories " +
                        "JOIN recipe_category " +
                        "ON (recipe_category.CATEGORY_ID = categories.ID) " +
                        "JOIN recipes " +
                        "ON (recipe_category.RECIPE_ID = recipes.ID) ",
                null);
    }

    public Cursor getCategoryRecipeList (int category_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("" +
                        "SELECT recipes.ID, recipes.NAME FROM categories " +
                        "JOIN recipe_category " +
                        "ON (recipe_category.CATEGORY_ID = categories.ID) " +
                        "JOIN recipes " +
                        "ON (recipe_category.RECIPE_ID = recipes.ID) " +
                        "WHERE categories.ID="+category_id,
                null);
    }

    public boolean addCategoryToRecipe (int recipe_id, int category_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM recipe_category WHERE RECIPE_ID="+recipe_id+" AND CATEGORY_ID="+category_id, null);
        if (c.getCount() != 0)
        {
            c.close();
            return false;
        }

        c.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put("RECIPE_ID", recipe_id);
        contentValues.put("CATEGORY_ID", category_id);
        long result = db.insert("recipe_category", null, contentValues);

        db.close();
        return result != -1;
    }

    public void removeCategoryFromRecipe(int recipe_id, int category_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("recipe_category", "RECIPE_ID="+recipe_id+" AND CATEGORY_ID="+category_id, null);
    }

    /* ---- Ingredients ---- */

    boolean createNewIngredient(int recipe_id, String desc, String amount)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        int pos = 0;
        Cursor cursor = db.rawQuery("SELECT MAX(POSITION) FROM ingredients WHERE RECIPE_ID="+recipe_id, null);
        if (cursor.moveToFirst())
        {
            pos = cursor.getInt(0) + 1;
        }

        contentValues.put("RECIPE_ID", recipe_id);
        contentValues.put("DESCRIPTION", desc);
        contentValues.put("AMOUNT", amount);
        contentValues.put("POSITION", pos);
        long result = db.insert("ingredients", null, contentValues);

        return result != -1;
    }

    public void updateIngredient(int id, int recipe_id, String desc, String amount, int position)
    {
        SQLiteDatabase db  = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("DESCRIPTION", desc);
        contentValues.put("AMOUNT", amount);
        contentValues.put("POSITION", position);
        db.update("ingredients", contentValues, "ID="+id, null);
    }

    public Cursor getIngredientList(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM ingredients WHERE RECIPE_ID="+id+" ORDER BY POSITION", null);
    }

    public void deleteIngredient(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ingredients", "ID="+id, null);
    }

    /* --- Methods --- */
    boolean createNewMethod(int recipe_id, int pos, String step, double time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("RECIPE_ID", recipe_id);
        contentValues.put("POSITION", pos);
        contentValues.put("STEP", step);
        contentValues.put("TIME", time);
        long result = db.insert("methods", null, contentValues);

        return result != -1;
    }

    public void updateMethod(int id, int recipe_id, int pos, String step, double time)
    {
        SQLiteDatabase db  = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("POSITION", pos);
        contentValues.put("STEP", step);
        contentValues.put("TIME", time);
        db.update("methods", contentValues, "ID="+id, null);
    }

    public Cursor getMethodList(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM methods WHERE RECIPE_ID="+id+" ORDER BY POSITION", null);
    }

    public void deleteMethod(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("methods", "ID="+id, null);
    }
}
