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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>
{
    private List<Recipe> recipes;

    public RecipeAdapter(List<Recipe> _recipes)
    {
        recipes = _recipes;
    }

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
    public void onBindViewHolder(@NonNull final RecipeAdapter.ViewHolder viewHolder, int i) {
        final Recipe recipe = recipes.get(i);

        TextView textView = viewHolder.nameTextView;
        textView.setText(recipe.getName());
        Button favouriteButton = viewHolder.favouriteButton;
        ConstraintLayout layout = viewHolder.layout;
        final Context context = viewHolder.context;

        if (recipe.isFavourite())
        {
            favouriteButton.setBackgroundResource(R.drawable.ic_baseline_star_24px);
        }
        else
        {
            favouriteButton.setBackgroundResource(R.drawable.ic_baseline_star_border_24px);
        }

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.toggleFavourite();
                notifyItemChanged(recipe.getPosition());
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeActivity.class);

                intent.putExtra(HomeActivity.RECIPE_ID_MESSAGE, recipe.getID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        Button favouriteButton;
        ConstraintLayout layout;
        Context context;

        public ViewHolder(final View itemView) {

            super(itemView);
            context = itemView.getContext();
            layout = (ConstraintLayout) itemView.findViewById(R.id.layout);
            nameTextView = (TextView) itemView.findViewById(R.id.recipe_name);
            favouriteButton = (Button) itemView.findViewById(R.id.fav_button);
        }
    }
}

