package com.example.recipekeeper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private List<Ingredient> ingredients;
    private RecipeIngredientsFragment fragment;

    IngredientAdapter(List<Ingredient> _ingredients, RecipeIngredientsFragment _fragment) {
        ingredients = _ingredients;
        fragment = _fragment;
    }

    /**
     * Called when view holder is created.
     */
    @NonNull
    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate view with custom layout.
        View ingredientView = inflater.inflate(R.layout.item_ingredient, parent, false);

        return new ViewHolder(ingredientView);
    }

    /**
     * When this is bound to it's parent.
     */
    @Override
    public void onBindViewHolder(@NonNull final IngredientAdapter.ViewHolder viewHolder, int i) {
        // Get the ingredient represented by this adapter.
        final Ingredient ingredient = ingredients.get(i);

        // Retrieve UI elements from parent.
        TextView descTextView = viewHolder.descTextView;
        TextView amountTextView = viewHolder.amountTextView;
        Button edit_button = viewHolder.editButton;
        Button delete_button = viewHolder.deleteButton;

        // Set values of text view to match this ingredients information.
        descTextView.setText(ingredient.getDescription());
        amountTextView.setText(ingredient.getAmount());


        // Bind onclick of edit button to method.
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIngredient(viewHolder.itemView, ingredient);
            }
        });

        // Bind onclick of delete button to method.
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIngredient(viewHolder.itemView, ingredient);
            }
        });
    }

    /**
     * Creates popup for editing ingredient information.
     */
    private void editIngredient(View view, final Ingredient ingredient) {
        // Open popup dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit Ingredient");

        // Inflate view with custom layout.
        View v = View.inflate(view.getContext(), R.layout.add_ingredient_popup, null);
        builder.setView(v);

        // Get text input fields from view.
        final EditText description_input = v.findViewById(R.id.desc_editview);
        final EditText amount_input = v.findViewById(R.id.amount_editview);

        // Set values to pre-existing ones.
        description_input.setText(ingredient.getDescription());
        amount_input.setText(ingredient.getAmount());

        // Bind 'save' button to update information about ingredient, and refresh the parent fragment.
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ingredient.setDescription(description_input.getText().toString());
                ingredient.setAmount(amount_input.getText().toString());
                fragment.refreshIngredients(null);
            }
        });

        // Bind 'cancel' button to cancel dialog.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Creates popup for confirming deletion..
     */
    private void deleteIngredient(View view, final Ingredient ingredient) {
        // Open popup dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete Ingredient");
        builder.setMessage("This cannot be undone.");

        // Bind 'delete' button to remove ingredient from database, and refresh parent fragment.
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ingredient.delete();
                fragment.refreshIngredients(null);
            }
        });
        // Bind 'cancel' button to a cancel dialog.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Number of items this adapter will display.
     * @return size of category list.
     */
    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    /**
     * Sets member variables for each view holder.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView descTextView;
        TextView amountTextView;
        Button editButton;
        Button deleteButton;
        Context context;

        ViewHolder(final View itemView) {

            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            descTextView = itemView.findViewById(R.id.ingredient_desc);
            amountTextView = itemView.findViewById(R.id.ingredient_amount);
            editButton = itemView.findViewById(R.id.ingredient_edit_button);
            deleteButton = itemView.findViewById(R.id.ingredient_delete_button);
            context = itemView.getContext();
        }
    }
}

