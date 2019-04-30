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

    public IngredientAdapter(List<Ingredient> _ingredients, RecipeIngredientsFragment _fragment) {
        ingredients = _ingredients;
        fragment = _fragment;
    }

    @NonNull
    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View ingredientView = inflater.inflate(R.layout.item_ingredient, parent, false);

        ViewHolder viewHolder = new ViewHolder(ingredientView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final IngredientAdapter.ViewHolder viewHolder, int i) {
        final Ingredient ingredient = ingredients.get(i);

        TextView descTextView = viewHolder.descTextView;
        descTextView.setText(ingredient.getDescription());

        TextView amountTextView = viewHolder.amountTextView;
        amountTextView.setText(ingredient.getAmount());

        ConstraintLayout layout = viewHolder.layout;
        final Context context = viewHolder.context;

        Button edit_button = viewHolder.editButton;
        Button delete_button = viewHolder.deleteButton;

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIngredient(viewHolder.itemView, ingredient);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIngredient(viewHolder.itemView, ingredient);
            }
        });
    }

    public void editIngredient(View view, final Ingredient ingredient) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit Ingredient");

        View v = View.inflate(view.getContext(), R.layout.add_ingredient_popup, null);

        builder.setView(v);

        final EditText description_input = v.findViewById(R.id.desc_editview);
        final EditText amount_input = v.findViewById(R.id.amount_editview);

        description_input.setText(ingredient.getDescription());
        amount_input.setText(ingredient.getAmount());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ingredient.setDescription(description_input.getText().toString());
                ingredient.setAmount(amount_input.getText().toString());
                fragment.refreshIngredients(null);
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

    public void deleteIngredient(View view, final Ingredient ingredient) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete Ingredient");

        builder.setMessage("This cannot be undone.");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ingredient.delete();
                fragment.refreshIngredients(null);
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
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView descTextView;
        TextView amountTextView;

        Button editButton;
        Button deleteButton;

        Context context;

        public ViewHolder(final View itemView) {

            super(itemView);
            context = itemView.getContext();

            layout = itemView.findViewById(R.id.layout);
            descTextView = itemView.findViewById(R.id.ingredient_desc);
            amountTextView = itemView.findViewById(R.id.ingredient_amount);

            editButton = itemView.findViewById(R.id.ingredient_edit_button);
            deleteButton = itemView.findViewById(R.id.ingredient_delete_button);
        }
    }
}

