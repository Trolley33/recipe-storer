package com.example.recipekeeper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>
{
    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View recipeView = inflater.inflate(R.layout.item_recipe, parent, false);

        ViewHolder viewHolder = new ViewHolder(recipeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder viewHolder, int i) {
        Recipe recipe = recipes.get(i);

        TextView textView = viewHolder.nameTextView;
        textView.setText(recipe.getName());
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private List<Recipe> recipes;

    public RecipeAdapter(List<Recipe> _recipes)
    {
        recipes = _recipes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public ViewHolder(View itemView) {

            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.recipe_name);
        }
    }
}
