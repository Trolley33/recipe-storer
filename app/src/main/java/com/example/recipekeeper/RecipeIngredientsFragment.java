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
 * A fragment for listing ingredients.
 */
public class RecipeIngredientsFragment extends Fragment {

    RecipeActivity parent;
    private Recipe selectedRecipe;
    private RecyclerView recyclerView;
    private List<Ingredient> ingredientsList;
    private IngredientAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeIngredientsFragment() {
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

        // Instantiate method variables.
        ingredientsList = new ArrayList<>();
        adapter = new IngredientAdapter(ingredientsList, this);
    }

    /**
     * Called when view is created.
     * @return view with ingredient list.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate view with layout.
        final View view = inflater.inflate(R.layout.fragment_ingredient_list, container, false);
        Context context = view.getContext();

        // Get recycler view and bind adapter to it.
        recyclerView = view.findViewById(R.id.ingredient_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // Get FAB and bind onclick handler.
        FloatingActionButton fab = view.findViewById(R.id.add_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When FAB is pressed, run the add ingredient method.
                addIngredient(view);
            }
        });

        // Populate the list with items.
        refreshIngredients(view);
        // Bind touch helper.
        attachItemTouchHelper();
        return view;
    }

    void setParent(RecipeActivity _parent) {
        parent = _parent;
    }

    /**
     * Attaches touch helper to recycler view, allowing drag/drop movement.
     */
    void attachItemTouchHelper() {
        // Extend the Callback class
        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            // When an item is dragged and dropped.
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // Swap dragged item and target item.
                ingredientsList.get(viewHolder.getAdapterPosition()).setPosition(target.getAdapterPosition());
                ingredientsList.get(target.getAdapterPosition()).setPosition(viewHolder.getAdapterPosition());

                Collections.swap(ingredientsList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

                // Notify adapter to change positions.
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            // No swiping logic required.
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
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
     * Refreshes the list of ingredients.
     */
    void refreshIngredients(View view) {
        // Empty ingredient list.
        ingredientsList.clear();
        // Add list to adapter.
        ingredientsList.addAll(Ingredient.getIngredientList(selectedRecipe.getID()));
        // Update the adapter.
        adapter.notifyDataSetChanged();
        // Update values of sharing.
        parent.setShareValues();
    }

    /**
     * Creates popup for entering new ingredient information.
     */
    public void addIngredient(View view) {
        // Open popup dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add new ingredient");

        // Create view for popup.
        View v = getLayoutInflater().inflate(R.layout.add_ingredient_popup, null);
        builder.setView(v);

        // Text input fields.
        final EditText description_input = v.findViewById(R.id.desc_editview);
        final EditText amount_input = v.findViewById(R.id.amount_editview);

        // Bind 'add' button to create the new ingredient and refresh the screen.
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Ingredient.addIngredient(selectedRecipe.getID(),
                        description_input.getText().toString(), amount_input.getText().toString());
                refreshIngredients(null);
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
