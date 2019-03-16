package com.example.recipekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder>
{
    private List<Ingredient> ingredients;

    public IngredientAdapter(List<Ingredient> _ingredients)
    {
        ingredients = _ingredients;
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
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView descTextView;
        TextView amountTextView;
        Context context;

        public ViewHolder(final View itemView) {

            super(itemView);
            context = itemView.getContext();

            layout =  itemView.findViewById(R.id.layout);
            descTextView = itemView.findViewById(R.id.ingredient_desc);
            amountTextView = itemView.findViewById(R.id.ingredient_amount);
        }
    }
}

