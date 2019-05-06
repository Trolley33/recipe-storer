package com.example.recipekeeper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for listing favourite recipes.
 */
public class FavRecipesFragment extends Fragment {


    private List<Recipe> recipeList;
    private RecipeAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavRecipesFragment() {
    }
    /**
     * Called when fragment is created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate method variables.
        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList);

    }

    /**
     * Called when view is created.
     * @return view with recipe list.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view with layout.
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        Context context = view.getContext();

        // Get recycler view and bind adapter to it.
        RecyclerView recyclerView = view.findViewById(R.id.recipe_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // Get FAB and bind onclick handler.
        FloatingActionButton fab = view.findViewById(R.id.edit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When FAB is pressed, run the add recipe method.
                addRecipe(view);
            }
        });

        // Populate the list with items.
        refreshRecipes(view);
        return view;
    }

    /**
     * Refreshes the list of recipes.
     */
    void refreshRecipes(View view) {
        // Empty recipe list.
        recipeList.clear();
        // Get list of Recipes tagged as favourites..
        recipeList.addAll(Recipe.getRecipeList(DBHelper.FILTER.FAVOURITES));
        // Update the adapter.
        adapter.notifyDataSetChanged();
    }

    /**
     * Creates popup for entering new recipe information.
     */
    public void addRecipe(View view) {
        // Open popup dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Recipe Title");

        // Text input field.
        final EditText input = new EditText(getContext());
        builder.setView(input);

        // Bind 'add' button to create the new favourite recipe and refresh the screen.
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Recipe.addNew(input.getText().toString(), "", 1);
                refreshRecipes(null);
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
