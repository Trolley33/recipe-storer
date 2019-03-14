package com.example.recipekeeper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
 * A fragment representing a list of Items.
 * <p/>

 * interface.
 */
public class AllRecipesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView recyclerView;
    private List<Recipe> recipeList;
    private RecipeAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllRecipesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AllRecipesFragment newInstance(int columnCount) {
        AllRecipesFragment fragment = new AllRecipesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        Context context = view.getContext();

        // Set the adapter
        if (view instanceof RecyclerView) {

            recyclerView = (RecyclerView) view;
        }
        else
        {
            recyclerView = view.findViewById(R.id.recipe_list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecipe(view);
            }
        });

        refreshRecipes(view);
        adapter.notifyDataSetChanged();

        return view;
    }

    void refreshRecipes(View view)
    {
        recipeList.clear();
        recipeList.addAll(Recipe.getRecipeList(DBHelper.FILTER.ALL));
        adapter.notifyDataSetChanged();
    }

    public void addRecipe(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Recipe Title");

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Recipe.addNew(input.getText().toString(), "", 0);
                refreshRecipes(null);
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
