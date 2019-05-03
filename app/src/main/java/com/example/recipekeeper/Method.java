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

    public Method(int _id, int _recipe_id, int _pos, String _step, double _time) {
        // Set member values on instantiation.
        id = _id;
        recipe_id = _recipe_id;
        position = _pos;
        step = _step;
        time = _time;
    }

    /**
     * Add new step to method in database.
     * @param recipe_id of recipe to add to.
     * @param step description
     * @param time in minutes.
     */
    static void addMethod(int recipe_id, String step, double time) {
        db.createNewMethod(recipe_id, getMethodList(recipe_id).size(), step, time);
    }

    /**
     * Retrieves a list of all steps for a recipe.
     * @param recipe_id to get steps for.
     * @return {@link ArrayList<Method>} of steps.
     */
    static ArrayList<Method> getMethodList(int recipe_id) {
        ArrayList<Method> methods = new ArrayList<>();

        // Retrieve cursor containing all steps from  database.
        Cursor res = db.getMethodList(recipe_id);
        // Query failed, return blank list.
        if (res == null) {
            return methods;
        }
        // Query empty, return blank list.
        if (res.getCount() == 0) {
            return methods;
        }

        // Loop over each row.
        while (res.moveToNext()) {
            // Add Method object to list.
            int _id = res.getInt(0);
            int _recipe_id = res.getInt(1);
            int _position = res.getInt(2);
            String _step = res.getString(3);
            double _time = res.getDouble(4);

            methods.add(new Method(_id, _recipe_id, _position, _step, _time));
        }

        // Return list.
        return methods;
    }

    public int getID() {
        return id;
    }

    private int getRecipeID() {
        return recipe_id;
    }

    int getPosition() {
        return position;
    }

    void setPosition(int _pos) {
        position = _pos;
        updateMethod();
    }

    String getStep() {
        return step;
    }

    void setStep(String _step) {
        step = _step;
        updateMethod();
    }

    double getTime() {
        return time;
    }

    void setTime(double _time) {
        time = _time;
        updateMethod();
    }

    /**
     * Update database side information with this step.
     */
    private void updateMethod() {
        db.updateMethod(getID(), getRecipeID(), getPosition(), getStep(), getTime());
    }

    /**
     * Delete this step from the database.
     */
    public void delete() {
        db.deleteMethod(getID());
    }
}
