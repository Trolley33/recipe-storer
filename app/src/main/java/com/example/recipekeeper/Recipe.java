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
        id = _id;
        name = _name;
        overview = _overview;
        favourite = (_fav == 1);
        position = _pos;
    }

    public static List<Recipe> searchFor(String term) {
        ArrayList<Recipe> results = new ArrayList<>();
        term = term.toLowerCase();
        for (Recipe r : getRecipeList(DBHelper.FILTER.ALL)) {
            if (r.getName().toLowerCase().contains(term)) {
                results.add(r);
            } else {
                for (Category c : r.getCategories()) {
                    if (c.getName().toLowerCase().contains(term)) {
                        results.add(r);
                    }
                }
            }
        }

        return results;
    }

    public static void addNew(String name, String overview) {
        db.createNewRecipe(name, overview, 0);
    }

    public static void addNew(String name, String overview, int fav) {
        db.createNewRecipe(name, overview, fav);
    }

    public static ArrayList<Recipe> getRecipeList(Enum filter) {
        ArrayList<Recipe> recipes = new ArrayList<>();

        Cursor res = db.getRecipeList(filter);
        if (res == null) {
            return recipes;
        }
        if (res.getCount() == 0) {
            return recipes;
        }

        while (res.moveToNext()) {
            int _id = res.getInt(0);
            String _name = res.getString(1);
            String _overview = res.getString(2);
            int _favourite = res.getInt(3);
            int _position = res.getInt(4);

            recipes.add(new Recipe(_id, _name, _overview, _favourite, _position));
        }


        return recipes;
    }

    public static Recipe getFromID(int _id) {
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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String _overview) {
        overview = _overview;
        updateRecipe();
    }

    public boolean isFavourite() {
        return favourite;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int _pos) {
        position = _pos;
        updateRecipe();
    }

    public ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        Cursor res = db.getCategoryRecipeList();

        while (res.moveToNext()) {
            int recipe_id = res.getInt(2);
            int cat_id = res.getInt(0);
            if (getID() == recipe_id) {
                categories.add(Category.getFromID(cat_id));
            }
        }

        return categories;
    }

    public void toggleFavourite() {
        favourite = !favourite;
        updateRecipe();
    }

    public boolean addCategory(int category_id) {
        return db.addCategoryToRecipe(getID(), category_id);
    }

    public void removeCategory(int category_id) {
        db.removeCategoryFromRecipe(getID(), category_id);
    }

    void updateRecipe() {
        db.updateRecipe(getID(), getName(), getOverview(), isFavourite() ? 1 : 0, getPosition());
    }

    public void delete() {
        db.deleteRecipe(getID());
    }
}
