package com.example.recipekeeper;

import android.database.Cursor;

import java.util.ArrayList;

public class Method {
    static DBHelper db;
    private int id;
    private int recipe_id;
    private int position;
    private String step;
    private double time;
    private ArrayList<Category> categories;

    public Method(int _id, int _recipe_id, int _pos, String _step, double _time) {
        id = _id;
        recipe_id = _recipe_id;
        position = _pos;
        step = _step;
        time = _time;
    }

    public static void addMethod(int recipe_id, String step, double time) {
        db.createNewMethod(recipe_id, getMethodList(recipe_id).size(), step, time);
    }

    public static ArrayList<Method> getMethodList(int recipe_id) {
        ArrayList<Method> methods = new ArrayList<>();

        Cursor res = db.getMethodList(recipe_id);
        if (res == null) {
            return methods;
        }
        if (res.getCount() == 0) {
            return methods;
        }

        while (res.moveToNext()) {
            int _id = res.getInt(0);
            int _recipe_id = res.getInt(1);
            int _position = res.getInt(2);
            String _step = res.getString(3);
            double _time = res.getDouble(4);

            methods.add(new Method(_id, _recipe_id, _position, _step, _time));
        }


        return methods;
    }

    public static Method getFromID(int _id, int recipe_id) {
        for (Method i : getMethodList(recipe_id)) {
            if (i.getID() == _id) {
                return i;
            }
        }
        return null;
    }

    public int getID() {
        return id;
    }

    public int getRecipeID() {
        return recipe_id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int _pos) {
        position = _pos;
        updateMethod();
    }

    public String getStep() {
        return step;
    }

    public void setStep(String _step) {
        step = _step;
        updateMethod();
    }

    public double getTime() {
        return time;
    }

    public void setTime(double _time) {
        time = _time;
        updateMethod();
    }

    void updateMethod() {
        db.updateMethod(getID(), getRecipeID(), getPosition(), getStep(), getTime());
    }

    public void delete() {
        db.deleteMethod(getID());
    }
}
