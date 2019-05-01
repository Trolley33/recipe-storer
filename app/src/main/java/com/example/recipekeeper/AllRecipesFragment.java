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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment for listing recipes.
 */
public class AllRecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Recipe> recipeList;
    private RecipeAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to
     * instantiate the fragment (e.g. upon screen orientation changes).
     */
    public AllRecipesFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        Context context = view.getContext();

        // Get recycler view and bind adapter to it.
        recyclerView = view.findViewById(R.id.recipe_list);
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
        // Bind touch helper.
        attachItemTouchHelper();
        return view;
    }

    /**
     * Attaches touch helper to recycler view, allowing drag/drop movement.
     */
    void attachItemTouchHelper() {
        // Extend the callback class.
        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            // When an item is dragged and dropped.
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // Swap dragged item and target item.
                recipeList.get(viewHolder.getAdapterPosition()).setPosition(target.getAdapterPosition());
                recipeList.get(target.getAdapterPosition()).setPosition(viewHolder.getAdapterPosition());

                Collections.swap(recipeList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

                // Notify adapter to change positions.
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            // No swiping logic required.
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {}

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                refreshRecipes(null);
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };

        // Create an 'ItemTouchHelper' and attach it to the 'RecyclerView'
        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        ith.attachToRecyclerView(recyclerView);
    }

    /**
     * Refreshes the list of recipes.
     */
    void refreshRecipes(View view) {
        // Empty recipe list.
        recipeList.clear();
        // Get list of Recipes.
        ArrayList<Recipe> rList = Recipe.getRecipeList(DBHelper.FILTER.ALL);
        // Reset ordering to start at 0.
        for (int i = 0; i < rList.size(); i++) {
            rList.get(i).setPosition(i);
        }
        // Add newly ordered list to the list bound to the adapter.
        recipeList.addAll(rList);
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

        // Bind 'add' button to create the new recipe and refresh the screen.
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Recipe.addNew(input.getText().toString(), "", 0);
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
