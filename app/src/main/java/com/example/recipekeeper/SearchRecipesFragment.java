package com.example.recipekeeper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for listing recipes.
 */
public class SearchRecipesFragment extends Fragment {

    private List<Recipe> recipeList;
    private RecipeAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchRecipesFragment() {
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view with layout.
        final View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        Context context = view.getContext();

        // Get recycler view and bind adapter to it.
        RecyclerView recyclerView = view.findViewById(R.id.recipe_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // Refresh recipe list, no method needed as this only happens once per search.
        recipeList.clear();
        recipeList.addAll(Recipe.searchFor(((SearchActivity) getActivity()).getSearchTerm()));
        adapter.notifyDataSetChanged();

        // Hide included floating action button from view.
        FloatingActionButton fab = view.findViewById(R.id.edit_fab);
        fab.hide();

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
