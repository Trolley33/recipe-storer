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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for listing categories.
 */
public class CategoryFragment extends Fragment {

    private List<Category> categoryList;
    private CategoryAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategoryFragment() {
    }

    /**
     * Called when fragment is created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate method variables.
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList);

    }

    /**
     * When the view is created.
     * @return view with category list.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view with layout.
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        Context context = view.getContext();

        // Get recycler view and bind adapter to it.
        RecyclerView recyclerView = view.findViewById(R.id.recipe_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // Get FAB and bind onclick handler.
        FloatingActionButton fab = view.findViewById(R.id.edit_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory(view);
            }
        });

        // Populate list with items.
        refreshCategories(view);
        return view;
    }

    /**
     * Refreshes the list of categories.
     */
    void refreshCategories(View view) {
        // Empty category list.
        categoryList.clear();
        // Get list of Categories
        categoryList.addAll(Category.getCategoryList());
        // Update adapter with new data.
        adapter.notifyDataSetChanged();
    }

    /**
     * Creates popup for entering new category information.
     */
    public void addCategory(View view) {
        // Open popup dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Category Title");

        // Text input field.
        final EditText input = new EditText(getContext());
        builder.setView(input);

        // Bind 'add' button to create new category and refresh the screen.
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Category.addNew(input.getText().toString());
                refreshCategories(null);
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
