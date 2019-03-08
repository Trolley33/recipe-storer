package com.example.recipekeeper;

import android.database.Cursor;

import java.util.ArrayList;

public class Recipe {
    private int id;
    private String name;
    private String overview;

    public Recipe (int _id, String _name, String _overview)
    {
        id = _id;
        name = _name;
        overview = _overview;
    }

    public int getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getOverview()
    {
        return overview;
    }

    public static ArrayList<Recipe> getRecipeList(DBHelper db, Enum filter)
    {
        ArrayList<Recipe> recipes = new ArrayList<>();

        Cursor res = db.getRecipeList(filter);
        if (res == null)
        {
            return recipes;
        }
        if (res.getCount() == 0)
        {
            return recipes;
        }

        while (res.moveToNext())
        {
            int _id = res.getInt(0);
            String _name = res.getString(1);
            String _overview = res.getString(2);

            recipes.add(new Recipe(_id, _name, _overview));
        }


        return recipes;
    }
}
