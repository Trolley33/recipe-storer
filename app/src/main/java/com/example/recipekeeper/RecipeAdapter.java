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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private List<Recipe> recipes;

    RecipeAdapter(List<Recipe> _recipes) {
        recipes = _recipes;
    }

    /**
     * Called when view holder is created.
     */
    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate view with custom layout.
        View recipeView = inflater.inflate(R.layout.item_recipe, parent, false);

        return new ViewHolder(recipeView);
    }

    /**
     * When this is bound to it's parent.
     */
    @Override
    public void onBindViewHolder(@NonNull final RecipeAdapter.ViewHolder viewHolder, int i) {
        // Get the recipe represented by this adapter.
        final Recipe recipe = recipes.get(i);

        // Retrieve UI elements from parent.
        TextView textView = viewHolder.nameTextView;
        Button favouriteButton = viewHolder.favouriteButton;
        ConstraintLayout layout = viewHolder.layout;
        final Context context = viewHolder.context;

        // Set value of text view to match recipe information.
        textView.setText(recipe.getName());

        // Set correct icon for favourite star.
        if (recipe.isFavourite()) {
            favouriteButton.setBackgroundResource(R.drawable.ic_baseline_star_24px);
        } else {
            favouriteButton.setBackgroundResource(R.drawable.ic_baseline_star_border_24px);
        }
        // Bind favourite button click event to togggle recipe's favourite state, and update that recipe in the adapter.
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.toggleFavourite();
                notifyItemChanged(recipe.getPosition());
            }
        });
        // Bind row click event to start intent for this recipe, and open the new activity.
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeActivity.class);
                // Place recipe id as extra, to retrieve later.
                intent.putExtra(HomeActivity.RECIPE_ID_MESSAGE, recipe.getID());
                context.startActivity(intent);
            }
        });

    }

    /**
     * Number of items this adapter will display.
     * @return size of recipe list.
     */
    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * Sets member variables for each view holder.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView nameTextView;
        Button favouriteButton;
        Context context;

        ViewHolder(final View itemView) {

            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            nameTextView = itemView.findViewById(R.id.recipe_name);
            favouriteButton = itemView.findViewById(R.id.fav_button);
            context = itemView.getContext();
        }
    }
}

