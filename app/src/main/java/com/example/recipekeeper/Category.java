package com.example.recipekeeper;

import android.database.Cursor;

import java.util.ArrayList;

public class Category {
    static DBHelper db;
    private int id;
    private String name;

    public Category(int _id, String _name) {
        id = _id;
        name = _name;
    }

    public static void addNew(String name) {
        db.createNewCategory(name);
    }

    public static ArrayList<Category> getCategoryList() {
        ArrayList<Category> categories = new ArrayList<>();

        Cursor res = db.getCategoryList();
        if (res == null) {
            return categories;
        }
        if (res.getCount() == 0) {
            return categories;
        }

        while (res.moveToNext()) {
            int _id = res.getInt(0);
            String _name = res.getString(1);

            categories.add(new Category(_id, _name));
        }

        return categories;
    }

    public static Category getFromID(int _id) {
        for (Category c : getCategoryList()) {
            if (c.getID() == _id) {
                return c;
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
        name = _name;
        updateCategory();
    }

    public ArrayList<Recipe> getRecipeList() {
        Cursor res = db.getCategoryRecipeList(getID());
        ArrayList<Recipe> output = new ArrayList<>();
        if (res == null) {
            return output;
        }

        ArrayList<Recipe> allRecipes = Recipe.getRecipeList(DBHelper.FILTER.ALL);
        while (res.moveToNext()) {
            int res_id = res.getInt(0);
            for (Recipe r : allRecipes) {
                if (r.getID() == res_id) {
                    output.add(r);
                    break;
                }
            }
        }

        return output;
    }

    public int getRecipeCount() {
        return getRecipeList().size();
    }

    void updateCategory() {
        db.updateCategory(getID(), getName());
    }

    public void delete() {
        db.deleteCategory(getID(), getRecipeList().toArray(new Recipe[0]));

    }
}
