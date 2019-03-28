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

import org.w3c.dom.Text;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>
{
    private List<Category> categories;

    public CategoryAdapter(List<Category> _categories)
    {
        categories = _categories;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View categoryView = inflater.inflate(R.layout.item_category, parent, false);

        ViewHolder viewHolder = new ViewHolder(categoryView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder viewHolder, int i) {
        final Category category = categories.get(i);

        ConstraintLayout layout = viewHolder.layout;
        final Context context = viewHolder.context;

        TextView nameTextView = viewHolder.nameTextView;
        TextView countTextView = viewHolder.countTextView;

        nameTextView.setText(category.getName());
        countTextView.setText(String.format("%d Recipes", category.getRecipeCount()));

        layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
            Intent intent = new Intent(context, CategoryListActivity.class);

            intent.putExtra(HomeActivity.CATEGORY_ID_MESSAGE, category.getID());
            context.startActivity(intent);
                    }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView countTextView;

        ConstraintLayout layout;
        Context context;

        public ViewHolder(View itemView) {

            super(itemView);
            layout = (ConstraintLayout) itemView.findViewById(R.id.layout);
            context = itemView.getContext();
            nameTextView = (TextView) itemView.findViewById(R.id.category_name);
            countTextView = (TextView) itemView.findViewById(R.id.category_count);
        }
    }
}
