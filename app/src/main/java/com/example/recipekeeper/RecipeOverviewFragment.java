package com.example.recipekeeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A fragment representing a list of Items.
 * <p/>

 * interface.
 */
public class RecipeOverviewFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private Recipe selectedRecipe;
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ArrayList<Category> categories;
    private ArrayList<Method> method;

    public RecipeOverviewFragment() {
    }

    // TODO: Customize parameter initialization
    public static RecipeOverviewFragment newInstance(int columnCount) {
        RecipeOverviewFragment fragment = new RecipeOverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCategories(ArrayList<Category> _categories)
    {
        categories = _categories;
    }

    public void setMethod(ArrayList<Method> _method)
    {
        method = _method;
    }

    public void setSelectedRecipe(Recipe recipe)
    {
        selectedRecipe = recipe;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipe_overview, container, false);
        final Context context = view.getContext();

        // Get time display
        final TextView time_display = view.findViewById(R.id.time);

        // Calculate time for recipe to finish.
        double t = 0;
        for (Method step : method)
        {
            t += step.getTime();
        }

        time_display.setText(String.format("Time: %.2f (mins)", t));

        // Get category display.
        final TextView categories_content = view.findViewById(R.id.categories_content);
        // No categories -> None
        if (categories.size() == 0)
            categories_content.setText("None");
        else
            // <= 3 categories -> C1, C2, C3
            if (categories.size() <= 3) {
                StringBuilder text = new StringBuilder();
                for (Category c : categories) {
                    text.append(c.getName()).append(", ");
                }
                categories_content.setText(text.toString());
            }
            // > 3 categories -> C1, C2, C3, +X more
            else
            {
                StringBuilder text = new StringBuilder();
                for (int i = 0 ; i < 3; i++) {
                    text.append(categories.get(i).getName()).append(", ");
                }
                categories_content.setText(String.format("%s +%d more", text.toString(), categories.size() - 3));
            }

        // Overview text section, and overview edit text section.
        final TextView overviewTextView = view.findViewById(R.id.overview_text);
        final EditText overviewEditText = view.findViewById(R.id.overview_edit);

        // Edit button and save button.
        final FloatingActionButton edit_fab = view.findViewById(R.id.edit_fab);
        final FloatingActionButton save_fab = view.findViewById(R.id.save_fab);

        overviewTextView.setText(selectedRecipe.getOverview());
        overviewEditText.setText(selectedRecipe.getOverview());

        overviewTextView.setVisibility(View.VISIBLE);
        overviewEditText.setVisibility(View.INVISIBLE);

        edit_fab.setVisibility(View.VISIBLE);
        save_fab.setVisibility(View.INVISIBLE);

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Hide text box, show edit box.
                overviewTextView.setVisibility(View.INVISIBLE);
                overviewEditText.setVisibility(View.VISIBLE);

                categories_content.setTypeface(null, Typeface.ITALIC);
                categories_content.setTextColor(Color.CYAN);
                categories_content.setBackgroundResource(R.drawable.ripple_effect);

                categories_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editCategories(v);
                    }
                });

                // Grab focus onto overview edit field.
                if (overviewEditText.requestFocus())
                {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(overviewEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                // Hide edit button, show save button.
                edit_fab.setVisibility(View.INVISIBLE);
                save_fab.setVisibility(View.VISIBLE);
            }
        });

        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Hide edit box, show text box.
                overviewTextView.setVisibility(View.VISIBLE);
                overviewEditText.setVisibility(View.INVISIBLE);

                categories_content.setTypeface(null, Typeface.BOLD);
                categories_content.setTextColor(Color.parseColor("#b3ffffff"));
                categories_content.setBackgroundResource(R.color.colorPrimary);

                categories_content.setOnClickListener(null);

                // Hide save button, show edit button.
                edit_fab.setVisibility(View.VISIBLE);
                save_fab.setVisibility(View.INVISIBLE);

                // Get new overview text, and save it to database.
                String overview = overviewEditText.getText().toString();
                overviewTextView.setText(overview);
                selectedRecipe.setOverview(overview);
            }
        });
        return view;
    }

    void editCategories (View root)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        builder.setTitle("Edit categories");

        View v = getLayoutInflater().inflate(R.layout.add_method_popup, null);

        builder.setView(v);

        final EditText step_input = v.findViewById(R.id.step_edit);
        final EditText time_input = v.findViewById(R.id.time_edit);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Method.addMethod(selectedRecipe.getID(), step_input.getText().toString(), Double.parseDouble(time_input.getText().toString()));
                // refreshMethods(null);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
