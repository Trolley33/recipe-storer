package com.example.recipekeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "recipe.db";
    public enum FILTER {ALL, FAVOURITES};


    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 7);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE recipes (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, OVERVIEW TEXT, FAVOURITE INT, POSITION INT)");

        db.execSQL("CREATE TABLE ingredients (ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPE_ID INT, DESCRIPTION TEXT, AMOUNT TEXT, POSITION INT)");

        db.execSQL("CREATE TABLE methods (ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPE_ID INT, POSITION INT, STEP TEXT, TIME REAL)");

        db.execSQL("CREATE TABLE categories (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT)");

        db.execSQL("CREATE TABLE recipe_category (ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPE_ID INT, CATEGORY_ID INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS ingredients");
        db.execSQL("DROP TABLE IF EXISTS methods");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS recipe_category");
        onCreate(db);
    }

    boolean createNewRecipe(String name, String overview)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("OVERVIEW", "");
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

        db.execSQL("UPDATE recipes SET NAME=?, OVERVIEW=?, FAVOURITE=?, POSITION=? WHERE ID=?", new Object[] {name, overview, favourite, position, id});
    }
}
