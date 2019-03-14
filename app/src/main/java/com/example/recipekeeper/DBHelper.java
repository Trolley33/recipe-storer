package com.example.recipekeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "recipe.db";

    // For filtering favourites on the main screen.
    public enum FILTER {ALL, FAVOURITES};


    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 7);
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
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("OVERVIEW", "");
        contentValues.put("FAVOURITE", fav);
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
}
