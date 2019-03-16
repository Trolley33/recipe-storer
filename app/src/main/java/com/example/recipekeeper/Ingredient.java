package com.example.recipekeeper;

import android.database.Cursor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Ingredient {
    private int id;
    private int recipe_id;
    private String description;
    private String amount;
    private int position;

    static DBHelper db;

    public Ingredient (int _id, int _recipe_id, String _desc, String _amount, int _pos)
    {
        id = _id;
        recipe_id = _recipe_id;
        description = _desc;
        amount = _amount;
        position = _pos;
    }

    public int getID()
    {
        return id;
    }

    public int getRecipeID()
    {
        return recipe_id;
    }

    public String getDescription()
    {
        return description;
    }

    public String getAmount()
    {
        return amount;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int _pos)
    {
        position = _pos;
        updateIngredient();
    }

    public void setDescription(String desc)
    {
        description = desc;
        updateIngredient();
    }

    public void setAmount(String _amount)
    {
        amount = _amount;
        updateIngredient();
    }

    void updateIngredient ()
    {
        db.updateIngredient(getID(), getRecipeID(), getDescription(), getAmount(), getPosition());
    }

    public static void addIngredient(int recipe_id, String desc, String amount)
    {
        db.createNewIngredient(recipe_id, desc, amount, getIngredientList(recipe_id).size());
    }

    public void delete ()
    {
        db.deleteIngredient(getID());
    }

    public static ArrayList<Ingredient> getIngredientList(int recipe_id)
    {
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        Cursor res = db.getIngredientList(recipe_id);
        if (res == null)
        {
            return ingredients;
        }
        if (res.getCount() == 0)
        {
            return ingredients;
        }

        while (res.moveToNext())
        {
            int _id = res.getInt(0);
            int _recipe_id = res.getInt(1);
            String _desc = res.getString(2);
            String _amount = res.getString(3);
            int _position = res.getInt(4);

            ingredients.add(new Ingredient(_id, _recipe_id, _desc, _amount, _position));
        }


        return ingredients;
    }

    public static Ingredient getFromID (int _id, int recipe_id)
    {
        for (Ingredient i : getIngredientList(recipe_id))
        {
            if (i.getID() == _id)
            {
                return i;
            }
        }
        return null;
    }
}
