package com.example.recipekeeper;

import android.database.Cursor;

import java.util.ArrayList;

public class Category {
    private int id;
    private String name;

    static DBHelper db;

    public Category(int _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public int getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getRecipeCount()
    {
        Cursor res = db.getCategoryRecipeList();

        if (res == null)
        {
            return 0;
        }

        int total = 0;
        while (res.moveToNext())
        {
            int cat_id = res.getInt(0);
            if (id == cat_id)
                total++;
        }

        return total;
    }

    public static void addNew(String name)
    {
        db.createNewCategory(name);
    }

    public static ArrayList<Category> getCategoryList()
    {
        ArrayList<Category> categories = new ArrayList<>();

        Cursor res = db.getCategoryList();
        if (res == null)
        {
            return categories;
        }
        if (res.getCount() == 0)
        {
            return categories;
        }

        while (res.moveToNext())
        {
            int _id = res.getInt(0);
            String _name = res.getString(1);

            categories.add(new Category(_id, _name));
        }

        return categories;
    }

    public static Category getFromID (int _id)
    {
        for (Category c : getCategoryList())
        {
            if (c.getID() == _id)
            {
                return c;
            }
        }

        return null;
    }
}
