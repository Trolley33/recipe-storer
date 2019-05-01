package com.example.recipekeeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {
    Category selectedCategory;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Recipe> recipeList;
    private RecipeAdapter adapter;

    /**
     * When activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content to custom layout.
        setContentView(R.layout.activity_category_viewer);

        // Retrieve selected category ID from intent.
        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getExtras().get(HomeActivity.CATEGORY_ID_MESSAGE).toString());

        // Get Category object from selected ID.
        selectedCategory = Category.getFromID(id);

        // Setup custom action bar.
        toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle(selectedCategory.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Instantiate member variables.
        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList);

        // Get recycler view and bind adapter to it.
        recyclerView = findViewById(R.id.recipe_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Update screen.
        refreshRecipes(null);
    }

    /**
     * Refreshes the list of recipes for this category.
     * @param view
     */
    void refreshRecipes(View view) {
        // Empty recipe list.
        recipeList.clear();
        // Add all recipes in this category to recipe list.
        ArrayList<Recipe> rList = selectedCategory.getRecipeList();
        recipeList.addAll(rList);
        // Update adapter.
        adapter.notifyDataSetChanged();
    }

    /**
     * Add custom menu items to action bar.
     *
     * @param menu automatically generated menu
     * @return if action was successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Run method based on which menu item was selected.
     *
     * @param item menu item selected
     * @return if action was successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                editPressed();
                break;
            case R.id.delete:
                deletePressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates popup for entering new category name.
     */
    void editPressed() {
        // Open popup dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Category Name");

        // Text input field, set to current category's name.
        final EditText input = new EditText(this);
        input.setText(selectedCategory.getName());
        builder.setView(input);

        // Bind 'done' button to update categories name, and update toolbar text.
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategory.setName(input.getText().toString());
                toolbar.setTitle(input.getText().toString());
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

    /**
     * Creates popup for confirming deletion.
     */
    void deletePressed() {
        // Open popup dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Really Delete Category?");
        builder.setMessage("This cannot be undone.");

        // Bind 'delete' button to remove category from database, and switch back to the main activity.
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategory.delete();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
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
}
