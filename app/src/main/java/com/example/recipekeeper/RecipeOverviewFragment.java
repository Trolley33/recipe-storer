package com.example.recipekeeper;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A fragment for showing recipe overview.
 */
public class RecipeOverviewFragment extends Fragment {

    RecipeActivity parent;
    private Recipe selectedRecipe;

    private ArrayList<Category> categories;
    private ArrayList<Method> method;

    /**
     * Mandatory empty constructor for the fragment manager to
     * instantiate the fragment (e.g. upon screen orientation changes).
     */
    public RecipeOverviewFragment() {
    }

    public void setCategories(ArrayList<Category> _categories) {
        categories = _categories;
    }

    public void setMethod(ArrayList<Method> _method) {
        method = _method;
    }

    public void setSelectedRecipe(Recipe recipe) {
        selectedRecipe = recipe;
    }

    /**
     * Called when fragment is created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Called when view is created.
     * @return view with recipe list.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Create channel for notifications.
        createNotificationChannels();
        // Inflate view with layout.
        final View view = inflater.inflate(R.layout.fragment_recipe_overview, container, false);

        // Get time UI elements.
        final TextView time_display = view.findViewById(R.id.time);
        final ImageButton hour_glass_button = view.findViewById(R.id.hourglass);

        // Calculate time for recipe to finish.
        setMethod(Method.getMethodList(selectedRecipe.getID()));
        double t = 0;
        for (Method step : method) {
            t += step.getTime();
        }
        // Set information on display.
        time_display.setText(String.format("Time: %.2f (mins)", t));

        // Set category text.
        final TextView categories_content = view.findViewById(R.id.categories_content);
        refreshCategoryText(view);

        // Get other UI elements.
        final TextView overviewTextView = view.findViewById(R.id.overview_text);
        final EditText overviewEditText = view.findViewById(R.id.overview_edit);
        final FloatingActionButton edit_fab = view.findViewById(R.id.edit_fab);
        final FloatingActionButton save_fab = view.findViewById(R.id.save_fab);

        // Set both text boxes to have the existing overview text.
        overviewTextView.setText(selectedRecipe.getOverview());
        overviewEditText.setText(selectedRecipe.getOverview());
        // Hide editor by default
        overviewTextView.setVisibility(View.VISIBLE);
        overviewEditText.setVisibility(View.INVISIBLE);

        // Hide save button by default.
        edit_fab.show();
        save_fab.hide();

        // When edit button is clicked.
        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide text box, show edit box.
                overviewTextView.setVisibility(View.INVISIBLE);
                overviewEditText.setVisibility(View.VISIBLE);

                // Change style of categories, to indicate they can now be edited.
                categories_content.setTypeface(null, Typeface.ITALIC);
                categories_content.setTextColor(Color.CYAN);
                categories_content.setBackgroundResource(R.drawable.ripple_effect);

                // Add edit listener to categories label in this mode.
                categories_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editCategories(v);
                    }
                });

                // Grab focus onto overview edit field.
                if (overviewEditText.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(overviewEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                // Hide edit button, show save button.
                edit_fab.hide();
                save_fab.show();
            }
        });

        // When save button is clicked.
        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show text box, hide edit box.
                overviewTextView.setVisibility(View.VISIBLE);
                overviewEditText.setVisibility(View.INVISIBLE);

                // Set category style back to default.
                categories_content.setTypeface(null, Typeface.BOLD);
                categories_content.setTextColor(Color.parseColor("#b3ffffff"));
                categories_content.setBackgroundResource(R.color.colorPrimary);

                // Unbind listener.
                categories_content.setOnClickListener(null);

                // Show edit button, hide save button.
                edit_fab.show();
                save_fab.hide();

                // Get new overview text, and save it to database.
                String overview = overviewEditText.getText().toString();
                overviewTextView.setText(overview);
                // Update non-editable text.
                selectedRecipe.setOverview(overview);

                // Refresh category text when finished editing.
                setCategories(selectedRecipe.getCategories());
                refreshCategoryText(view);
            }
        });
        // Bind timer method to hourglass icon.
        hour_glass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        return view;
    }

    /**
     * Creates notification channel for this app.
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel",
                    "Recipe Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Recipe Notification Channel");

            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Creates and starts service for timing this recipe.
     */
    void startTimer() {
        Intent intent = new Intent(getContext(), TimerService.class);
        // Give service access to this recipe's ID.
        intent.putExtra("RECIPE_ID", selectedRecipe.getID());
        getContext().startService(intent);
    }

    /**
     * Creates popup for entering new recipe information.
     * @param parent of this popup.
     */
    void editCategories(View parent) {
        // Open popup dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
        builder.setTitle("Choose categories");

        // Update selected categories to ensure none are missing.
        setCategories(selectedRecipe.getCategories());

        // Get list of all categories.
        ArrayList<Category> allCategories = Category.getCategoryList();

        final String[] cat_strings = new String[allCategories.size()];
        final int[] cat_ids = new int[allCategories.size()];
        final boolean[] cat_bools = new boolean[allCategories.size()];

        // Loop over each category.
        for (int i = 0; i < allCategories.size(); i++) {
            // Add names to array.
            cat_strings[i] = allCategories.get(i).getName();
            // Add ids to array.
            cat_ids[i] = allCategories.get(i).getID();
            // Check all selected categories.
            for (int j = 0; j < categories.size(); j++) {
                // If the current recipe is selected, mark it as selected in the boolean array.
                if (categories.get(j).getID() == allCategories.get(i).getID()) {
                    cat_bools[i] = true;
                    break;
                }
            }
        }

        // Set checkbox items to above arrays.
        builder.setMultiChoiceItems(cat_strings, cat_bools, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            }
        });
        // On 'saving' the selected categories.
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Loop over the checkbox items.
                for (int i = 0; i < cat_bools.length; i++) {
                    // If category is checked.
                    if (cat_bools[i]) {
                        // Add category to recipe.
                        selectedRecipe.addCategory(cat_ids[i]);
                    }
                    // If item is unchecked.
                    else {
                        // Remove category from recipe.
                        selectedRecipe.removeCategory(cat_ids[i]);
                    }
                }
            }
        });

        // Bind the cancel button to cancel the dialog.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Updates the text of the category display.
     * @param v parent view.
     */
    void refreshCategoryText(View v) {
        // Get category UI element.
        final TextView categories_content = v.findViewById(R.id.categories_content);
        // No categories -> 'None'
        if (categories.size() == 0) {
            categories_content.setText("None");
        } else {
            // <= 3 categories -> 'C1, C2, C3'
            if (categories.size() <= 3) {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < categories.size(); i++) {
                    text.append(categories.get(i).getName());
                    if (i != categories.size() - 1) {
                        text.append(", ");
                    }
                }
                categories_content.setText(text.toString());
            }
            // > 3 categories -> 'C1, C2, C3, +X more'
            else {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    text.append(categories.get(i).getName()).append(", ");
                }
                categories_content.setText(String.format("%s +%d more", text.toString(), categories.size() - 3));
            }
        }
    }

    void setParent(RecipeActivity _parent) {
        parent = _parent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
