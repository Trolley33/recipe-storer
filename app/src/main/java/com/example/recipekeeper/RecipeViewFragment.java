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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>

 * interface.
 */
public class RecipeViewFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private Recipe selectedRecipe;
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ArrayList<Category> categories;

    public RecipeViewFragment() {
    }

    // TODO: Customize parameter initialization
    public static RecipeViewFragment newInstance(int columnCount) {
        RecipeViewFragment fragment = new RecipeViewFragment();
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

        TextView categories_text = view.findViewById(R.id.categories);
        if (categories.size() == 0)
            categories_text.setText("Categories: None");
        else
            // TODO: fix this.
            categories_text.setText(String.format("Categories: %s", categories.get(0).getName()));

        final TextView textView = view.findViewById(R.id.overview_text);
        final EditText editText = view.findViewById(R.id.overview_edit);

        final FloatingActionButton edit_fab = view.findViewById(R.id.edit_fab);
        final FloatingActionButton save_fab = view.findViewById(R.id.save_fab);

        textView.setText(selectedRecipe.getOverview());
        editText.setText(selectedRecipe.getOverview());

        textView.setVisibility(View.VISIBLE);
        editText.setVisibility(View.INVISIBLE);

        edit_fab.setVisibility(View.VISIBLE);
        save_fab.setVisibility(View.INVISIBLE);


        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                textView.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.VISIBLE);
                if (editText.requestFocus())
                {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }

                edit_fab.setVisibility(View.INVISIBLE);
                save_fab.setVisibility(View.VISIBLE);
            }
        });

        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                textView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.INVISIBLE);

                edit_fab.setVisibility(View.VISIBLE);
                save_fab.setVisibility(View.INVISIBLE);

                String overview = editText.getText().toString();
                textView.setText(overview);
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
