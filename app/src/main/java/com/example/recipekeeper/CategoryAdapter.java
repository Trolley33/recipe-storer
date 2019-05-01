package com.example.recipekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categories;

    CategoryAdapter(List<Category> _categories) {
        categories = _categories;
    }

    /**
     * Called when view holder is created.
     */
    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate view with custom layout.
        View categoryView = inflater.inflate(R.layout.item_category, parent, false);

        return new ViewHolder(categoryView);
    }

    /**
     * When this is bound to it's parent.
     */
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder parent, int i) {
        // Get the category represented by this adapter.
        final Category category = categories.get(i);

        // Retrieve UI elements from parent.
        final ConstraintLayout layout = parent.layout;
        final Context context = parent.context;
        final TextView nameTextView = parent.nameTextView;
        final TextView countTextView = parent.countTextView;

        // Set values of text view to match this category's information.
        nameTextView.setText(category.getName());
        countTextView.setText(String.format("%d Recipes", category.getRecipeCount()));

        // When the row is clicked.
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for opening category list.
                Intent intent = new Intent(context, CategoryListActivity.class);
                // Put category's ID as extra, to be used later.
                intent.putExtra(HomeActivity.CATEGORY_ID_MESSAGE, category.getID());
                context.startActivity(intent);
            }
        });
    }

    /**
     * Number of items this adapter will display.
     * @return size of category list.
     */
    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Sets member variables for each view holder.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView countTextView;
        ConstraintLayout layout;
        Context context;

        ViewHolder(View itemView) {

            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            context = itemView.getContext();
            nameTextView = itemView.findViewById(R.id.category_name);
            countTextView = itemView.findViewById(R.id.category_count);
        }
    }
}
