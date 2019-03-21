package com.example.recipekeeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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

        TextView categories_content = view.findViewById(R.id.categories_content);
        if (categories.size() == 0)
            categories_content.setText("None");
        else
            if (categories.size() <= 3) {
                StringBuilder text = new StringBuilder();
                for (Category c : categories) {
                    text.append(c.getName()).append(", ");
                }
                categories_content.setText(text.toString());
            }
            else
            {
                StringBuilder text = new StringBuilder();
                for (int i = 0 ; i < 3; i++) {
                    text.append(categories.get(i).getName()).append(", ");
                }
                categories_content.setText(String.format("%s +%d more", text.toString(), categories.size() - 3));
            }

        final TextView overviewTextView = view.findViewById(R.id.overview_text);
        final EditText overviewEditText = view.findViewById(R.id.overview_edit);

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

                // Grab focus onto text field.
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
