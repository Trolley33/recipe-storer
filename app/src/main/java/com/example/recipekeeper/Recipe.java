package com.example.recipekeeper;

import android.database.Cursor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String overview;
    private boolean favourite;
    private int position;

    private ArrayList<Category> categories;

    static DBHelper db;

    public Recipe (int _id, String _name, String _overview, int _fav, int _pos)
    {
        id = _id;
        name = _name;
        overview = _overview;
        favourite = (_fav == 1);
        position = _pos;
        categories = initCategories();
    }

    private ArrayList<Category> initCategories()
    {
        ArrayList<Category> output = new ArrayList<>();
        Cursor res = db.getCategoryRecipeList();

        while (res.moveToNext())
        {
            int recipe_id = res.getInt(3);
            int cat_id = res.getInt(0);
            if (id == recipe_id)
                output.add(Category.getFromID(cat_id));
        }

        return output;
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

    public boolean isFavourite()
    {
        return favourite;
    }

    public int getPosition()
    {
        return position;
    }

    public ArrayList<Category> getCategories ()
    {
        return categories;
    }

    public void toggleFavourite ()
    {
        favourite = !favourite;
        updateRecipe();
    }

    public void setPosition(int _pos)
    {
        position = _pos;
        updateRecipe();
    }

    public void setOverview(String _overview)
    {
        overview = _overview;
        updateRecipe();
    }

    void updateRecipe ()
    {
        db.updateRecipe(getID(), getName(), getOverview(), isFavourite() ? 1 : 0, getPosition());
    }

    public static void addNew(String name, String overview)
    {
        db.createNewRecipe(name, overview, 0);
    }

    public static void addNew(String name, String overview, int fav)
    {
        db.createNewRecipe(name, overview, fav);
    }

    public void delete ()
    {
        db.deleteRecipe(getID());
    }

    public static ArrayList<Recipe> getRecipeList(Enum filter)
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
            int _favourite = res.getInt(3);
            int _position = res.getInt(4);

            recipes.add(new Recipe(_id, _name, _overview, _favourite, _position));
        }


        return recipes;
    }

    public static Recipe getFromID (int _id)
    {
        for (Recipe r : getRecipeList(DBHelper.FILTER.ALL))
        {
            if (r.getID() == _id)
            {
                return r;
            }
        }
        return null;
    }
}
