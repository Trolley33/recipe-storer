package com.example.recipekeeper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
 * A fragment representing a list of Items.
 * <p/>

 * interface.
 */
public class RecipeIngredientsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private Recipe selectedRecipe;
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView recyclerView;
    private List<Ingredient> ingredientsList;
    private IngredientAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeIngredientsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipeIngredientsFragment newInstance(int columnCount) {
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void setSelectedRecipe(Recipe recipe)
    {
        selectedRecipe = recipe;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ingredientsList = new ArrayList<>();
        adapter = new IngredientAdapter(ingredientsList, this);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_ingredient_list, container, false);
        Context context = view.getContext();

        // Set the adapter
        if (view instanceof RecyclerView) {

            recyclerView = (RecyclerView) view;
        }
        else
        {
            recyclerView = view.findViewById(R.id.ingredient_list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngredient(view);
            }
        });

        refreshIngredients(view);
        adapter.notifyDataSetChanged();

        attachItemTouchHelper();

        return view;
    }

    void attachItemTouchHelper ()
    {
        // Extend the Callback class
        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            // When an item is dragged and dropped.
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // Swap dragged item and target item.
                ingredientsList.get(viewHolder.getAdapterPosition()).setPosition(target.getAdapterPosition());
                ingredientsList.get(target.getAdapterPosition()).setPosition(viewHolder.getAdapterPosition());

                Collections.swap(ingredientsList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

                // Notify adapter to change positions.
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };
        // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        ith.attachToRecyclerView(recyclerView);
    }

    void refreshIngredients(View view)
    {
        ingredientsList.clear();
        ingredientsList.addAll(Ingredient.getIngredientList(selectedRecipe.getID()));
        adapter.notifyDataSetChanged();
    }

    public void addIngredient(View view)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add new ingredient");

        View v = getLayoutInflater().inflate(R.layout.add_ingredient_popup, null);

        builder.setView(v);

        final EditText description_input = v.findViewById(R.id.desc_editview);
        final EditText amount_input = v.findViewById(R.id.amount_editview);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Ingredient.addIngredient(selectedRecipe.getID(),
                        description_input.getText().toString(), amount_input.getText().toString());
                refreshIngredients(null);
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
